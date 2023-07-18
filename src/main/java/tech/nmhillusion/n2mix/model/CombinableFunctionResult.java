package tech.nmhillusion.n2mix.model;

import java.util.List;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class CombinableFunctionResult<R> {
    private List<R> result;
    private List<Throwable> throwableList;

    public List<R> getResult() {
        return result;
    }

    public void setResult(List<R> result) {
        this.result = result;
    }

    public List<Throwable> getThrowableList() {
        return throwableList;
    }

    public void setThrowableList(List<Throwable> throwableList) {
        this.throwableList = throwableList;
    }
}
