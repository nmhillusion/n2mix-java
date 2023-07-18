package tech.nmhillusion.n2mix.constant;

import okhttp3.MediaType;

/**
 * date: 2022-11-20
 * <p>
 * created-by: nmhillusion
 */

public enum OkHttpContentType {
    X_WWW_FORM_URLENCODED(ContentType.FORM_URLENCODED),
    FORM_DATA(ContentType.FORM_DATA),
    JSON(ContentType.JSON);

    private final MediaType value;

    OkHttpContentType(String rawValue) {
        this.value = MediaType.get(rawValue);
    }

    public MediaType getValue() {
        return value;
    }
}
