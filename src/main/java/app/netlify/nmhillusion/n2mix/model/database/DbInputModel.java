package app.netlify.nmhillusion.n2mix.model.database;

import java.sql.SQLException;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public interface DbInputModel {
    String getInputName();

    String getInputValue();

    Class<?> getInputType() throws SQLException;
}
