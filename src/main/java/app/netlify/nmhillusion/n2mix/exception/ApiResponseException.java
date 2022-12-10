package app.netlify.nmhillusion.n2mix.exception;

import app.netlify.nmhillusion.n2mix.model.ApiErrorResponse;
import org.springframework.http.HttpStatus;

public class ApiResponseException extends RuntimeException {
    private final ApiErrorResponse errorResponse;

    public ApiResponseException(String message) {
        super(message);
        this.errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, "GENERAL_EXCEPTION", message);
    }

    public ApiResponseException(Throwable throwable) {
        super(throwable);
        this.errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, throwable.getStackTrace()[1].getClassName(), throwable.getMessage());
    }

    public ApiResponseException(ApiErrorResponse errorResponse) {
        super(errorResponse.toString());
        this.errorResponse = errorResponse;
    }

    public ApiResponseException(ApiErrorResponse errorResponse, Throwable throwable) {
        super(errorResponse.getErrorName(), throwable);
        this.errorResponse = errorResponse;
    }

    public ApiErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
