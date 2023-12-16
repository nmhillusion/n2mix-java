package tech.nmhillusion.n2mix.helper.database.query;

import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public interface DatabaseExecutor {
    <T> T doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable;

    void doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable;
}
