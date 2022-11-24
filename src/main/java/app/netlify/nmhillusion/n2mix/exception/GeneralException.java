package app.netlify.nmhillusion.n2mix.exception;

import app.netlify.nmhillusion.n2mix.model.ApiErrorResponse;

/**
 * date: 2022-11-19
 * <p>
 * created-by: nmhillusion
 */

public class GeneralException extends Exception {

    public GeneralException(String message) {
        super(message);
    }

    public GeneralException(Throwable throwable) {
        super(throwable);
    }

    public GeneralException(ApiErrorResponse errorResponse) {
        super(errorResponse.getErrorName());
    }

    public GeneralException(ApiErrorResponse errorResponse, Throwable throwable) {
        super(errorResponse.getErrorName(), throwable);
    }
}
