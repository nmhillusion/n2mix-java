package tech.nmhillusion.n2mix.helper.database.query;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import tech.nmhillusion.n2mix.exception.InvalidArgument;
import tech.nmhillusion.n2mix.helper.database.query.executor.HibernateSessionDatabaseExecutor;

import javax.sql.DataSource;
import java.util.Collections;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * Database Helper
 * <p>
 * <i>Default implementation of Internal Database</i>
 */
public class DatabaseHelper {
    private final DataSource mDataSource;

    private final SessionFactory mSessionFactory;

    private final JdbcTemplate jdbcTemplate;

    private final Class<? extends DatabaseExecutor> classOfExecutor;

    public DatabaseHelper(DataSource mDataSource, SessionFactory mSessionFactory) throws InvalidArgument {
        this(mDataSource, mSessionFactory, HibernateSessionDatabaseExecutor.class);
    }

    public DatabaseHelper(DataSource mDataSource, SessionFactory mSessionFactory, Class<? extends DatabaseExecutor> classOfExecutor) throws InvalidArgument {
        if (null == mDataSource) {
            throw new InvalidArgument("DataSource must not be null");
        }

        this.mDataSource = mDataSource;
        this.mSessionFactory = mSessionFactory;
        this.classOfExecutor = classOfExecutor;
        this.jdbcTemplate = new JdbcTemplate(mDataSource);
    }

    /**
     * @return get worker with Internal Session Factory
     */
    @Deprecated
    public DatabaseWorker getWorker() {
        return getWorker(this.mSessionFactory);
    }

    @Deprecated
    public DatabaseWorker getWorker(SessionFactory sessionFactory) {
        return getWorker(sessionFactory, mDataSource);
    }

    @Deprecated
    public DatabaseWorker getWorker(SessionFactory sessionFactory, DataSource _dataSource) {
        return new DatabaseWorker(sessionFactory, _dataSource, this::closeConnectionWrapper);
    }

    public DatabaseExecutor getExecutor() {
        return getExecutor(this.mSessionFactory);
    }

    public DatabaseExecutor getExecutor(SessionFactory sessionFactory) {
        return getExecutor(sessionFactory, mDataSource);
    }

    public DatabaseExecutor getExecutor(SessionFactory sessionFactory, DataSource _dataSource) {
        if (classOfExecutor.isAssignableFrom(HibernateSessionDatabaseExecutor.class)) {
            return new HibernateSessionDatabaseExecutor(sessionFactory, _dataSource);
        } else {
            throw new RuntimeException("Class of executor is not supported: " + classOfExecutor);
        }
    }


    private void warningLargeConnections() {
//        for (DataSource iDataSource : Arrays.asList(dataSource, mDataSource)) {
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

    public synchronized boolean closeConnectionWrapper(DataSource dataSource, ConnectionWrapper connectionWrapper) {
        boolean result = true;
        try {
            if (null != dataSource &&
                    null != connectionWrapper &&
                    null != connectionWrapper.getConnection()) {
                DataSourceUtils.releaseConnection(connectionWrapper.getConnection(), dataSource);
//                getLog(this).info("__release__connection__");
//                connectionWrapper.getConnection().close();
            }
            warningLargeConnections();
        } catch (Exception ex) {
            getLogger(this).error(ex);
            result = false;
        }
        return result;
    }
}
