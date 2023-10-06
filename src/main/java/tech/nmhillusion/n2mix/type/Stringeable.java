package tech.nmhillusion.n2mix.type;

import tech.nmhillusion.n2mix.helper.log.LogHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

/**
 * date: 2022-09-25
 * <p>
 * created-by: nmhillusion
 */

public class Stringeable implements Serializable {
    private static final Map<String, List<Field>> CACHED__CLASS_FIELDS = new TreeMap<>();

    private List<Field> getFieldsOfClass() {
        if (CACHED__CLASS_FIELDS.containsKey(getClass().getName())) {
            return CACHED__CLASS_FIELDS.get(getClass().getName());
        }

        final Field[] declaredFields = getClass().getDeclaredFields();

        final List<Field> fieldList = Stream.of(
                        declaredFields
                )
                .toList();

        CACHED__CLASS_FIELDS.put(getClass().getName(), fieldList);

        return fieldList;
    }

    private Object getValueOfField(Field field_) throws IllegalAccessException {
        if (null == field_) {
            return null;
        }

        if (Stream.of(byte.class,
                        char.class,
                        short.class,
                        int.class,
                        long.class,
                        float.class,
                        double.class,
                        CharSequence.class
                )
                .anyMatch(cls -> cls.isAssignableFrom(field_.getType()))
        ) {
            return field_.get(this);
        } else if (Iterable.class.isAssignableFrom(field_.getType())) {
            final ParameterizedType genericType = (ParameterizedType) field_.getGenericType();
            return "List of %s".formatted(Arrays.toString(genericType.getActualTypeArguments()));
        } else if (Map.class.isAssignableFrom(field_.getType())) {
            final ParameterizedType genericType = (ParameterizedType) field_.getGenericType();
            return "Map of %s".formatted(Arrays.toString(genericType.getActualTypeArguments()));
        } else {
            return "%s@%s".formatted(getClass().getName(), field_.get(this).hashCode());
        }
    }

    @Override
    public String toString() {
        final Map<String, Object> fieldMap = new HashMap<>();

        final List<Field> fieldsOfClass = getFieldsOfClass();
        for (Field field_ : fieldsOfClass) {
            try {
                field_.setAccessible(true);
                fieldMap.put(field_.getName(), getValueOfField(field_));
            } catch (Throwable e) {
                LogHelper.getLogger(this).error(e);
                fieldMap.put(field_.getName(), "!!!Error: IllegalAccessException");
            }
        }

        return "class $className $fields"
                .replace("$className", getClass().getName())
                .replace("$fields", fieldMap.toString());
    }
}
