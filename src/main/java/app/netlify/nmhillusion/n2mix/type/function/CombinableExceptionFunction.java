package app.netlify.nmhillusion.n2mix.type.function;

import app.netlify.nmhillusion.n2mix.type.Pair;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * date: 2021-07-29
 * <p>
 * created-by: nmhillusion
 */

public class CombinableExceptionFunction {
    private final List<Throwable> exceptionList = new ArrayList<>();

    public <T> CombinableExceptionFunction pipeVoid(@NonNull ThrowableVoidFunction<T> func, T input) {
        try {
            func.throwableVoidApply(input);
        } catch (Throwable e) {
            exceptionList.add(e);
        }
        return this;
    }

    public <T, R> Pair<CombinableExceptionFunction, R> pipeReturn(@NonNull ThrowableFunction<T, R> func, T input) {
        R value = null;
        try {
            value = func.throwableApply(input);
        } catch (Throwable e) {
            exceptionList.add(e);
        }
        return new Pair<>(this, value);
    }

    public List<Throwable> completeAndCollectExceptions() {
        return exceptionList;
    }
}
