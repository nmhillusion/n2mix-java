package app.netlify.nmhillusion.n2mix.helper.database.query;

import app.netlify.nmhillusion.n2mix.model.database.DbArgumentModel;
import app.netlify.nmhillusion.n2mix.model.database.DbInputModel;
import app.netlify.nmhillusion.n2mix.model.database.DbOutParameterModel;
import app.netlify.nmhillusion.n2mix.util.DateUtil;
import app.netlify.nmhillusion.n2mix.validator.StringValidator;
import org.springframework.lang.NonNull;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * date: 2021-11-09
 * <p>
 * created-by: minguy1
 */

public class SqlCallableStatementBuilder {
    private final ConnectionWrapper connectionWrapper;
    private final LinkedList<DbInputModel> dbInputModels = new LinkedList<>();
    private String functionName;
    private String procedureName;
    private int returnType;
    private boolean hasSetCallType = false;

    public SqlCallableStatementBuilder(ConnectionWrapper connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
    }

    /**
     * <b>Example:</b><br>
     * <pre>
     *
     * ams.pkg_example.f_demo
     *
     * </pre>
     **/
    public SqlCallableStatementBuilder setFunctionName(String functionName) throws SQLException {
        if (!hasSetCallType) {
            this.hasSetCallType = true;
            this.functionName = functionName;
            return this;
        } else {
            throw new SQLException("Can only set callable function/procedure one time!");
        }
    }

    public SqlCallableStatementBuilder setProcedureName(String procedureName) throws SQLException {
        if (!hasSetCallType) {
            this.hasSetCallType = true;
            this.procedureName = procedureName;
            return this;
        } else {
            throw new SQLException("Can only set callable function/procedure one time!");
        }
    }

    public SqlCallableStatementBuilder setReturnType(int returnType) {
        this.returnType = returnType;
        return this;
    }

    public SqlCallableStatementBuilder addArgument(String argumentName, Object argumentValue) {
        this.dbInputModels.add(new DbArgumentModel()
                .setArgumentName(argumentName)
                .setArgumentValue(argumentValue)
        );
        return this;
    }

    public SqlCallableStatementBuilder registerOutParameter(String parameterName, int sqlType) {
        this.dbInputModels.add(new DbOutParameterModel()
                .setParameterName(parameterName)
                .setSqlType(sqlType)
        );
        return this;
    }

    public CallableStatement build() throws SQLException {
        if (!StringValidator.isBlank(functionName)) {
            final CallableStatement funcCallableStatement = connectionWrapper.buildCallableStatementNamed(
                    buildQueryBuilder(functionName),
                    returnType
            );
            addDbInputsToCallable(funcCallableStatement);

            return funcCallableStatement;
        } else if (!StringValidator.isBlank(procedureName)) {
            final CallableStatement procCallableStatement = connectionWrapper.buildPureCallableStatement(
                    buildQueryBuilder(procedureName)
            );
            addDbInputsToCallable(procCallableStatement);

            return procCallableStatement;
        } else {
            throw new SQLException("Does NOT set functionName or procedureName");
        }
    }

    private void addDbInputsToCallable(CallableStatement callableStatement) throws SQLException {
        for (DbInputModel dbInputModel : dbInputModels) {
            if (dbInputModel instanceof DbArgumentModel dbArgumentModel) {
                addArgumentToCallable(
                        callableStatement,
                        dbArgumentModel.getArgumentName(),
                        dbArgumentModel.getArgumentValue()
                );
            } else if (dbInputModel instanceof DbOutParameterModel dbOutParameterModel) {
                registerOutParameterToCallable(
                        callableStatement,
                        dbOutParameterModel.getParameterName(),
                        dbOutParameterModel.getSqlType()
                );
            }
        }
    }

    private void registerOutParameterToCallable(CallableStatement callableStatement, String parameterName, int sqlType) throws SQLException {
        callableStatement.registerOutParameter(parameterName, sqlType);
    }

    private void addArgumentToCallable(CallableStatement callableStatement, String argumentName, Object argumentValue) throws SQLException {
        if (argumentValue instanceof Integer) {
            callableStatement.setInt(argumentName, (Integer) argumentValue);
        } else if (argumentValue instanceof Long) {
            callableStatement.setLong(argumentName, (Long) argumentValue);
        } else if (argumentValue instanceof Float) {
            callableStatement.setFloat(argumentName, (Float) argumentValue);
        } else if (argumentValue instanceof Double) {
            callableStatement.setDouble(argumentName, (Double) argumentValue);
        } else if (argumentValue instanceof java.sql.Date) {
            callableStatement.setDate(argumentName, (java.sql.Date) argumentValue);
        } else if (argumentValue instanceof Date dateValue) {
            callableStatement.setDate(argumentName, DateUtil.parseSqlDate(dateValue));
        } else if (argumentValue instanceof Boolean) {
            callableStatement.setBoolean(argumentName, (Boolean) argumentValue);
        } else if (argumentValue instanceof String) {
            callableStatement.setString(argumentName, (String) argumentValue);
        } else {
            callableStatement.setObject(argumentName, argumentValue);
        }
    }

    private String buildQueryBuilder(@NonNull String callName) {
        final List<String> argsStatements = new ArrayList<>();
        for (DbInputModel dbInputModel : dbInputModels) {
            argsStatements.add(
                    "$argumentName => :$argumentName"
                            .replace("$argumentName", dbInputModel.getInputName())
            );
        }

        final String finalQuery = "$callName($argumentList)"
                .replace("$callName", callName)
                .replace("$argumentList", String.join(",", argsStatements));

        {/// Mark: LOG QUERY
            try {
                String infoQuery = finalQuery;
                for (DbInputModel dbInputModel : dbInputModels) {
                    final String printValue = String.class.isAssignableFrom(dbInputModel.getInputType())
                            ? "'" + dbInputModel.getInputValue() + "'"
                            : dbInputModel.getInputValue();

                    infoQuery = infoQuery
                            .replaceFirst(":" + dbInputModel.getInputName() + "[,)]", printValue + ",");
                }
                infoQuery = infoQuery.replaceFirst(",$", ")");

                getLogger(this).info(infoQuery);
            } catch (Throwable ex) {
                getLogger(this).error(ex);
            }
        }

        return finalQuery;
    }
}
