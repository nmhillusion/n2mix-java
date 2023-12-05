package tech.nmhillusion.n2mix.helper.database.result;

import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;
import tech.nmhillusion.n2mix.util.ExceptionUtil;
import tech.nmhillusion.n2mix.util.SelectUtil;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * date: 2023-07-19
 * <p>
 * created-by: nmhillusion
 */

public class WorkingResultSetHelper {
    private CallableStatement call_;
    private String outParameterNameOfResultSet_;

    private boolean willCloseResultSet = true;

    public CallableStatement getCallableStatement() {
        return call_;
    }

    public WorkingResultSetHelper setCallableStatement(CallableStatement call_) {
        this.call_ = call_;
        return this;
    }

    public String getOutParameterNameOfResultSet() {
        return outParameterNameOfResultSet_;
    }

    public WorkingResultSetHelper setOutParameterNameOfResultSet(String outParameterNameOfResultSet_) {
        this.outParameterNameOfResultSet_ = outParameterNameOfResultSet_;
        return this;
    }

    public boolean getWillCloseResultSet() {
        return willCloseResultSet;
    }

    public WorkingResultSetHelper setWillCloseResultSet(boolean willCloseResultSet) {
        this.willCloseResultSet = willCloseResultSet;
        return this;
    }

    private void throwIfAbsentRequiredArguments() throws SQLException {
        if (Objects.isNull(this.call_)) {
            throw new SQLException("CallableStatement is not existed");
        }

        if (Objects.isNull(this.outParameterNameOfResultSet_)) {
            throw new SQLException("outParameterNameOfResultSet is not existed");
        }
    }

    private SQLException throwExceptionForResultSet(Object rawResult_) {
        final Object nonNullResultType = SelectUtil.getFirstValueNotNullArgv(rawResult_, new Object());

        String rawResultType = "";
        if (null != nonNullResultType) {
            rawResultType = nonNullResultType.getClass().getName();
        }

        return new SQLException(
                "Cannot obtain ResultSet. rawResult: %s; Class of rawResult: %s"
                        .formatted(rawResult_, rawResultType)
        );
    }

    public <R> R doReturningWorkOnResultSet(ThrowableFunction<ResultSet, R> func_) throws SQLException {
        try {
            throwIfAbsentRequiredArguments();

            final Object rawResult = call_.getObject(outParameterNameOfResultSet_);
            if (rawResult instanceof ResultSet resultSet_) {
                if (willCloseResultSet) {
                    try (resultSet_) {
                        return func_.throwableApply(resultSet_);
                    }
                } else {
                    return func_.throwableApply(resultSet_);
                }
            } else {
                throw throwExceptionForResultSet(rawResult);
            }
        } catch (Throwable ex) {
            throw ExceptionUtil.throwParsedSqlException(ex);
        }
    }

    public void doWorkOnResultSet(ThrowableVoidFunction<ResultSet> func_) throws SQLException {
        try {
            throwIfAbsentRequiredArguments();

            final Object rawResult = call_.getObject(outParameterNameOfResultSet_);
            if (rawResult instanceof ResultSet resultSet_) {
                if (willCloseResultSet) {
                    try (resultSet_) {
                        func_.throwableVoidApply(resultSet_);
                    }
                } else {
                    func_.throwableVoidApply(resultSet_);
                }
            } else {
                throw throwExceptionForResultSet(rawResult);
            }
        } catch (Throwable ex) {
            throw ExceptionUtil.throwParsedSqlException(ex);
        }
    }
}
