package tech.nmhillusion.n2mix.model.database.jdbc_template;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import tech.nmhillusion.n2mix.model.database.jdbc_template.param.DbIndexArgument;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * created by: chubb
 * <p>
 * created date: 2023-11-28
 */
public class JdbcTemplatePreparedStatement implements PreparedStatement {
    private final JdbcTemplate jdbcTemplate;
    private final String sql;
    private final List<DbIndexArgument> paramMap = new ArrayList<>();
    private PreparedStatement preparedStatement_;

    public JdbcTemplatePreparedStatement(JdbcTemplate jdbcTemplate, String sql) {
        this.jdbcTemplate = jdbcTemplate;
        this.sql = sql;
    }


    private <T> T __execute__(PreparedStatementCallback<T> callback_) {
        return jdbcTemplate.execute(new JdbcTemplatePreparedStatementCreator(this.sql), (preparedStatement_) -> {
            for (DbIndexArgument dbIndexArgument : paramMap) {
                if (dbIndexArgument.getHasSetSqlType()) {
                    preparedStatement_.setObject(
                            dbIndexArgument.getParameterIndex(),
                            dbIndexArgument.getValue(),
                            dbIndexArgument.getSqlType()
                    );
                } else {
                    preparedStatement_.setObject(
                            dbIndexArgument.getParameterIndex(),
                            dbIndexArgument.getValue()
                    );
                }
            }
            this.preparedStatement_ = preparedStatement_;
            return callback_.doInPreparedStatement(preparedStatement_);
        });
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return __execute__(PreparedStatement::executeQuery);
    }

    @Override
    public int executeUpdate() throws SQLException {
        return __execute__(PreparedStatement::executeUpdate);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(null)
                .setSqlType(Types.NULL)
        );
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.BOOLEAN)
        );
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.BIT)
        );
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.TINYINT)
        );
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.INTEGER)
        );
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.NUMERIC)
        );
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.NUMERIC)
        );
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.REAL)
        );
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.NUMERIC)
        );
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.VARCHAR)
        );
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.DATE)
        );
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.TIME)
        );
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.TIMESTAMP)
        );
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearParameters() throws SQLException {
        paramMap.clear();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(targetSqlType)
        );
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
        );
    }

    @Override
    public boolean execute() throws SQLException {
        return __execute__(PreparedStatement::execute);
    }

    @Override
    public void addBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.REF)
        );
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.BLOB)
        );
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.CLOB)
        );
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(x)
                .setSqlType(Types.ARRAY)
        );
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        paramMap.add(new DbIndexArgument()
                .setParameterIndex(parameterIndex)
                .setValue(value)
                .setSqlType(Types.NVARCHAR)
        );
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return __execute__(ps -> ps.executeQuery(sql));
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return __execute__(ps -> ps.executeUpdate(sql));
    }

    @Override
    public void close() throws SQLException {
        if (null != preparedStatement_) {
            preparedStatement_.close();
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxRows() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel() throws SQLException {
        if (null != preparedStatement_) {
            preparedStatement_.cancel();
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return __execute__(ps -> ps.execute(sql));
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return __execute__(Statement::executeBatch);
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return __execute__(ps -> ps.executeUpdate(sql, autoGeneratedKeys));
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return __execute__(ps -> ps.executeUpdate(sql, columnIndexes));
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return __execute__(ps -> ps.executeUpdate(sql, columnNames));
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return __execute__(ps -> ps.execute(sql, autoGeneratedKeys));
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return __execute__(ps -> ps.execute(sql, columnIndexes));
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return __execute__(ps -> ps.execute(sql, columnNames));
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return null != preparedStatement_ && preparedStatement_.isClosed();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        if (null != preparedStatement_) {
            preparedStatement_.closeOnCompletion();
        }
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
