package app.netlify.nmhillusion.n2mix.exception;

/**
 * date: 2020-11-24
 * created-by: nmhillusion
 */

public class OutOfDateCachedException extends Exception {
    public OutOfDateCachedException(String message) {
        super(message);
    }

    public OutOfDateCachedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
