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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;
import static tech.nmhillusion.n2mix.util.ExceptionUtil.getExceptionFromStacktrace;

/**
 * Executes SQL statements on either a Hibernate {@link Session} or a Spring {@link JdbcTemplate}.
 *
 * <p><b>Statement lifetime.</b> Every {@link PreparedStatement} or {@link CallableStatement} created
 * by this executor is closed before the executor method returns, regardless of whether the
 * callback completes normally or throws. Consequently, a callback must not leak the statement,
 * any {@link java.sql.ResultSet} derived from it, or any {@link java.sql.Blob}/{@link java.sql.Clob}
 * stream outside the callback — those resources are only valid inside the callback.</p>
 *
 * <p><b>Session ownership.</b> A {@link Session} passed to the constructor is borrowed, not owned:
 * {@link #close()} only marks this executor as closed and never closes that session. The caller, or
 * the executor that created it, remains responsible for closing it.</p>
 *
 * <p>created by: nmhillusion
 * <p>
 * created date: 2023-12-01
 */
public class StatementExecutor implements AutoCloseable {
    public static final String RESULT_PARAM_NAME = "result";
    private final JdbcTemplate jdbcTemplate;
    private final Session session;

    private boolean isClosed = false;

    public StatementExecutor(Session session) {
        if (null == session) {
            throw new IllegalArgumentException("Session must not be null");
        }
        this.session = session;
        this.jdbcTemplate = null;
    }

    public StatementExecutor(JdbcTemplate jdbcTemplate) {
        if (null == jdbcTemplate) {
            throw new IllegalArgumentException("JdbcTemplate must not be null");
        }
        this.session = null;
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean isAbleToExecute() {
        return !isClosed;
    }

    private <T> T wrapperToSqlExceptionReturning(ThrowableNoInputFunction<T> func) throws SQLException {
        try {
            if (!isAbleToExecute()) {
                throw new SQLException("This statement executor has closed");
            }
            return func.apply();
        } catch (Error err) {
            throw err;
        } catch (Throwable ex) {
            throw getExceptionFromStacktrace(ex, SQLException.class, new SQLException(ex));
        }
    }

    private void wrapperToSqlException(ThrowableVoidNoInputFunction func) throws SQLException {
        try {
            if (!isAbleToExecute()) {
                throw new SQLException("This statement executor has closed");
            }
            func.apply();
        } catch (Error err) {
            throw err;
        } catch (Throwable ex) {
            throw getExceptionFromStacktrace(ex, SQLException.class, new SQLException(ex));
        }
    }

    /**
     * Execute <i>PreparedStatement</i> with Ordinal Parameter <br>
     * <b>Example:</b><br>
     * To call in native query -->
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * update dds.t_document set title = ? where doc_id = ?
     *
     * </pre>
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * statementExecutor.doPreparedStatement("update dds.t_document set title = ? where doc_id = ?", (preparedStatement_) -> {
     *     preparedStatement_.setString(1, 'The Code');
     *     preparedStatement_.setInt(2, 2);
     *     preparedStatement_.execute();
     * });
     *
     * </pre>
     *
     * @param sql       a sql query stands for a prepared statement
     * @param callback_ function will be called when execute
     */
    public void doPreparedStatement(String sql, NoReturnPreparedStatementCallback callback_) throws SQLException {
        wrapperToSqlException(() -> {
            if (null != session) {
                session.doWork(_conn -> {
                    try (final PreparedStatement preparedStatement_ = _conn.prepareStatement(sql)) {
                        callback_.apply(preparedStatement_);
                    }
                });
            } else if (null != jdbcTemplate) {
                jdbcTemplate.execute(sql, (PreparedStatement preparedStatement_) -> {
                    callback_.apply(preparedStatement_);
                    return null;
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    /**
     * Execute <i>PreparedStatement</i> with Ordinal Parameter <br>
     * <b>Example:</b><br>
     * To call in native query -->
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * select * from dds.t_document where doc_id = ?
     *
     * </pre>
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * List&lt;String&gt; result_ = statementExecutor.doReturningPreparedStatement("select * from dds.t_document where doc_id = ?", (preparedStatement_) -> {
     *     preparedStatement_.setInt(1, 2);
     *     try (ResultSet resultSet = preparedStatement_.executeQuery()) {
     *         List&lt;String&gt; resultList = new ArrayList<>();
     *         // ... do something with resultSet
     *
     *         return resultList;
     *     }
     * });
     *
     * </pre>
     *
     * @param sql       a sql query stands for a prepared statement
     * @param callback_ function will be called when execute and result of this callback will be used as return value of this function `doReturningPreparedStatement()`
     */
    public <T> T doReturningPreparedStatement(String sql, PreparedStatementCallback<T> callback_) throws SQLException {
        return wrapperToSqlExceptionReturning(() -> {
            if (null != session) {
                return session.doReturningWork(_conn -> {
                    try (final PreparedStatement preparedStatement_ = _conn.prepareStatement(sql)) {
                        return callback_.apply(preparedStatement_);
                    }
                });
            } else if (null != jdbcTemplate) {
                return jdbcTemplate.execute(sql, (PreparedStatement pre_) -> callback_.apply(pre_));
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    /**
     * Execute <i>CallableStatement</i> with Ordinal Parameter <br>
     * <b>Example:</b><br>
     * To call in native query -->
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * { ? = call ams.pkg_example.f_demo(i_username => ?) }
     *
     * </pre>
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * List&lt;String&gt; result_ = statementExecutor.doReturningCallableStatement("ams.pkg_example.f_demo(i_username => ?)", Types.REF_CURSOR, (call_) -> {
     *     call.setString(2, "it_query");
     *     call.execute();
     *
     *     Object result = call.getObject(1);
     *     List&lt;String&gt; resultList = new ArrayList<>();
     *     // ... do something
     *     return resultList;
     * });
     * </pre>
     *
     * @param callQuery                 a sql query to call a procedure / function
     * @param returnType                SQL return type of this procedure / function
     * @param callableStatementCallback function will be called when execute and result of this callback will be used as return value of this function `doReturningCallableStatement()`
     */
    public <T> T doReturningCallableStatement(String callQuery, int returnType, CallableStatementCallback<T> callableStatementCallback) throws SQLException {
        return wrapperToSqlExceptionReturning(() -> {
            final String callStatement = "{ ? = call " + callQuery + " } ";
            if (null != session) {
                return session.doReturningWork(_conn -> {
                    try (final CallableStatement _callableStatement = _conn.prepareCall(callStatement)) {
                        _callableStatement.registerOutParameter(1, returnType);

                        return callableStatementCallback.apply(_callableStatement);
                    }
                });
            } else if (null != jdbcTemplate) {
                return jdbcTemplate.execute(callStatement, (CallableStatement call_) -> {
                    call_.registerOutParameter(1, returnType);

                    return callableStatementCallback.apply(call_);
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    /**
     * Execute <i>CallableStatement</i> with Ordinal Parameter <br>
     * <b>Example:</b><br>
     * To call in native query -->
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * { ? = call ams.pkg_example.f_demo(i_username => ?) }
     *
     * </pre>
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * statementExecutor.doCallableStatement("ams.pkg_example.f_demo(i_username => ?)", Types.REF_CURSOR, (call_) -> {
     *     call.setString(2, "it_query");
     *     call.execute();
     *
     *     Object result = call.getObject(1);
     * });
     * </pre>
     *
     * @param callQuery                 a sql query to call a procedure / function
     * @param returnType                SQL return type of this procedure / function
     * @param callableStatementCallback callback will be called when executing
     */
    public void doCallableStatement(String callQuery, int returnType, NoReturnCallableStatementCallback callableStatementCallback) throws SQLException {
        wrapperToSqlException(() -> {
            final String callStatement = "{ ? = call " + callQuery + " } ";
            if (null != session) {
                session.doWork(_conn -> {
                    try (final CallableStatement _callableStatement = _conn.prepareCall(
                            callStatement)) {
                        _callableStatement.registerOutParameter(1, returnType);

                        callableStatementCallback.apply(_callableStatement);
                    }
                });
            } else if (null != jdbcTemplate) {
                jdbcTemplate.execute(callStatement, (CallableStatement call_) -> {
                    call_.registerOutParameter(1, returnType);

                    callableStatementCallback.apply(call_);
                    return null;
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    /**
     * Using the same as StatementExecutor.doReturningCallableStatement(String, int, CallableStatementCallback) but without re-existed parameter for out parameter
     *
     * @see StatementExecutor#doReturningCallableStatement(String, int, CallableStatementCallback)
     */
    public <T> T doReturningPureCallableStatement(String callQuery, CallableStatementCallback<T> callableStatementCallback) throws SQLException {
        return wrapperToSqlExceptionReturning(() -> {
            final String callStatement = "{ call " + callQuery + " } ";
            if (null != session) {
                return session.doReturningWork(_conn -> {
                    try (final CallableStatement _callableStatement = _conn.prepareCall(callStatement)) {

                        return callableStatementCallback.apply(_callableStatement);
                    }
                });
            } else if (null != jdbcTemplate) {
                return jdbcTemplate.execute(callStatement, callableStatementCallback::apply);
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    /**
     * Using the same as StatementExecutor.doCallableStatement(String, int, NoReturnCallableStatementCallback) but without re-existed parameter for out parameter
     *
     * @see StatementExecutor#doCallableStatement(String, int, NoReturnCallableStatementCallback)
     */
    public void doPureCallableStatement(String callQuery, NoReturnCallableStatementCallback callableStatementCallback) throws SQLException {
        wrapperToSqlException(() -> {
            final String callStatement = "{ call " + callQuery + " } ";
            if (null != session) {
                session.doWork(_conn -> {
                    try (final CallableStatement _callableStatement = _conn.prepareCall(callStatement)) {

                        callableStatementCallback.apply(_callableStatement);
                    }
                });
            } else if (null != jdbcTemplate) {
                jdbcTemplate.execute(callStatement, (CallableStatement call_) -> {
                    callableStatementCallback.apply(call_);
                    return null;
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    /**
     * Using the same as StatementExecutor.doReturningCallableStatement(String, int, CallableStatementCallback) but all parameter will be used in named parameter.
     * So when using and pass parameter, we have to put by named parameter also
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * List&lt;String&gt; result_ = statementExecutor.doReturningCallableStatementNamed("ams.pkg_example.f_demo(i_username => :i_username)", Types.REF_CURSOR, (call_) -> {
     *     call.setString("i_username", "it_query");
     *     call.execute();
     *
     *     Object result = call.getObject(StatementExecutor.RESULT_PARAM_NAME);
     *     List&lt;String&gt; resultList = new ArrayList<>();
     *     if (result instanceof ResultSet resultSet) {
     *       //...do something with resultSet
     *     }
     *     return resultList;
     * });
     * </pre>
     *
     * @see StatementExecutor#doReturningCallableStatement(String, int, CallableStatementCallback)
     */
    public <T> T doReturningCallableStatementNamed(String callQuery, int returnType, CallableStatementCallback<T> callableStatementCallback) throws SQLException {
        return wrapperToSqlExceptionReturning(() -> {
            final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
            if (LogHelper.getLogger(this).isTraceEnabled()) {
                getLogger(this).trace(queryString + " | returnType: " + returnType);
            }

            if (null != session) {
                return session.doReturningWork(_conn -> {
                    try (final CallableStatement _callableStatement = _conn.prepareCall(queryString)) {
                        _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                        return callableStatementCallback.apply(_callableStatement);
                    }
                });
            } else if (null != jdbcTemplate) {
                return jdbcTemplate.execute(queryString, (CallableStatement call_) -> {
                    call_.registerOutParameter(RESULT_PARAM_NAME, returnType);

                    return callableStatementCallback.apply(call_);
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    /**
     * Using the same as StatementExecutor.doReturningCallableStatementNamed(String, int, CallableStatementCallback) but without return value
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * statementExecutor.doCallableStatementNamed("ams.pkg_example.f_demo_2(i_username => :i_username)", Types.REF_CURSOR, (call_) -> {
     *     call.setString("i_username", "it_query");
     *     call.execute();
     * });
     * </pre>
     *
     * @see StatementExecutor#doReturningCallableStatementNamed(String, int, CallableStatementCallback)
     */
    public void doCallableStatementNamed(String callQuery, int returnType, NoReturnCallableStatementCallback callableStatementCallback) throws SQLException {
        wrapperToSqlException(() -> {
            final String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
            if (LogHelper.getLogger(this).isTraceEnabled()) {
                getLogger(this).trace(queryString + " | returnType: " + returnType);
            }

            if (null != session) {
                session.doWork(_conn -> {

                    try (final CallableStatement _callableStatement = _conn.prepareCall(queryString)) {
                        _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                        callableStatementCallback.apply(_callableStatement);
                    }
                });
            } else if (null != jdbcTemplate) {
                jdbcTemplate.execute(queryString, (CallableStatement call_) -> {
                    call_.registerOutParameter(RESULT_PARAM_NAME, returnType);

                    callableStatementCallback.apply(call_);
                    return null;
                });
            } else {
                throw new SQLException("Does not exist session or connection to execute SQL");
            }
        });
    }

    @Override
    public void close() {
        isClosed = true;
    }
}
