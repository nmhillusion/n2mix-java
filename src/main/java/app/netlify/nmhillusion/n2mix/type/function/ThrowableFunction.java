package app.netlify.nmhillusion.n2mix.type.function;

/**
 * date: 2021-04-16
 * created-by: nmhillusion
 */

public interface ThrowableFunction<T, R> {
    R throwableApply(T t) throws Throwable;
}
