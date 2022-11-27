package app.netlify.nmhillusion.n2mix.helper.http;

import app.netlify.nmhillusion.n2mix.constant.OkHttpContentType;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static app.netlify.nmhillusion.n2mix.constant.OkHttpContentType.*;

/**
 * date: 2020-08-24
 * created-by: minguy1
 */

public class RequestHttpBuilder {
    private final Request.Builder requestBuilder;
    private final Map<String, String> params = new HashMap<>();
    private final Map<String, String> headerMap = new HashMap<>();
    private String url;
    private OkHttpContentType mediaType;
    private Map<String, Object> requestBodyData = new HashMap<>();

    public RequestHttpBuilder() {
        requestBuilder = new Request.Builder();
    }

    public String getUrl() {
        return url;
    }

    public RequestHttpBuilder addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public RequestHttpBuilder addAllParams(Map<String, String> addAllParams) {
        params.putAll(addAllParams);
        return this;
    }

    public RequestHttpBuilder addHeader(String key, String value) {
        headerMap.put(key, value);
        return this;
    }

    public RequestHttpBuilder addHeaders(Map<String, String> headers) {
        headerMap.putAll(headers);
        return this;
    }

    /**
     * Default MediaType: X_WWW_FORM_URLENCODED
     *
     * @param requestBodyData
     */
    public RequestHttpBuilder setBody(Map<String, Object> requestBodyData) {
        return setBody(requestBodyData, X_WWW_FORM_URLENCODED);
    }

    public RequestHttpBuilder setBody(Map<String, Object> requestBodyData, OkHttpContentType mediaType) {
        this.requestBodyData = requestBodyData;
        this.mediaType = mediaType;
        return this;
    }

    public RequestHttpBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    private void combinedUrlAndBody() {
        if (!X_WWW_FORM_URLENCODED.equals(mediaType)) {
            String paramsString = HttpHelper.buildParamsStringFromMap(params);
            url += "?" + paramsString;
        } else {
            requestBodyData.putAll(params);
        }
    }

    private RequestBody buildRequestBody() {
        String data = "";
        if (X_WWW_FORM_URLENCODED.equals(mediaType)) {
            Map<String, String> params = new HashMap<String, String>();
            for (String key : requestBodyData.keySet()) {
                params.put(key, String.valueOf(key));
            }
            data = HttpHelper.buildParamsStringFromMap(params);
            return RequestBody.create(data, mediaType.getValue());
        } else if (JSON.equals(mediaType)) {
            data = new JSONObject(requestBodyData).toString();
            return RequestBody.create(data, mediaType.getValue());
        } else if (FORM_DATA.equals(mediaType)) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            for (String keyName : requestBodyData.keySet()) {
                Object value = requestBodyData.getOrDefault(keyName, "");
                if (value instanceof byte[]) {
                    builder.addFormDataPart(keyName, keyName + ".dat",
                            RequestBody.create((byte[]) value, mediaType.getValue()));
                } else {
                    builder.addFormDataPart(keyName, String.valueOf(value));
                }
            }
            return builder.build();
        } else {
            return RequestBody.create(data, mediaType.getValue());
        }
    }

    public Request buildPost() {
        combinedUrlAndBody();

        return requestBuilder
                .url(url)
                .headers(Headers.of(headerMap))
                .post(buildRequestBody())
                .build();
    }

    public Request buildGet() {
        combinedUrlAndBody();

        return requestBuilder
                .url(url)
                .headers(Headers.of(headerMap))
                .get()
                .build();
    }
}
