package tech.nmhillusion.n2mix.helper.database.result;

import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

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
    private String outParameterName_;
    
    public CallableStatement getCall_() {
        return call_;
    }
    
    public WorkingResultSetHelper setCall_(CallableStatement call_) {
        this.call_ = call_;
        return this;
    }
    
    public String getOutParameterName_() {
        return outParameterName_;
    }
    
    public WorkingResultSetHelper setOutParameterName_(String outParameterName_) {
        this.outParameterName_ = outParameterName_;
        return this;
    }
    
    private void throwIfAbsentRequiredArguments() throws SQLException {
        if (Objects.isNull(this.call_)) {
            throw new SQLException("CallableStatement is not existed");
        }
        
        if (Objects.isNull(this.outParameterName_)) {
            throw new SQLException("outParameterName is not existed");
        }
    }
    
    public <R> R doReturningWorkOnResultSet(ThrowableFunction<ResultSet, R> func_) throws Throwable {
        throwIfAbsentRequiredArguments();
        
        final Object rawResult = call_.getObject(outParameterName_);
        if (rawResult instanceof ResultSet resultSet_) {
            return func_.throwableApply(resultSet_);
        }
        
        throw new SQLException("Cannot obtain ResultSet");
    }
    
    public void doWorkOnResultSet(ThrowableVoidFunction<ResultSet> func_) throws Throwable {
        throwIfAbsentRequiredArguments();
        
        final Object rawResult = call_.getObject(outParameterName_);
        if (rawResult instanceof ResultSet resultSet_) {
            func_.throwableVoidApply(resultSet_);
        } else {
            throw new SQLException("Cannot obtain ResultSet");
        }
    }
}
