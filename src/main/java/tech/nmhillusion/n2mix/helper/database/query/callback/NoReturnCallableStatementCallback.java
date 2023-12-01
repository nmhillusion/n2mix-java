package tech.nmhillusion.n2mix.helper.database.query.callback;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-01
 */
public interface NoReturnCallableStatementCallback {
    void apply(CallableStatement callableStatement) throws SQLException;
}
