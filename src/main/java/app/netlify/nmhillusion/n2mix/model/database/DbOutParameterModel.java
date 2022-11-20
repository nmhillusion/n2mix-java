package app.netlify.nmhillusion.n2mix.model.database;

/**
 * date: 2021-11-10
 * <p>
 * created-by: nmhillusion
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
        return String.valueOf(sqlType);
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
