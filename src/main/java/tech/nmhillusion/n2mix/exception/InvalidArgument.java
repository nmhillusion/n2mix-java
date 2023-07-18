package tech.nmhillusion.n2mix.exception;

/**
 * date: 2023-03-05
 * <p>
 * created-by: nmhillusion
 */

public class InvalidArgument extends Exception {
    public InvalidArgument() {
    }

    public InvalidArgument(String message) {
        super(message);
    }

    public InvalidArgument(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidArgument(Throwable cause) {
        super(cause);
    }

    public InvalidArgument(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
