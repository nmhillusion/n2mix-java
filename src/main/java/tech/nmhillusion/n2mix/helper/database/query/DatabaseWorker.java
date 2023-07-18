package tech.nmhillusion.n2mix.helper.database.query;

import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableReturnType;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.sql.DataSource;
import java.util.function.BiFunction;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class DatabaseWorker {
    private final DataSource dataSource;
    private final BiFunction<DataSource, ConnectionWrapper, Boolean> closeFunc;
    private final SessionFactory sessionFactory;

    public DatabaseWorker(SessionFactory sessionFactory, DataSource dataSource, BiFunction<DataSource, ConnectionWrapper, Boolean> closeFunc) {
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
        this.closeFunc = closeFunc;
    }

    public <T> T doReturningWork(ThrowableFunction<ConnectionWrapper, T> func) throws Throwable {
        return doReturningWork(sessionFactory, func);
    }

    //    @Transactional
    public <T> T doReturningWork(SessionFactory _sessionFactory, ThrowableFunction<ConnectionWrapper, T> func) throws Throwable {
        try (final Session session = _sessionFactory.openSession()) {
            final ThrowableReturnType<T> rawResult = session.doReturningWork(connection -> {
                final ThrowableReturnType<T> returnTypeResult = new ThrowableReturnType<>();
                try (final ConnectionWrapper connectionWrapper = new ConnectionWrapper(dataSource,
                        connection, closeFunc)) {
                    returnTypeResult.setHasError(false);
                    returnTypeResult.setData(func.throwableApply(connectionWrapper));
                } catch (Throwable ex) {
                    returnTypeResult.setHasError(true);
                    returnTypeResult.setException(ex);
                }
                return returnTypeResult;
            });

            if (!rawResult.getHasError()) {
                return rawResult.getData();
            } else {
                throw rawResult.getException();
            }
        }
    }

    public void doWork(ThrowableVoidFunction<ConnectionWrapper> func) throws Throwable {
        doWork(sessionFactory, func);
    }

    //    @Transactional
    public void doWork(SessionFactory _sessionFactory, ThrowableVoidFunction<ConnectionWrapper> func) throws Throwable {
        try (final Session session = _sessionFactory.openSession()) {
            final ThrowableReturnType<Void> rawResult = session.doReturningWork(connection -> {
                final ThrowableReturnType<Void> returnTypeResult = new ThrowableReturnType<>();
                try (final ConnectionWrapper connectionWrapper = new ConnectionWrapper(dataSource,
                        connection, closeFunc)) {
                    returnTypeResult.setHasError(false);
                    func.throwableVoidApply(connectionWrapper);
                } catch (Throwable ex) {
                    returnTypeResult.setHasError(true);
                    returnTypeResult.setException(ex);
                }
                return returnTypeResult;
            });

            if (rawResult.getHasError()) {
                throw rawResult.getException();
            }
        }
    }
}
