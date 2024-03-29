package tech.nmhillusion.n2mix.helper.database.result;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * created by: chubb
 * <p>
 * created date: 2023-11-24
 */
public interface ResultSetObjectBuilderCallback<T> {
    void throwableApply(T t, ResultSet resultSet) throws SQLException;
}
