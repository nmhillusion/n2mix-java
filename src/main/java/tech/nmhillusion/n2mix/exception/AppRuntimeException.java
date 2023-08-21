package tech.nmhillusion.n2mix.exception;

/**
 * date: 2023-08-21
 * <p>
 * created-by: nmhillusion
 */

public class AppRuntimeException extends RuntimeException {
    
    public AppRuntimeException() {
    }
    
    public AppRuntimeException(String message) {
        super(message);
    }
    
    public AppRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AppRuntimeException(Throwable cause) {
        super(cause);
    }
    
    public AppRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
