package tech.nmhillusion.n2mix.model.database.jdbc_template.param;

import tech.nmhillusion.n2mix.type.Stringeable;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-11-28
 */
public class DbIndexArgument extends Stringeable {
    private int parameterIndex;
    private Object value;
    /**
     * The SQL type (as defined in java.sql.Types) to be sent to the database
     */
    private int sqlType;
    private boolean hasSetSqlType = false;

    public int getParameterIndex() {
        return parameterIndex;
    }

    public DbIndexArgument setParameterIndex(int parameterIndex) {
        this.parameterIndex = parameterIndex;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public DbIndexArgument setValue(Object value) {
        this.value = value;
        return this;
    }

    public int getSqlType() {
        return sqlType;
    }

    public DbIndexArgument setSqlType(int sqlType) {
        hasSetSqlType = true;
        this.sqlType = sqlType;
        return this;
    }

    public boolean getHasSetSqlType() {
        return hasSetSqlType;
    }
}
