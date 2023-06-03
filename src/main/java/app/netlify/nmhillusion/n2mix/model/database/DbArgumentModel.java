package app.netlify.nmhillusion.n2mix.model.database;

import java.sql.SQLException;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class DbArgumentModel implements DbInputModel {
	private String argumentName;
	private Object argumentValue;

	@Override
	public String getInputName() {
		return argumentName;
	}

	@Override
	public String getInputValue() {
		return String.valueOf(argumentValue);
	}

	@Override
	public Class<?> getInputType() throws SQLException {
		return null != argumentValue ? argumentValue.getClass() : Object.class;
	}

	public String getArgumentName() {
		return argumentName;
	}

	public DbArgumentModel setArgumentName(String argumentName) {
		this.argumentName = argumentName;
		return this;
	}

	public Object getArgumentValue() {
		return argumentValue;
	}

	public DbArgumentModel setArgumentValue(Object argumentValue) {
		this.argumentValue = argumentValue;
		return this;
	}
}
