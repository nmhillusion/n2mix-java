package tech.nmhillusion.n2mix.model.database.jdbc_template;

import org.springframework.jdbc.core.CallableStatementCreator;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * created by: chubb
 * <p>
 * created date: 2023-11-28
 */
public class JdbcTemplateCallableStatementCreator implements CallableStatementCreator {
    private final String sql;

    public JdbcTemplateCallableStatementCreator(String sql) {
        this.sql = sql;
    }

    @Override
    public CallableStatement createCallableStatement(Connection con) throws SQLException {
        return con.prepareCall(this.sql);
    }
}
