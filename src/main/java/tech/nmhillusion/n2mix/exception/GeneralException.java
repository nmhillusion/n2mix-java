package tech.nmhillusion.n2mix.exception;

import tech.nmhillusion.n2mix.constant.ErrorType;

/**
 * date: 2022-11-19
 * <p>
 * created-by: nmhillusion
 */

public class GeneralException extends Exception {
    private final ErrorType errorType;
    
    
    public GeneralException(String message) {
        this(ErrorType.GENERAL_ERROR, message);
    }
    
    public GeneralException(Throwable throwable) {
        this(ErrorType.GENERAL_ERROR, throwable);
    }
    
    public GeneralException(String message, Throwable cause) {
        this(ErrorType.GENERAL_ERROR, message, cause);
    }
    
    public GeneralException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }
    
    public GeneralException(ErrorType errorType, Throwable throwable) {
        super(throwable);
        this.errorType = errorType;
    }
    
    public GeneralException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
}
