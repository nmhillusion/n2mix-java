package tech.nmhillusion.n2mix.helper.database.query;

import tech.nmhillusion.n2mix.helper.log.MixLogger;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * Base class of the database executors.
 *
 * <p><b>Transaction contract.</b> A call that does not join an outer transaction is one unit of
 * work: the executor begins a transaction, commits when the callback completes normally, and rolls
 * back when the callback throws. The throwable raised by the callback is always rethrown; a failure
 * while rolling back that call is attached to it as a suppressed exception instead of replacing it.
 * A failure of the commit itself is reported as the backend's commit exception.</p>
 *
 * <p><b>Ambient transactions.</b> A call does not join an ambient Hibernate transaction: the
 * Hibernate implementation always opens its own {@code Session}, and therefore its own database
 * connection, even when the caller is already inside a Spring transaction. The JDBC implementation
 * joins an existing Spring transaction bound to the same {@code DataSource} (propagation
 * {@code REQUIRED}); a failing call marks that shared transaction rollback-only, so the outer
 * transaction cannot commit and throws {@code UnexpectedRollbackException}. Isolation and
 * auto-commit are whatever the {@code DataSource} and driver default to; the executors do not
 * override them.</p>
 *
 * <p>date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public abstract class DatabaseExecutor {
    private final MixLogger logger = getLogger(this);

    /**
     * Execute a database operation that returns a value.
     *
     * <p><b>Throws contract.</b> This method declares {@code throws Throwable} for binary
     * compatibility with existing callers. In practice it throws {@code SQLException} or
     * {@link tech.nmhillusion.n2mix.exception.AppRuntimeException}. The declaration will be
     * narrowed in the next major version.</p>
     */
    public final <T> T doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("execute doReturningWork on " + getClass().getName());
        }
        return __doReturningWork(func);
    }

    protected abstract <T> T __doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable;

    /**
     * Execute a database operation that does not return a value.
     *
     * <p><b>Throws contract.</b> This method declares {@code throws Throwable} for binary
     * compatibility with existing callers. In practice it throws {@code SQLException} or
     * {@link tech.nmhillusion.n2mix.exception.AppRuntimeException}. The declaration will be
     * narrowed in the next major version.</p>
     */
    public final void doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("execute doWork on " + getClass().getName());
        }
        __doWork(func);
    }

    protected abstract void __doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable;

}
