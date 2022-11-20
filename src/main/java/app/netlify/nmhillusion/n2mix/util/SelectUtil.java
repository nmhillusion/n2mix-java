package app.netlify.nmhillusion.n2mix.util;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * date: 2021-08-05
 * <p>
 * created-by: nmhillusion
 */

public abstract class SelectUtil {
    @NonNull
    public static <T> T getOrDefault(@Nullable T nullableValue, @NonNull T defaultValue) {
        if (null != nullableValue) {
            return nullableValue;
        } else {
            return defaultValue;
        }
    }
}
