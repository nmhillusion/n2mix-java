package tech.nmhillusion.n2mix.type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * date: 2022-09-25
 * <p>
 * created-by: nmhillusion
 */

public class Stringeable implements Serializable {
    private static final Map<String, List<Field>> CACHED__CLASS_FIELDS = new TreeMap<>();
    private static final List<String> pendingObjects = new CopyOnWriteArrayList<>();

    private List<Field> getFieldsOfClass() {
        if (CACHED__CLASS_FIELDS.containsKey(getClass().getName())) {
            return CACHED__CLASS_FIELDS.get(getClass().getName());
        }

        final Field[] declaredFields = getClass().getDeclaredFields();

        final List<Field> fieldList = new ArrayList<>(Arrays.asList(declaredFields));

        Class<?> superClass = getClass().getSuperclass();
        while (!superClass.equals(Object.class)) {
            if (!superClass.equals(Stringeable.class)) {
                fieldList.addAll(
                        Stream.of(
                                        superClass.getDeclaredFields()
                                )
                                .toList()
                );
            }

            superClass = superClass.getSuperclass();
        }

        CACHED__CLASS_FIELDS.put(getClass().getName(), fieldList);

        return fieldList;
    }

    private String generateIdKeyForObject() {
        return getClass().getName() + "$" + System.identityHashCode(this);
    }

    @Override
    public String toString() {
        final String currentHashCode = generateIdKeyForObject();

        if (pendingObjects.contains(currentHashCode)) {
            return getClass().getSimpleName();
        }
        pendingObjects.add(currentHashCode);

        final Map<String, Object> fieldMap = new HashMap<>();

        final List<Field> fieldsOfClass = getFieldsOfClass();
        for (Field field_ : fieldsOfClass) {
            try {
                field_.setAccessible(true);
                final Object fieldValue_ = field_.get(this);
                fieldMap.put(field_.getName(), fieldValue_);

            } catch (Throwable e) {
                getLogger(this).error(e);
                fieldMap.put(field_.getName(), "!!!Error: IllegalAccessException");
            }
        }

        final String finalContent = "$className$fields"
                .replace("$className", getClass().getSimpleName())
                .replace("$fields", fieldMap.toString());

        pendingObjects.remove(currentHashCode);
        return finalContent;
    }
}
