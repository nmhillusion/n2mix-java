package tech.nmhillusion.n2mix.type;

import tech.nmhillusion.n2mix.helper.log.LogHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
                /// Mark: IGNORE IF FIELD HAS THE SAME CLASS WITH ITSELF
                .filter(f_ -> !f_.getType().isAssignableFrom(getClass()))
                .toList();

        CACHED__CLASS_FIELDS.put(getClass().getName(), fieldList);

        return fieldList;
    }

    @Override
    public String toString() {
        final Map<String, Object> fieldMap = new HashMap<>();

        final List<Field> fieldsOfClass = getFieldsOfClass();
        for (Field field : fieldsOfClass) {
            try {
                field.setAccessible(true);
                fieldMap.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                LogHelper.getLogger(this).error(e);
                fieldMap.put(field.getName(), "!!!Error: IllegalAccessException");
            }
        }

        return "class $className $fields"
                .replace("$className", getClass().getName())
                .replace("$fields", fieldMap.toString());
    }
}
