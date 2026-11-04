package tech.nmhillusion.n2mix.helper.database.query.executor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseExecutor;
import tech.nmhillusion.n2mix.helper.database.query.StatementExecutor;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import java.util.Objects;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-16
 */
public class HibernateSessionDatabaseExecutor extends DatabaseExecutor {
    private final SessionFactory sessionFactory;

    public HibernateSessionDatabaseExecutor(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory, "SessionFactory must not be null");
    }

    @Override
    protected <T> T __doReturningWork(ThrowableFunction<StatementExecutor, T> func) throws Throwable {
        try (final Session session = this.sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            try {
                final T result;
                try (final StatementExecutor statementExecutor = new StatementExecutor(session)) {
                    result = func.throwableApply(statementExecutor);
                }
                transaction.commit();
                return result;
            } catch (Throwable primary) {
                rollbackSafely(transaction, primary);
                throw primary;
            }
        }
    }

    @Override
    protected void __doWork(ThrowableVoidFunction<StatementExecutor> func) throws Throwable {
        try (final Session session = this.sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            try {
                try (final StatementExecutor statementExecutor = new StatementExecutor(session)) {
                    func.throwableVoidApply(statementExecutor);
                }
                transaction.commit();
            } catch (Throwable primary) {
                rollbackSafely(transaction, primary);
                throw primary;
            }
        }
    }

    private static void rollbackSafely(Transaction transaction, Throwable primary) {
        try {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Throwable rollbackFailure) {
            if (rollbackFailure != primary) {
                primary.addSuppressed(rollbackFailure);
            }
        }
    }
}
