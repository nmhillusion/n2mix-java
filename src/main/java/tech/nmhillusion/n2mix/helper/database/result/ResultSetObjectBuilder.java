package tech.nmhillusion.n2mix.helper.database.result;

import tech.nmhillusion.n2mix.annotation.IgnoredField;
import tech.nmhillusion.n2mix.helper.database.query.ExtractResultToPage;
import tech.nmhillusion.n2mix.type.Pair;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;
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
    private final Map<String, ThrowableFunction<Object, Object>> fieldCustomConverters = new TreeMap<>();
    private final List<String> allColumnNamesCache = new ArrayList<>();
    private final Map<String, Optional<String>> fieldColumnNameMapCache = new TreeMap<>();
    private final ResultSet resultSet;
    private boolean isIgnoreMissingField = true;

    public ResultSetObjectBuilder(ResultSet resultSet) {
        if (null == resultSet) {
            throw new IllegalArgumentException("ResultSet must be not null to initialize");
        }
        this.resultSet = resultSet;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public Map<String, ThrowableFunction<Object, Object>> getFieldCustomConverters() {
        return fieldCustomConverters;
    }

    public ResultSetObjectBuilder addFieldCustomConverters(String fieldName, ThrowableFunction<Object, Object> customConverter) {
        if (StringValidator.isBlank(fieldName) || null == customConverter) {
            return this;
        }

        this.fieldCustomConverters.put(fieldName.toLowerCase(), customConverter);
        return this;
    }

    public ResultSetObjectBuilder setIsIgnoreMissingField(boolean ignoreMissingField) {
        isIgnoreMissingField = ignoreMissingField;
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

    private Optional<ThrowableFunction<Object, Object>> getCustomConverterOfField(Field field_) {
        if (null == field_) {
            return Optional.empty();
        }

        final String fieldName = field_.getName();
        if (StringValidator.isBlank(fieldName)) {
            return Optional.empty();
        }

        final Optional<String> converterColumnNameOpt = fieldCustomConverters.keySet()
                .stream()
                .filter(it -> fieldName.equalsIgnoreCase(String.valueOf(it)))
                .findFirst();

        return converterColumnNameOpt.map(fieldCustomConverters::get);

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

    private <T> T fillDataForInstance(Class<T> mainClass, T instance_, ResultSet resultSet) throws SQLException, NoSuchFieldException {
        final List<String> allColumnNames = getAllColumnNames(resultSet);
        final List<Field> fieldsOfClass = getFieldsOfClass(mainClass);

        for (Field field_ : fieldsOfClass) {
            final Optional<String> columnNameFromFieldOpt = getColumnNameFromField(field_, allColumnNames);
            if (columnNameFromFieldOpt.isEmpty()) {
                if (!isIgnoreMissingField) {
                    throw new NoSuchFieldException("Not found column name for field: " + field_.getName());
                } else {
                    continue;
                }
            }

            try {
                final String columnName = columnNameFromFieldOpt.get();
                final Object rawObject = resultSet.getObject(columnName);

                final Optional<ThrowableFunction<Object, Object>> customConverterOfColumn = getCustomConverterOfField(field_);
                final Object convertedObject = customConverterOfColumn.isEmpty()
                        ? CastUtil.safeCast(rawObject, field_.getType())
                        : customConverterOfColumn
                        .get()
                        .throwableApply(rawObject);

                field_.setAccessible(true);
                field_.set(instance_, convertedObject);
            } catch (Throwable ex) {
                throw new SQLException(ex);
            }
        }
        return instance_;
    }


    public <T> T buildCurrent(Class<T> mainClass) throws SQLException, NoSuchFieldException {
        if (0 == resultSet.getRow()) {
            throw new SQLException("Current ResultSet is not valid to obtain data. Maybe not call to ResultSet.next() yet.");
        }

        if (null == mainClass) {
            return null;
        }

        try {
            final T instance_ = newInstanceWithNoArgs(mainClass);
            return fillDataForInstance(mainClass, instance_, resultSet);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException ex) {
            throw new SQLException(ex);
        }
    }

    public <T> List<T> buildList(Class<T> mainClass) throws Throwable {
        return buildList(mainClass, null);
    }

    public <T> List<T> buildList(Class<T> mainClass, ResultSetObjectBuilderCallback<T> callbackFunc) throws Throwable {
        final List<T> resultList = new ArrayList<>();

        if (null != mainClass) {
            while (resultSet.next()) {
                if (null == callbackFunc) {
                    resultList.add(
                            buildCurrent(mainClass)
                    );
                } else {
                    final T currentItem = buildCurrent(mainClass);
                    resultList.add(
                            currentItem
                    );

                    callbackFunc.throwableApply(currentItem, resultSet);
                }
            }
        }

        return resultList;
    }
}
