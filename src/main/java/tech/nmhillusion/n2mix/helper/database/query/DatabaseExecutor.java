package tech.nmhillusion.n2mix.helper.database.query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableReturnType;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import javax.sql.DataSource;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class DatabaseExecutor {
    private final DataSource dataSource;
    private final SessionFactory sessionFactory;

    public DatabaseExecutor(SessionFactory sessionFactory, DataSource dataSource) {
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    public <T> T doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        return doReturningWork(this.sessionFactory, func);
    }

    public <T> T doReturningWork(SessionFactory _sessionFactory, ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        try (final Session session = _sessionFactory.openSession()) {
            final ThrowableReturnType<T> returnTypeResult = new ThrowableReturnType<>();
            try (final StatementExecutor connectionWrapper = new StatementExecutor(dataSource, session)) {
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
    }

    public void doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        doWork(this.sessionFactory, func);
    }

    public void doWork(SessionFactory _sessionFactory, ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        try (final Session session = _sessionFactory.openSession()) {
            final ThrowableReturnType<Void> returnTypeResult = new ThrowableReturnType<>();
            try (final StatementExecutor connectionWrapper = new StatementExecutor(dataSource, session)) {
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
}
