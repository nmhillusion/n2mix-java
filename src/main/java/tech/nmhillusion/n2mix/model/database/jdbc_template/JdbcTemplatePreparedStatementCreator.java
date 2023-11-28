package tech.nmhillusion.n2mix.model.database.jdbc_template;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * created by: chubb
 * <p>
 * created date: 2023-11-28
 */
public class JdbcTemplatePreparedStatementCreator implements PreparedStatementCreator {
    private final String sql;

    public JdbcTemplatePreparedStatementCreator(String sql) {
        this.sql = sql;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        return con.prepareStatement(this.sql);
    }

    public String getSql() {
        return sql;
    }
}
