package app.netlify.nmhillusion.n2mix.util;

import org.springframework.lang.Nullable;

import java.util.*;

/**
 * date: 2021-10-05
 * <p>
 * created-by: nmhillusion
 */

public abstract class CollectionUtil {
    public static boolean isNullOrEmpty(@Nullable Collection<?> collection) {
        return null == collection || collection.isEmpty();
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

    public static <T> Collection<T> getOrDefaultEmpty(@Nullable Collection<T> items) {
        if (null == items) {
            items = new ArrayList<>();
        }
        return items;
    }

    public static <T> T getFirstOfList(@Nullable List<T> items) {
        T item = null;

        if (!isNullOrEmpty(items)) {
            item = items.get(0);
        }

        return item;
    }

    public static <T> List<T> listFromIterator(Iterator<T> iterator) {
        final List<T> resultList = new ArrayList<>();

        while (iterator.hasNext()) {
            resultList.add(iterator.next());
        }

        return resultList;
    }
}
