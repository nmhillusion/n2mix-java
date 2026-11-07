package tech.nmhillusion.n2mix.helper.database.query;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.hibernate.SessionFactory;
import tech.nmhillusion.n2mix.exception.InvalidArgument;
import tech.nmhillusion.n2mix.helper.database.query.executor.HibernateSessionDatabaseExecutor;
import tech.nmhillusion.n2mix.helper.database.query.executor.JdbcTemplateDatabaseExecutor;
import tech.nmhillusion.n2mix.helper.log.LogHelper;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * Database Helper
 * <p>
 * <i>Default implementation of Internal Database</i>
 */
public class DatabaseHelper {
    private static final long POOL_WARNING_INTERVAL_MS = 30_000L;

    private final DataSource mDataSource;

    private final SessionFactory mSessionFactory;

    private final Class<? extends DatabaseExecutor> classOfExecutor;

    private final AtomicLong lastPoolWarningTime$ = new AtomicLong(0L);

    public DatabaseHelper(DataSource mDataSource, SessionFactory mSessionFactory) throws InvalidArgument {
        this(mDataSource, mSessionFactory, HibernateSessionDatabaseExecutor.class);
    }

    public DatabaseHelper(DataSource mDataSource, SessionFactory mSessionFactory, Class<? extends DatabaseExecutor> classOfExecutor) throws InvalidArgument {
        if (null == mDataSource) {
            throw new InvalidArgument("DataSource must not be null");
        }
        if (null == classOfExecutor) {
            throw new InvalidArgument("Class of executor must not be null");
        }

        LogHelper.getLogger(this).info("Register database executor for " + classOfExecutor);

        this.mDataSource = mDataSource;
        this.mSessionFactory = mSessionFactory;
        this.classOfExecutor = classOfExecutor;
    }

    public DatabaseExecutor getExecutor() {
        return getExecutor(this.mSessionFactory);
    }

    public DatabaseExecutor getExecutor(SessionFactory sessionFactory) {
        return getExecutor(sessionFactory, mDataSource);
    }

    public DatabaseExecutor getExecutor(SessionFactory sessionFactory, DataSource _dataSource) {
        if (null == _dataSource) {
            throw new IllegalArgumentException("DataSource must not be null");
        }

        if (HibernateSessionDatabaseExecutor.class.isAssignableFrom(classOfExecutor)) {
            if (null == sessionFactory) {
                throw new IllegalArgumentException("SessionFactory must not be null when using " + classOfExecutor.getName());
            }
            return new HibernateSessionDatabaseExecutor(sessionFactory);
        } else if (JdbcTemplateDatabaseExecutor.class.isAssignableFrom(classOfExecutor)) {
            return new JdbcTemplateDatabaseExecutor(_dataSource);
        } else {
            throw new RuntimeException("Class of executor is not supported: " + classOfExecutor);
        }
    }


    private void warningLargeConnections() {
        final long now = System.currentTimeMillis();
        final long last = lastPoolWarningTime$.get();
        if (now - last < POOL_WARNING_INTERVAL_MS
                || !lastPoolWarningTime$.compareAndSet(last, now)) {
            return;
        }

        for (DataSource iDataSource : Collections.singletonList(mDataSource)) {
            doWarningLargeConnections(iDataSource);
        }
    }

    private void doWarningLargeConnections(DataSource _dataSource) {
        if (_dataSource instanceof HikariDataSource hikariDataSource) {
            final HikariPoolMXBean hikariPoolMXBean = hikariDataSource.getHikariPoolMXBean();

            if (null != hikariPoolMXBean &&
                    hikariPoolMXBean.getActiveConnections() > 20
            ) {
                getLogger(this).warn("[dataSource] Pool Name: " + hikariDataSource.getPoolName());
                final String dataSourceInfo =
                        "[dataConnection] activeConnections: " + hikariPoolMXBean.getActiveConnections() +
                                "\tidle connections: " + hikariPoolMXBean.getIdleConnections() +
                                "\ttotal: " + hikariPoolMXBean.getTotalConnections() +
                                "\twaiting threads: " + hikariPoolMXBean.getThreadsAwaitingConnection() +
                                "\tpool size: " + hikariDataSource.getMaximumPoolSize();
                getLogger(this).warn(dataSourceInfo);
            }
        }
    }

}
