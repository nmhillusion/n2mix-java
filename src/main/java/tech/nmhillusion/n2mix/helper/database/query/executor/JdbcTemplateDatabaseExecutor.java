package tech.nmhillusion.n2mix.helper.database.query.executor;

import org.springframework.jdbc.core.JdbcTemplate;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseExecutor;
import tech.nmhillusion.n2mix.helper.database.query.StatementExecutor;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableReturnType;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import javax.sql.DataSource;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-16
 */
public class JdbcTemplateDatabaseExecutor extends DatabaseExecutor {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateDatabaseExecutor(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T __doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        final ThrowableReturnType<T> returnTypeResult = new ThrowableReturnType<>();
        try (final StatementExecutor connectionWrapper = new StatementExecutor(dataSource, jdbcTemplate)) {
            returnTypeResult.setHasError(false);
            returnTypeResult.setData(func.throwableApply(connectionWrapper));
        } catch (Throwable ex) {
            returnTypeResult.setHasError(true);
            returnTypeResult.setException(ex);
        }

        if (!returnTypeResult.getHasError()) {
            return returnTypeResult.getData();
        } else {
            throw returnTypeResult.getException();
        }

    }

    @Override
    public void __doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        final ThrowableReturnType<Void> returnTypeResult = new ThrowableReturnType<>();
        try (final StatementExecutor connectionWrapper = new StatementExecutor(dataSource, jdbcTemplate)) {
            returnTypeResult.setHasError(false);
            func.throwableVoidApply(connectionWrapper);
        } catch (Throwable ex) {
            returnTypeResult.setHasError(true);
            returnTypeResult.setException(ex);
        }

        if (returnTypeResult.getHasError()) {
            throw returnTypeResult.getException();
        }
    }
}
