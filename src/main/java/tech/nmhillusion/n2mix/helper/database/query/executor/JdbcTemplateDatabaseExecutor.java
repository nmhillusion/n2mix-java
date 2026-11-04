package tech.nmhillusion.n2mix.helper.database.query.executor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseExecutor;
import tech.nmhillusion.n2mix.helper.database.query.StatementExecutor;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-16
 */
public class JdbcTemplateDatabaseExecutor extends DatabaseExecutor {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public JdbcTemplateDatabaseExecutor(DataSource dataSource) {
        Objects.requireNonNull(dataSource, "DataSource must not be null");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Override
    protected <T> T __doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        try {
            return transactionTemplate.execute(status -> {
                try (final StatementExecutor statementExecutor = new StatementExecutor(jdbcTemplate)) {
                    return func.throwableApply(statementExecutor);
                } catch (Throwable ex) {
                    throw new CallbackFailure(ex);
                }
            });
        } catch (CallbackFailure ex) {
            throw ex.getCause();
        }
    }

    @Override
    protected void __doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                try (final StatementExecutor statementExecutor = new StatementExecutor(jdbcTemplate)) {
                    func.throwableVoidApply(statementExecutor);
                } catch (Throwable ex) {
                    throw new CallbackFailure(ex);
                }
            });
        } catch (CallbackFailure ex) {
            throw ex.getCause();
        }
    }

    private static final class CallbackFailure extends RuntimeException {
        private CallbackFailure(Throwable cause) {
            super(null, cause, false, false);
        }
    }
}
