package tech.nmhillusion.n2mix.helper.database.query;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

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

    public DatabaseHelper(DataSource mDataSource, SessionFactory mSessionFactory) {
        this.mDataSource = mDataSource;
        this.mSessionFactory = mSessionFactory;
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
        return new DatabaseExecutor(sessionFactory, _dataSource);
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
