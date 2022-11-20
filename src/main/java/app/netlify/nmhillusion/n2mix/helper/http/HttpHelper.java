package app.netlify.nmhillusion.n2mix.helper.http;

import app.netlify.nmhillusion.n2mix.exception.ApiResponseException;
import app.netlify.nmhillusion.n2mix.model.ApiErrorResponse;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import javax.net.ssl.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Map;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLog;

public class HttpHelper {
    private static final Duration defaultTimeout = Duration.ofSeconds(60_000);
    private static final OkHttpClient __client = getDefaultOkHttpClient();
    private static final OkHttpClient __unsafeClient = getUnsafeOkHttpClient();

    public static String buildParamsStringFromMap(Map<String, String> params) {
        String data = "";
        StringBuilder stringBuilder = new StringBuilder();
        params.forEach((key, value) -> {
            try {
                String strValue = String.valueOf(value);
                String encodedValue = null == value ? "" : URLEncoder.encode(strValue, "UTF-8");
                stringBuilder.append(key).append("=").append(encodedValue).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
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
            getLog(HttpHelper.class).error(e);
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
        OkHttpClient client;
        if (!trustAllCertificates) {
            client = __client;
        } else {
            client = __unsafeClient;
        }

        try (Response response = client.newCall(httpBuilder.buildPost()).execute()) {
            ResponseBody responseBody = response.body();
            if (null == responseBody) {
                throw new ApiResponseException(
                        new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Lỗi trao đổi dữ liệu server",
                                "Trao đổi dữ liệu giữa các server không thành công, vui lòng thử lại!"));
            } else if (response.isSuccessful()) {
                return responseBody.bytes();
            } else {
                String rawResponse = responseBody.string();
                getLog(HttpHelper.class).debug("rawResponse: " + rawResponse);
                if (!rawResponse.isEmpty()) {
                    JSONObject errorBody = new JSONObject(rawResponse);
                    throw new ApiResponseException(
                            new ApiErrorResponse(
                                    HttpStatus.valueOf(errorBody.getInt("status")),
                                    errorBody.getString("error"),
                                    errorBody.getString("message")));
                } else {
                    throw new ApiResponseException(
                            new ApiErrorResponse(
                                    HttpStatus.valueOf(response.code()),
                                    response.message(),
                                    response.message()));
                }
            }
        }
    }

    public byte[] get(RequestHttpBuilder httpBuilder) throws Exception {
        return get(httpBuilder, false);
    }

    public byte[] get(RequestHttpBuilder httpBuilder, boolean trustAllCertificates) throws Exception {
        OkHttpClient client;
        if (!trustAllCertificates) {
            client = __client;
        } else {
            client = __unsafeClient;
        }

        try (Response response = client.newCall(httpBuilder.buildGet()).execute()) {
            ResponseBody responseBody = response.body();
            if (null == responseBody) {
                throw new ApiResponseException(
                        new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Lỗi trao đổi dữ liệu server",
                                "Trao đổi dữ liệu giữa các server không thành công, vui lòng thử lại!"));
            } else if (response.isSuccessful()) {
                return responseBody.bytes();
            } else {
                String rawResponse = responseBody.string();
                getLog(HttpHelper.class).debug("rawResponse: " + rawResponse);
                if (!rawResponse.isEmpty()) {
                    JSONObject errorBody = new JSONObject(rawResponse);
                    throw new ApiResponseException(
                            new ApiErrorResponse(
                                    HttpStatus.valueOf(errorBody.getInt("status")),
                                    errorBody.getString("error"),
                                    errorBody.getString("message")));
                } else {
                    throw new ApiResponseException(
                            new ApiErrorResponse(
                                    HttpStatus.valueOf(response.code()),
                                    response.message(),
                                    response.message()));
                }
            }
        }
    }
}
