package app.netlify.nmhillusion.n2mix.model.database;

/**
 * date: 2021-11-10
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
