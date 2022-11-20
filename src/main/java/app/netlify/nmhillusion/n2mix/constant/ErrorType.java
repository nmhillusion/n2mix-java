package app.netlify.nmhillusion.n2mix.constant;

/**
 * date: 2021-07-28
 * created-by: nmhillusion
 */

public enum ErrorType {
    GENERAL_ERROR("GENERAL_ERROR", 400),
    RUNTIME_ERROR("RUNTIME_ERROR", 400),
    OPERATION_ERROR("OPERATION_ERROR", 403),
    PRIVILEGE_ERROR("PRIVILEGE_ERROR", 403),
    INTERNAL_ERROR("INTERNAL_ERROR", 500),
    AUTH_EXPIRED_TOKEN_ERROR("AUTH_EXPIRED_TOKEN_ERROR", 401),
    AUTH_BAD_TOKEN_ERROR("AUTH_BAD_TOKEN_ERROR", 401),
    AUTH_GENERIC_ERROR("AUTH_GENERIC_ERROR", 401),
    SQL_ERROR("SQL_ERROR", 400);

    final String errorName;
    final int errorCode;

    ErrorType(String errorName, int errorCode) {
        this.errorName = errorName;
        this.errorCode = errorCode;
    }

    public String getErrorName() {
        return errorName;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
