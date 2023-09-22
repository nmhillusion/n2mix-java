package tech.nmhillusion.n2mix.helper.database.result;

import tech.nmhillusion.n2mix.helper.database.query.ExtractResultToPage;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.util.CastUtil;
import tech.nmhillusion.n2mix.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-22
 */
public class ResultSetObjectBuilder<T> {
    private ResultSet resultSet;
    private Class<T> objectBuilderClass;

    public ResultSet getResultSet() {
        return resultSet;
    }

    public ResultSetObjectBuilder<T> setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        return this;
    }

    public Class<T> getObjectBuilderClass() {
        return objectBuilderClass;
    }

    public ResultSetObjectBuilder<T> setObjectBuilderClass(Class<T> objectBuilderClass) {
        this.objectBuilderClass = objectBuilderClass;
        return this;
    }

    private T newInstanceWithNoArgs() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            final Constructor<T> constructorNoArgs = objectBuilderClass.getConstructor();
            return constructorNoArgs.newInstance();
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("Required a constructor with no args");
        }
    }

    private T fillDataForInstance(Class<T> mainClass, T instance_, ResultSet resultSet) throws SQLException {
        final List<String> allColumnNames = ExtractResultToPage.getAllColumnNames(resultSet);

        for (String columnName : allColumnNames) {
            try {
                final String fieldNameInPascalCase = StringUtil.convertPascalCaseFromSnakeCase(columnName);
                final String fieldNameInCamelCase = StringUtil.convertCamelCaseFromPascalCase(fieldNameInPascalCase);
                final Field field_ = mainClass.getDeclaredField(fieldNameInCamelCase);
                final Method method_ = mainClass.getMethod("set" + fieldNameInPascalCase, field_.getType());

                final Object rawObject = resultSet.getObject(columnName);
                final Object convertedObject = CastUtil.safeCast(rawObject, field_.getType());

                method_.invoke(instance_, convertedObject);
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                LogHelper.getLogger(this).warn("No field with column name [%s]".formatted(columnName));
            }
        }
        return instance_;
    }


    public T build() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        if (null == objectBuilderClass) {
            return null;
        }

        final T instance_ = newInstanceWithNoArgs();
        return fillDataForInstance(objectBuilderClass, instance_, resultSet);
    }
}
