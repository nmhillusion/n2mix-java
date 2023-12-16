package tech.nmhillusion.n2mix.helper.database.query;

import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public abstract class DatabaseExecutor {
    public final <T> T doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        if (getLogger(this).isDebugEnabled()) {
            getLogger(this).debug("execute doReturningWork on " + getClass().getName());
        }
        return __doReturningWork(func);
    }

    protected abstract <T> T __doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable;

    public final void doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        if (getLogger(this).isDebugEnabled()) {
            getLogger(this).debug("execute doWork on " + getClass().getName());
        }
        __doWork(func);
    }

    protected abstract void __doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable;

}
