package tech.nmhillusion.n2mix.helper.database.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-11-29
 */
@FunctionalInterface
public interface PreparedStatementCallback<T> {
    T apply(PreparedStatement preparedStatement_) throws SQLException;
}
