package tech.nmhillusion.n2mix.helper.database.result;

import tech.nmhillusion.n2mix.helper.database.query.ExtractResultToPage;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.util.CastUtil;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-22
 */
public class ResultSetObjectBuilder {
    private final Map<String, ThrowableFunction<Object, Object>> customConverters = new HashMap<>();
    private ResultSet resultSet;
    private boolean isIgnoreWarningMissingField = true;

    public ResultSet getResultSet() {
        return resultSet;
    }

    public ResultSetObjectBuilder setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        return this;
    }

    public Map<String, ThrowableFunction<Object, Object>> getCustomConverters() {
        return customConverters;
    }

    public ResultSetObjectBuilder addCustomConverters(String columnName, ThrowableFunction<Object, Object> customConverter) {
        if (StringValidator.isBlank(columnName) || null == customConverter) {
            return this;
        }

        this.customConverters.put(columnName.toLowerCase(), customConverter);
        return this;
    }

    public ResultSetObjectBuilder setIsIgnoreWarningMissingField(boolean ignoreWarningMissingField) {
        isIgnoreWarningMissingField = ignoreWarningMissingField;
        return this;
    }

    private <T> T newInstanceWithNoArgs(Class<T> mainClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            final Constructor<T> constructorNoArgs = mainClass.getConstructor();
            return constructorNoArgs.newInstance();
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("Required a constructor with no args");
        }
    }

    private <T> T fillDataForInstance(Class<T> mainClass, T instance_, ResultSet resultSet) throws SQLException {
        final List<String> allColumnNames = ExtractResultToPage.getAllColumnNames(resultSet);

        for (String columnName : allColumnNames) {
            try {
                columnName = columnName.toLowerCase();

                final String fieldNameInPascalCase = StringUtil.convertPascalCaseFromSnakeCase(columnName);
                final String fieldNameInCamelCase = StringUtil.convertCamelCaseFromPascalCase(fieldNameInPascalCase);
                final Field field_ = mainClass.getDeclaredField(fieldNameInCamelCase);
                final Method method_ = mainClass.getMethod("set" + fieldNameInPascalCase, field_.getType());

                final Object rawObject = resultSet.getObject(columnName);
                final Object convertedObject = !customConverters.containsKey(columnName)
                        ? CastUtil.safeCast(rawObject, field_.getType())
                        : customConverters.get(columnName).throwableApply(rawObject);

                method_.invoke(instance_, convertedObject);
            } catch (Throwable ex) {
                if (!isIgnoreWarningMissingField) {
                    LogHelper.getLogger(this).warn("No field with column name [%s]. Error: %s".formatted(columnName, ex));
                }
            }
        }
        return instance_;
    }


    public <T> T build(Class<T> mainClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        if (null == mainClass) {
            return null;
        }

        final T instance_ = newInstanceWithNoArgs(mainClass);
        return fillDataForInstance(mainClass, instance_, resultSet);
    }
}
