package tech.nmhillusion.n2mix.model;

import tech.nmhillusion.n2mix.type.Stringeable;

/**
 * date: 2023-08-21
 * <p>
 * created-by: nmhillusion
 */

public class ResultResponseEntity<T> extends Stringeable {
    private boolean success;
    private String message;
    private T data;
    
    public boolean getSuccess() {
        return success;
    }
    
    public ResultResponseEntity<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }
    
    public String getMessage() {
        return message;
    }
    
    public ResultResponseEntity<T> setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public T getData() {
        return data;
    }
    
    public ResultResponseEntity<T> setData(T data) {
        this.data = data;
        return this;
    }
}
