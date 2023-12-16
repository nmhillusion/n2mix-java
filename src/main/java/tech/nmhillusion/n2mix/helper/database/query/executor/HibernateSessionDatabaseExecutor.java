package tech.nmhillusion.n2mix.helper.database.query.executor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
public class HibernateSessionDatabaseExecutor implements DatabaseExecutor {
    private final DataSource dataSource;
    private final SessionFactory sessionFactory;

    public HibernateSessionDatabaseExecutor(SessionFactory sessionFactory, DataSource dataSource) {
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    @Override
    public <T> T doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        try (final Session session = this.sessionFactory.openSession()) {
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

    @Override
    public void doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        try (final Session session = this.sessionFactory.openSession()) {
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
