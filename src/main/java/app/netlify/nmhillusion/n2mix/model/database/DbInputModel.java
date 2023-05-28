package app.netlify.nmhillusion.n2mix.model.database;

import java.sql.SQLException;

/**
 * date: 2021-11-10
 * <p>
 * created-by: minguy1
 */
public interface DbInputModel {
    String getInputName();

    String getInputValue();

    Class<?> getInputType() throws SQLException;
}
