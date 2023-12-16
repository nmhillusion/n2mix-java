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

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;
import static tech.nmhillusion.n2mix.util.ExceptionUtil.getExceptionFromStacktrace;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-01
 */
public class StatementExecutor implements Closeable {
    public static final String RESULT_PARAM_NAME = "result";
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplate = null;
    private Session session = null;

    private boolean isClosed = false;

    public StatementExecutor(DataSource dataSource,
                             Session session) {
        this.dataSource = dataSource;
        this.session = session;
        this.jdbcTemplate = null;
    }

    public StatementExecutor(DataSource dataSource,
                             JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
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
                session.doWork(_conn -> callback_.apply(_conn.prepareStatement(sql)));
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
                return session.doReturningWork(_conn -> callback_.apply(_conn.prepareStatement(sql)));
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
                    final CallableStatement _callableStatement = _conn.prepareCall(callStatement);
                    _callableStatement.registerOutParameter(1, returnType);

                    return callableStatementCallback.apply(_callableStatement);
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
                    final CallableStatement _callableStatement = _conn.prepareCall(
                            callStatement);
                    _callableStatement.registerOutParameter(1, returnType);

                    callableStatementCallback.apply(_callableStatement);
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
                    final CallableStatement _callableStatement = _conn.prepareCall(
                            callStatement);

                    return callableStatementCallback.apply(_callableStatement);
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
                    final CallableStatement _callableStatement = _conn.prepareCall(
                            callStatement);

                    callableStatementCallback.apply(_callableStatement);
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
                    final CallableStatement _callableStatement = _conn.prepareCall(queryString);
                    _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                    return callableStatementCallback.apply(_callableStatement);
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

                    final CallableStatement _callableStatement = _conn.prepareCall(queryString);
                    _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);

                    callableStatementCallback.apply(_callableStatement);
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
    public void close() throws IOException {
        isClosed = true;

        if (null != session) {
            session.close();
        }
    }
}
