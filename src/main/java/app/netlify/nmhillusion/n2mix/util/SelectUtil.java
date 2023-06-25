package app.netlify.nmhillusion.n2mix.util;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * date: 2021-08-05
 * <p>
 * created-by: nmhillusion
 */

public abstract class SelectUtil {
    @NonNull
    public static <T> T getOrDefault(@Nullable T nullableValue, @NonNull T defaultValue) {
        return Objects.requireNonNullElse(nullableValue, defaultValue);
    }

    @SafeVarargs
    @Nullable
    public static <T> T getFirstValueNotNullArgv(@Nullable T... values) {
        T value = null;

        for (T item_ : CollectionUtil.getOrDefaultEmptyArgv(values)) {
            if (null != item_) {
                value = item_;
                break;
            }
        }

        return value;
    }

    @Nullable
    public static <T> T getFirstValueNotNull(@Nullable Iterable<T> values) {
        T value = null;

        for (T item_ : CollectionUtil.getOrDefaultEmpty(values)) {
            if (null != item_) {
                value = item_;
                break;
            }
        }

        return value;
    }
}
