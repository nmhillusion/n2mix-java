package app.netlify.nmhillusion.n2mix.type.function;

public interface ThrowableVoidFunction<T> {
    void throwableVoidApply(T t) throws Throwable;
}
