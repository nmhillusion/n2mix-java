package app.netlify.nmhillusion.n2mix.type.function;

import java.io.Serializable;

/**
 * date: 2021-05-12
 * created-by: nmhillusion
 */

public class ThrowableReturnType<T> implements Serializable {
    private boolean hasError;
    private T data;
    private Throwable exception;

    public boolean getHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
