package tech.nmhillusion.n2mix.helper.database.query;

import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * Base class of the database executors.
 *
 * <p><b>Transaction contract.</b> One {@code doWork} / {@code doReturningWork} call is one unit of
 * work: the executor begins a transaction, commits when the callback completes normally, and rolls
 * back when the callback throws. A rollback failure is attached to the original throwable as a
 * suppressed exception instead of replacing it.</p>
 *
 * <p>A call does not join an ambient Hibernate transaction. The JDBC implementation joins a Spring
 * transaction bound to the same {@code DataSource} (propagation {@code REQUIRED}), because its
 * transaction is created through Spring's {@code TransactionTemplate}.</p>
 *
 * <p>date: 2023-06-03
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
