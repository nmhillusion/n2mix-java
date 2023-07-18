package tech.nmhillusion.n2mix.helper.http;

import tech.nmhillusion.n2mix.exception.ApiResponseException;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.model.ApiErrorResponse;
import tech.nmhillusion.n2mix.validator.StringValidator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.net.ssl.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

public class HttpHelper {
    private static final Duration defaultTimeout = Duration.ofSeconds(60_000);
    private static final OkHttpClient __client = getDefaultOkHttpClient();
    private static final List<X509Certificate> ACCEPTED_UNSAFE_CERTIFICATE_LIST = new ArrayList<>();
    private static final OkHttpClient __unsafeClient = getUnsafeOkHttpClient();

    public static void addAcceptedUnsafeCertificate(X509Certificate certificate) {
        ACCEPTED_UNSAFE_CERTIFICATE_LIST.add(certificate);
    }

    public static String buildParamsStringFromMap(Map<String, String> params) {
        String data = "";
        StringBuilder stringBuilder = new StringBuilder();
        params.forEach((key, value) -> {
            String strValue = String.valueOf(value);
            String encodedValue = null == value ? "" : URLEncoder.encode(strValue, StandardCharsets.UTF_8);
            stringBuilder.append(key).append("=").append(encodedValue).append("&");
        });

        if (0 < stringBuilder.length()) {
            data = stringBuilder.substring(0, stringBuilder.length() - 1);
        }

        return data;
    }

    private static OkHttpClient getDefaultOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(defaultTimeout)
                .readTimeout(defaultTimeout)
                .writeTimeout(defaultTimeout)
                .build();
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        private void throwIfInvalidCertificateOfException(X509Certificate[] chain) throws CertificateException {
                            if (
                                    Arrays.stream(chain)
                                            .noneMatch(it ->
                                                    ACCEPTED_UNSAFE_CERTIFICATE_LIST.stream()
                                                            .anyMatch(accIt -> accIt.getIssuerX500Principal().equals(it.getIssuerX500Principal()))
                                            )
                                            && !ACCEPTED_UNSAFE_CERTIFICATE_LIST.isEmpty()
                            ) {
                                throw new CertificateException("This certificate is not invalid: " + Arrays.toString(chain));
                            }
                        }

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                            throwIfInvalidCertificateOfException(chain);
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                            throwIfInvalidCertificateOfException(chain);
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return ACCEPTED_UNSAFE_CERTIFICATE_LIST.toArray(new X509Certificate[0]);
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .connectTimeout(defaultTimeout)
                    .readTimeout(defaultTimeout)
                    .writeTimeout(defaultTimeout);

            return builder.build();
        } catch (Exception e) {
            LogHelper.getLogger(HttpHelper.class).error(e);
            throw new ApiResponseException(e);
        }
    }

    /**
     * Default must be verify certificates
     */
    public byte[] post(RequestHttpBuilder httpBuilder) throws Exception {
        return post(httpBuilder, false);
    }

    public byte[] post(RequestHttpBuilder httpBuilder, boolean trustAllCertificates) throws Exception {
        return httpExecute(httpBuilder, HttpMethod.POST, trustAllCertificates);
    }

    public byte[] get(RequestHttpBuilder httpBuilder) throws Exception {
        return get(httpBuilder, false);
    }

    public byte[] get(RequestHttpBuilder httpBuilder, boolean trustAllCertificates) throws Exception {
        return httpExecute(httpBuilder, GET, trustAllCertificates);
    }

    private Request buildRequestFromMethod(HttpMethod httpMethod, RequestHttpBuilder requestHttpBuilder) {
        Request okRequest = null;

        if (httpMethod.equals(GET)) {
            okRequest = requestHttpBuilder.buildGet();
        } else if (httpMethod.equals(POST)) {
            okRequest = requestHttpBuilder.buildPost();
        }

        return okRequest;
    }

    public byte[] httpExecute(RequestHttpBuilder httpBuilder, HttpMethod httpMethod, boolean trustAllCertificates) throws Exception {
        OkHttpClient client;
        if (!trustAllCertificates) {
            client = __client;
        } else {
            client = __unsafeClient;
        }

        try (Response response = client.newCall(buildRequestFromMethod(httpMethod, httpBuilder)).execute()) {
            ResponseBody responseBody = response.body();
            if (null == responseBody) {
                throw new ApiResponseException(
                        new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Internal Server Error",
                                httpBuilder.getUrl() + ", method: " + httpMethod + ", responseBody is null"));
            } else if (response.isSuccessful()) {
                return responseBody.bytes();
            } else {
                String rawResponse = responseBody.string();
                LogHelper.getLogger(this).debug(httpBuilder.getUrl() + " -> rawResponse: " + rawResponse + "; httpResponse: " + response);
                if (!StringValidator.isBlank(rawResponse)) {
                    JSONObject errorBody = new JSONObject(rawResponse);
                    throw new ApiResponseException(
                            new ApiErrorResponse(
                                    HttpStatus.valueOf(errorBody.getInt("status")),
                                    errorBody.getString("error"),
                                    httpBuilder.getUrl() + ", method: " + httpMethod + ",code: " + response.code() + "," + errorBody.getString("message")));
                } else {
                    throw new ApiResponseException(
                            new ApiErrorResponse(
                                    HttpStatus.valueOf(response.code()),
                                    response.message(),
                                    httpBuilder.getUrl() + ", method: " + httpMethod + ",code: " + response.code() + "," + response.message()));
                }
            }
        }
    }
}
