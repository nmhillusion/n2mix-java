package tech.nmhillusion.n2mix.helper.database.query;

import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.nmhillusion.n2mix.helper.database.query.callback.CallableStatementCallback;
import tech.nmhillusion.n2mix.helper.database.query.callback.NoReturnCallableStatementCallback;
import tech.nmhillusion.n2mix.helper.database.query.callback.NoReturnPreparedStatementCallback;
import tech.nmhillusion.n2mix.helper.database.query.callback.PreparedStatementCallback;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-01
 */
public class StatementExecutor {
    public static final String RESULT_PARAM_NAME = "result";
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private Connection connection = null;
    private Session session = null;

    public StatementExecutor(DataSource dataSource,
                             Connection connection) {
        this.dataSource = dataSource;
        this.connection = connection;
        this.jdbcTemplate = new JdbcTemplate(dataSource, true);
    }

    public StatementExecutor(DataSource dataSource,
                             Session session) {
        this.dataSource = dataSource;
        this.session = session;
        this.jdbcTemplate = new JdbcTemplate(dataSource, true);
    }

    public <T> T doReturningPreparedStatement(String sql, PreparedStatementCallback<T> callback_) throws SQLException {
        if (null != session) {
            return session.doReturningWork(_conn -> callback_.apply(_conn.prepareStatement(sql)));
        } else if (null != connection) {
            return callback_.apply(connection.prepareStatement(sql));
        } else {
            throw new SQLException("Does not exist session or connection to execute SQL");
        }
    }

    public void doPreparedStatement(String sql, NoReturnPreparedStatementCallback callback_) throws SQLException {
        if (null != session) {
            session.doWork(_conn -> callback_.apply(_conn.prepareStatement(sql)));
        } else if (null != connection) {
            callback_.apply(connection.prepareStatement(sql));
        } else {
            throw new SQLException("Does not exist session or connection to execute SQL");
        }
    }

    public <T> T doReturningCallableStatement(String callQuery, int returnType, CallableStatementCallback<T> callableStatementCallback) throws SQLException {
        if (null != session) {
            return session.doReturningWork(_conn -> {
                final CallableStatement _callableStatement = _conn.prepareCall(
                        "{ ? = call " + callQuery + " } ");
                _callableStatement.registerOutParameter(1, returnType);

                return callableStatementCallback.apply(_callableStatement);
            });
        } else if (null != connection) {
            final CallableStatement _callableStatement = connection.prepareCall(
                    "{ ? = call " + callQuery + " } ");
            _callableStatement.registerOutParameter(1, returnType);

            return callableStatementCallback.apply(_callableStatement);
        } else {
            throw new SQLException("Does not exist session or connection to execute SQL");
        }
    }

    public void doCallableStatement(String callQuery, int returnType, NoReturnCallableStatementCallback callableStatementCallback) throws SQLException {
        if (null != session) {
            session.doWork(_conn -> {
                final CallableStatement _callableStatement = _conn.prepareCall(
                        "{ ? = call " + callQuery + " } ");
                _callableStatement.registerOutParameter(1, returnType);

                callableStatementCallback.apply(_callableStatement);
            });
        } else if (null != connection) {
            final CallableStatement _callableStatement = connection.prepareCall(
                    "{ ? = call " + callQuery + " } ");
            _callableStatement.registerOutParameter(1, returnType);

            callableStatementCallback.apply(_callableStatement);
        } else {
            throw new SQLException("Does not exist session or connection to execute SQL");
        }
    }

    public <T> T doReturningCallableStatementNamed(String callQuery, int returnType, CallableStatementCallback<T> callableStatementCallback) throws SQLException {
        if (null != session) {
            return session.doReturningWork(_conn -> {
                final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
                getLogger(this).trace(queryString + " | returnType: " + returnType);
                final CallableStatement _callableStatement = _conn.prepareCall(queryString);
                _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                return callableStatementCallback.apply(_callableStatement);
            });
        } else if (null != connection) {
            final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
            getLogger(this).trace(queryString + " | returnType: " + returnType);
            final CallableStatement _callableStatement = connection.prepareCall(queryString);
            _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

            return callableStatementCallback.apply(_callableStatement);
        } else {
            throw new SQLException("Does not exist session or connection to execute SQL");
        }
    }

    public void doCallableStatementNamed(String callQuery, int returnType, NoReturnCallableStatementCallback callableStatementCallback) throws SQLException {
        if (null != session) {
            session.doWork(_conn -> {
                final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
                getLogger(this).trace(queryString + " | returnType: " + returnType);
                final CallableStatement _callableStatement = _conn.prepareCall(queryString);
                _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                callableStatementCallback.apply(_callableStatement);
            });
        } else if (null != connection) {
            final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
            getLogger(this).trace(queryString + " | returnType: " + returnType);
            final CallableStatement _callableStatement = connection.prepareCall(queryString);
            _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

            callableStatementCallback.apply(_callableStatement);
        } else {
            throw new SQLException("Does not exist session or connection to execute SQL");
        }
    }
}
