package tech.nmhillusion.n2mix.helper.database.query;

import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.nmhillusion.n2mix.helper.database.query.callback.CallableStatementCallback;
import tech.nmhillusion.n2mix.helper.database.query.callback.NoReturnCallableStatementCallback;
import tech.nmhillusion.n2mix.helper.database.query.callback.NoReturnPreparedStatementCallback;
import tech.nmhillusion.n2mix.helper.database.query.callback.PreparedStatementCallback;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.function.ThrowableNoInputFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidNoInputFunction;
import tech.nmhillusion.n2mix.util.CastUtil;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-01
 */
public class StatementExecutor implements Closeable {
    public static final String RESULT_PARAM_NAME = "result";
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private Session session = null;

    public StatementExecutor(DataSource dataSource,
                             Session session) {
        this.dataSource = dataSource;
        this.session = session;
        this.jdbcTemplate = new JdbcTemplate(dataSource, true);
    }

    private <T extends Throwable> T getExceptionFromStacktrace(Throwable ex, Class<T> classExceptionToFind, T defaultValueIfNotFound) {
        if (classExceptionToFind.isInstance(ex)) {
            return CastUtil.safeCast(ex, classExceptionToFind);
        }

        final Throwable[] suppressedList = ex.getSuppressed();
        for (Throwable throwable_ : suppressedList) {
            if (classExceptionToFind.isInstance(throwable_)) {
                return CastUtil.safeCast(throwable_, classExceptionToFind);
            }
        }

        return defaultValueIfNotFound;
    }

    private <T> T wrapperToSqlExceptionReturning(ThrowableNoInputFunction<T> func) throws SQLException {
        try {
            return func.apply();
        } catch (Throwable ex) {
            throw getExceptionFromStacktrace(ex, SQLException.class, new SQLException(ex));
        }
    }

    private void wrapperToSqlException(ThrowableVoidNoInputFunction func) throws SQLException {
        try {
            func.apply();
        } catch (Throwable ex) {
            throw getExceptionFromStacktrace(ex, SQLException.class, new SQLException(ex));
        }
    }

    public void doPreparedStatement(String sql, NoReturnPreparedStatementCallback callback_) throws SQLException {
        wrapperToSqlException(() -> {
            if (null != session) {
                session.doWork(_conn -> callback_.apply(_conn.prepareStatement(sql)));
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    public <T> T doReturningPreparedStatement(String sql, PreparedStatementCallback<T> callback_) throws SQLException {
        return wrapperToSqlExceptionReturning(() -> {
            if (null != session) {
                return session.doReturningWork(_conn -> callback_.apply(_conn.prepareStatement(sql)));
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    public <T> T doReturningCallableStatement(String callQuery, int returnType, CallableStatementCallback<T> callableStatementCallback) throws SQLException {
        return wrapperToSqlExceptionReturning(() -> {
            if (null != session) {
                return session.doReturningWork(_conn -> {
                    final CallableStatement _callableStatement = _conn.prepareCall(
                            "{ ? = call " + callQuery + " } ");
                    _callableStatement.registerOutParameter(1, returnType);

                    return callableStatementCallback.apply(_callableStatement);
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    public void doCallableStatement(String callQuery, int returnType, NoReturnCallableStatementCallback callableStatementCallback) throws SQLException {
        wrapperToSqlException(() -> {
            if (null != session) {
                session.doWork(_conn -> {
                    final CallableStatement _callableStatement = _conn.prepareCall(
                            "{ ? = call " + callQuery + " } ");
                    _callableStatement.registerOutParameter(1, returnType);

                    callableStatementCallback.apply(_callableStatement);
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    public <T> T doReturningCallableStatementNamed(String callQuery, int returnType, CallableStatementCallback<T> callableStatementCallback) throws SQLException {
        return wrapperToSqlExceptionReturning(() -> {
            if (null != session) {
                return session.doReturningWork(_conn -> {
                    final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
                    if (LogHelper.getLogger(this).isTraceEnabled()) {
                        getLogger(this).trace(queryString + " | returnType: " + returnType);
                    }
                    final CallableStatement _callableStatement = _conn.prepareCall(queryString);
                    _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                    return callableStatementCallback.apply(_callableStatement);
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    public void doCallableStatementNamed(String callQuery, int returnType, NoReturnCallableStatementCallback callableStatementCallback) throws SQLException {
        wrapperToSqlException(() -> {
            if (null != session) {
                session.doWork(_conn -> {
                    final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
                    if (LogHelper.getLogger(this).isTraceEnabled()) {
                        getLogger(this).trace(queryString + " | returnType: " + returnType);
                    }
                    final CallableStatement _callableStatement = _conn.prepareCall(queryString);
                    _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                    callableStatementCallback.apply(_callableStatement);
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    @Override
    public void close() throws IOException {
        if (null != session) {
            session.close();
        }
    }
}
