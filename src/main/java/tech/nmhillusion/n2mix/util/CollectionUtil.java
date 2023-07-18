package tech.nmhillusion.n2mix.util;

import org.springframework.lang.Nullable;

import java.util.*;

/**
 * date: 2021-10-05
 * <p>
 * created-by: nmhillusion
 */

public abstract class CollectionUtil {
    public static boolean isNullOrEmpty(@Nullable Iterable<?> collection) {
        return null == collection || !collection.iterator().hasNext();
    }

    public static boolean isNullOrEmpty(@Nullable Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    public static boolean isNullOrEmpty(@Nullable Object[] data) {
        return null == data || 0 == data.length;
    }

    public static boolean isNullOrEmptyArgv(@Nullable Object... data) {
        return null == data || 0 == data.length;
    }

    public static <T> Iterable<T> getOrDefaultEmpty(@Nullable Iterable<T> items) {
        if (null == items) {
            items = new ArrayList<>();
        }
        return items;
    }

    @SafeVarargs
    public static <T> Iterable<T> getOrDefaultEmptyArgv(@Nullable T... items) {
        final List<T> items_ = new ArrayList<>();
        if (null != items) {
            Collections.addAll(items_, items);
        }
        return items_;
    }

    @SafeVarargs
    public static <T> T getFirstOfListArgv(@Nullable T... items) {
        T item = null;

        if (!isNullOrEmpty(items)) {
            item = items[0];
        }

        return item;
    }

    public static <T> T getFirstOfList(@Nullable Iterable<T> items) {
        T item = null;

        if (!isNullOrEmpty(items)) {
            item = items.iterator().next();
        }

        return item;
    }

    public static <T> List<T> listFromIterable(Iterable<T> iterator) {
        return listFromIterator(iterator.iterator());
    }

    public static <T> List<T> listFromIterator(Iterator<T> iterator) {
        final List<T> resultList = new ArrayList<>();

        if (null != iterator) {
            while (iterator.hasNext()) {
                resultList.add(iterator.next());
            }
        }

        return resultList;
    }
}
