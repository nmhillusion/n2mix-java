package app.netlify.nmhillusion.n2mix.exception;

/**
 * date: 2020-12-24
 * created-by: nmhillusion
 */

public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
