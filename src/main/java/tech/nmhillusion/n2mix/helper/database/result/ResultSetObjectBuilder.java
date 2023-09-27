package tech.nmhillusion.n2mix.helper.database.result;

import tech.nmhillusion.n2mix.annotation.IgnoredField;
import tech.nmhillusion.n2mix.helper.database.query.ExtractResultToPage;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.util.CastUtil;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-22
 */
public class ResultSetObjectBuilder {
    private static final Map<String, List<Field>> FIELD_OF_CLASSES_CACHE = new TreeMap<>();
    private final Map<String, ThrowableFunction<Object, Object>> customConverters = new HashMap<>();
    private final List<String> allColumnNamesCache = new ArrayList<>();
    private final Map<String, Optional<String>> fieldColumnNameMapCache = new TreeMap<>();
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

    private <T> List<Field> getFieldsOfClass(Class<T> mainClass) {
        if (FIELD_OF_CLASSES_CACHE.containsKey(mainClass.getName())) {
            return FIELD_OF_CLASSES_CACHE.get(mainClass.getName());
        }

        getLogger(this).debug("really read fields of class " + mainClass.getName());
        final List<Field> fieldList = Stream.of(
                        mainClass.getDeclaredFields()
                )
                .filter(it -> !it.isAnnotationPresent(IgnoredField.class))
                .toList();

        FIELD_OF_CLASSES_CACHE.put(mainClass.getName(), fieldList);

        return fieldList;
    }

    private Optional<String> getColumnNameFromField(Field field_, List<String> allColumnNames) {
        final String fieldName = field_.getName();

        if (fieldColumnNameMapCache.containsKey(fieldName)) {
            return fieldColumnNameMapCache.get(fieldName);
        }

        final String convertedFieldNameCol = StringUtil.convertSnakeCaseFromCamelCase(fieldName);

        final Optional<String> columnNameOpt = allColumnNames.stream()
                .filter(it -> it.equalsIgnoreCase(convertedFieldNameCol))
                .findFirst();

        fieldColumnNameMapCache.put(fieldName, columnNameOpt);

        return columnNameOpt;
    }

    private Optional<ThrowableFunction<Object, Object>> getCustomConverterOfColumn(String columnName) {
        if (StringValidator.isBlank(columnName)) {
            return Optional.empty();
        }

        final Optional<String> converterColumnNameOpt = customConverters.keySet()
                .stream()
                .filter(it -> columnName.equalsIgnoreCase(String.valueOf(it)))
                .findFirst();

        return converterColumnNameOpt.map(customConverters::get);

    }

    private List<String> getAllColumnNames(ResultSet resultSet) throws SQLException {
        if (this.allColumnNamesCache.isEmpty()) {
            this.allColumnNamesCache.addAll(
                    ExtractResultToPage.getAllColumnNames(resultSet)
                            .stream()
                            .map(it -> StringUtil.trimWithSpecificCharacter(it, "_"))
                            .toList()
            );
        }

        return this.allColumnNamesCache;
    }

    private <T> T fillDataForInstance(Class<T> mainClass, T instance_, ResultSet resultSet) throws SQLException {
        final List<String> allColumnNames = getAllColumnNames(resultSet);
        final List<Field> fieldsOfClass = getFieldsOfClass(mainClass);

        for (Field field_ : fieldsOfClass) {
            try {
                final Optional<String> columnNameFromFieldOpt = getColumnNameFromField(field_, allColumnNames);
                if (columnNameFromFieldOpt.isEmpty()) {
                    throw new NoSuchFieldException("Not found column name for field: " + field_.getName());
                }

                final String columnName = columnNameFromFieldOpt.get();
                final Object rawObject = resultSet.getObject(columnName);

                final Optional<ThrowableFunction<Object, Object>> customConverterOfColumn = getCustomConverterOfColumn(columnName);
                final Object convertedObject = customConverterOfColumn.isEmpty()
                        ? CastUtil.safeCast(rawObject, field_.getType())
                        : customConverterOfColumn
                        .get()
                        .throwableApply(rawObject);

                field_.setAccessible(true);
                field_.set(instance_, convertedObject);
            } catch (Throwable ex) {
                if (!isIgnoreWarningMissingField) {
                    getLogger(this).warn("No column with field name [%s]. Error: %s".formatted(field_.getName(), ex));
                }
            }
        }
        return instance_;
    }


    public <T> T buildCurrent(Class<T> mainClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        if (null == mainClass) {
            return null;
        }

        final T instance_ = newInstanceWithNoArgs(mainClass);
        return fillDataForInstance(mainClass, instance_, resultSet);
    }

    public <T> List<T> buildList(Class<T> mainClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        final List<T> resultList = new ArrayList<>();

        if (null != mainClass) {
            if (resultSet.next()) {
                resultList.add(
                        buildCurrent(mainClass)
                );
            }
        }

        return resultList;
    }
}
