package app.netlify.nmhillusion.n2mix.model.database;

import java.sql.JDBCType;
import java.sql.SQLException;

/**
 * date: 2021-11-10
 * <p>
 * created-by: minguy1
 */

public class DbOutParameterModel implements DbInputModel {
	private String parameterName;
	private int sqlType;

	@Override
	public String getInputName() {
		return parameterName;
	}

	@Override
	public String getInputValue() {
		return sqlType + "(" + JDBCType.valueOf(sqlType).getName() + ")";
	}

	@Override
	public Class<?> getInputType() throws SQLException {
		return int.class;
	}

	public String getParameterName() {
		return parameterName;
	}

	public DbOutParameterModel setParameterName(String parameterName) {
		this.parameterName = parameterName;
		return this;
	}

	public int getSqlType() {
		return sqlType;
	}

	public DbOutParameterModel setSqlType(int sqlType) {
		this.sqlType = sqlType;
		return this;
	}
}
