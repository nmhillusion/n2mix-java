package app.netlify.nmhillusion.n2mix.helper.database;

import org.hibernate.Session;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.function.BiFunction;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLogger;


public class ConnectionWrapper implements AutoCloseable {
    public static final String RESULT_PARAM_NAME = "result";
    private final DataSource dataSource;
    private final BiFunction<DataSource, ConnectionWrapper, Boolean> closeConnectionWrapper;
    private Connection connection = null;
    private Session session = null;

    public ConnectionWrapper(DataSource dataSource,
                             Connection connection,
                             BiFunction<DataSource, ConnectionWrapper, Boolean> closeConnectionWrapper) {
        this.dataSource = dataSource;
        this.connection = connection;
        this.closeConnectionWrapper = closeConnectionWrapper;
    }

    public ConnectionWrapper(DataSource dataSource,
                             Session session,
                             BiFunction<DataSource, ConnectionWrapper, Boolean> closeConnectionWrapper) {
        this.dataSource = dataSource;
        this.session = session;
        this.closeConnectionWrapper = closeConnectionWrapper;
    }

    public Connection getConnection() {
        return connection;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Session getSession() {
        return session;
    }

    public PreparedStatement buildPreparedStatement(String sql) {
        PreparedStatement preparedStatement = null;
        try {
            if (null != session) {
                preparedStatement = session.doReturningWork(_conn -> _conn.prepareStatement(sql));
            } else if (null != connection) {
                preparedStatement = connection.prepareStatement(sql);
            }
        } catch (Exception ex) {
            getLogger(this).error(ex);
        }
        return preparedStatement;
    }

    public CallableStatement buildPureCallableStatement(String callQuery) {
        CallableStatement callableStatement = null;
        try {
            if (null != session) {
                callableStatement = session.doReturningWork(_conn -> _conn.prepareCall(
                        "{ call " + callQuery + " } "));
            } else if (null != connection) {
                callableStatement = connection.prepareCall(
                        "{ call " + callQuery + " } ");
            }
        } catch (Exception ex) {
            getLogger(this).error(ex);
        }
        return callableStatement;
    }

    public CallableStatement buildCallableStatement(String callQuery) {
        return buildCallableStatement(callQuery, Types.VARCHAR);
    }

    /**
     * Build <i>CallableStatement</i> with Ordinal Parameter <br>
     * <b>Example:</b><br>
     * To call in hibernate -->
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * { ? = call ams.pkg_example.f_demo(i_username => ?) }
     *
     * </pre>
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * CallableStatement call = connectionWrapper.buildCallableStatement("ams.pkg_example.f_demo(i_username => ?)", Types.REF_CURSOR);
     * call.setString(2, "it_query");
     * call.execute();
     *
     * Object result = call.getObject(1);
     *
     * </pre>
     *
     * @param callQuery  a sql query with named parameters
     * @param returnType
     * @return callableStatement was built
     */
    public CallableStatement buildCallableStatement(String callQuery, int returnType) {
        CallableStatement callableStatement = null;
        try {
            if (null != session) {
                callableStatement = session.doReturningWork(_conn -> {
                    CallableStatement _callableStatement = _conn.prepareCall(
                            "{ ? = call " + callQuery + " } ");
                    _callableStatement.registerOutParameter(1, returnType);
                    return _callableStatement;
                });
            } else if (null != connection) {
                callableStatement = connection.prepareCall(
                        "{ ? = call " + callQuery + " } ");
                callableStatement.registerOutParameter(1, returnType);
            }
        } catch (Exception ex) {
            getLogger(this).error(ex);
        }
        return callableStatement;
    }

    /**
     * Build <i>CallableStatement</i> with Named Parameter <br>
     * <b>Example:</b><br>
     * To call in hibernate -->
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * { call :result := ams.pkg_example.f_demo(i_username => :i_data) }
     *
     * </pre>
     * Code in java looks like:
     * <pre style='background-color: #333333; color: #008b8b;'>
     *
     * CallableStatement call = connectionWrapper.buildCallableStatementNamed("ams.pkg_example.f_demo(i_username => :i_data)", Types.REF_CURSOR);
     * call.setString("i_data", "it_query");
     * call.execute();
     *
     * Object result = call.getObject(ConnectionWrapper.RESULT_PARAM_NAME);
     *
     * </pre>
     *
     * @param callQuery  a sql query with named parameters
     * @param returnType
     * @return callableStatement was built
     */
    public CallableStatement buildCallableStatementNamed(String callQuery, int returnType) {
        CallableStatement callableStatement = null;
        try {
            if (null != session) {
                callableStatement = session.doReturningWork(_conn -> {
                    String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
                    getLogger(this).trace(queryString + " | returnType: " + returnType);
                    CallableStatement _callableStatement = _conn.prepareCall(queryString);
                    _callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);
                    return _callableStatement;
                });
            } else if (null != connection) {
                String queryString = "{ call :" + RESULT_PARAM_NAME + " := " + callQuery + " }";
                getLogger(this).trace(queryString + " | returnType: " + returnType);
                callableStatement = connection.prepareCall(queryString);
                callableStatement.registerOutParameter(RESULT_PARAM_NAME, returnType);
            }
        } catch (Exception ex) {
            getLogger(this).error(ex);
        }
        return callableStatement;
    }

    @Override
    public void close() throws Exception {
        if (null != closeConnectionWrapper) {
            closeConnectionWrapper.apply(dataSource, this);
        }
    }
}
