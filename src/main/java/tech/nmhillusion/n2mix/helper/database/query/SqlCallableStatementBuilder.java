package tech.nmhillusion.n2mix.helper.database.query;

import org.springframework.lang.NonNull;
import tech.nmhillusion.n2mix.helper.database.query.callback.CallableStatementCallback;
import tech.nmhillusion.n2mix.helper.database.query.callback.NoReturnCallableStatementCallback;
import tech.nmhillusion.n2mix.model.database.DbArgumentModel;
import tech.nmhillusion.n2mix.model.database.DbInputModel;
import tech.nmhillusion.n2mix.model.database.DbOutParameterModel;
import tech.nmhillusion.n2mix.util.DateUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class SqlCallableStatementBuilder {
    @Deprecated
    private final ConnectionWrapper connectionWrapper;

    private final StatementExecutor statementExecutor;

    private final LinkedList<DbInputModel> dbInputModels = new LinkedList<>();
    private String functionName;
    private String procedureName;
    private Optional<Integer> returnTypeOpt = Optional.empty();
    private boolean hasSetCallType = false;

    public SqlCallableStatementBuilder(ConnectionWrapper connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
        this.statementExecutor = null;
    }

    public SqlCallableStatementBuilder(StatementExecutor statementExecutor) {
        this.connectionWrapper = null;
        this.statementExecutor = statementExecutor;
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

    public SqlCallableStatementBuilder setReturnType(int returnTypeOpt) {
        this.returnTypeOpt = Optional.of(returnTypeOpt);
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

    public String toString() {
        return buildQueryBuilder(
                !StringValidator.isBlank(functionName)
                        ? functionName
                        : procedureName
        );
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

        {//-- Mark: LOG QUERY
            try {
                final String argumentListValue_ = dbInputModels
                        .stream()
                        .map((k) -> {
                            String argv = "<Cannot obtain value of param>";
                            try {
                                final String rawInputValue = String.valueOf(k.getInputValue());
                                argv = String.class.isAssignableFrom(k.getInputType())
                                        ? "'%s'".formatted(rawInputValue.replace("'", "''"))
                                        : rawInputValue;
                            } catch (SQLException ex) {
                                argv = "<Cannot obtain value of param: %s>"
                                        .formatted(ex.getMessage());
                            }
                            return k.getInputName() + " => " + argv;
                        })
                        .collect(Collectors.joining(","));

                getLogger(this).info(
                        "$callName($argumentList)"
                                .replace("$callName", callName)
                                .replace("$argumentList", argumentListValue_)
                );
            } catch (Throwable ex) {
                getLogger(this).error(ex);
            }
        }

        return finalQuery;
    }

    @Deprecated
    public CallableStatement build() throws SQLException {
        if (!StringValidator.isBlank(functionName)) {
            if (returnTypeOpt.isEmpty()) {
                throw new IllegalArgumentException("Fail when setting function name without returnType, by setReturnType(int)");
            }

            final CallableStatement funcCallableStatement = connectionWrapper.buildCallableStatementNamed(
                    buildQueryBuilder(functionName),
                    returnTypeOpt.get()
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

    public void build(NoReturnCallableStatementCallback noReturnCallback) throws SQLException {
        if (null == statementExecutor) {
            throw new IllegalArgumentException("Fail when not exist statementExecutor");
        }

        if (!StringValidator.isBlank(functionName)) {
            if (returnTypeOpt.isEmpty()) {
                throw new IllegalArgumentException("Fail when setting function name without returnType, by setReturnType(int)");
            }

            statementExecutor.doCallableStatementNamed(
                    buildQueryBuilder(functionName),
                    returnTypeOpt.get(),
                    (callableStatement) -> {
                        addDbInputsToCallable(callableStatement);
                        noReturnCallback.apply(callableStatement);
                    }
            );
        } else if (!StringValidator.isBlank(procedureName)) {
            statementExecutor.doPureCallableStatement(
                    buildQueryBuilder(procedureName),
                    (callableStatement) -> {
                        addDbInputsToCallable(callableStatement);
                        noReturnCallback.apply(callableStatement);
                    }
            );
        } else {
            throw new SQLException("Does NOT set functionName or procedureName");
        }
    }

    public <T> T buildReturning(CallableStatementCallback<T> callback) throws SQLException {
        if (null == statementExecutor) {
            throw new IllegalArgumentException("Fail when not exist statementExecutor");
        }

        if (!StringValidator.isBlank(functionName)) {
            if (returnTypeOpt.isEmpty()) {
                throw new IllegalArgumentException("Fail when setting function name without returnType, by setReturnType(int)");
            }

            return statementExecutor.doReturningCallableStatementNamed(
                    buildQueryBuilder(functionName),
                    returnTypeOpt.get(),
                    (callableStatement) -> {
                        addDbInputsToCallable(callableStatement);
                        return callback.apply(callableStatement);
                    }
            );
        } else if (!StringValidator.isBlank(procedureName)) {
            return statementExecutor.doReturningPureCallableStatement(
                    buildQueryBuilder(procedureName),
                    (callableStatement) -> {
                        addDbInputsToCallable(callableStatement);
                        return callback.apply(callableStatement);
                    }
            );
        } else {
            throw new SQLException("Does NOT set functionName or procedureName");
        }
    }
}
