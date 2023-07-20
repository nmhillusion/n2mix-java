package tech.nmhillusion.n2mix.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * date: 2023-07-20
 * <p>
 * created-by: nmhillusion
 */

public abstract class CloneHelper {
    @SuppressWarnings({"unchecked"})
    public static <T> T clone(T inp) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Class<T> class_ = (Class<T>) inp.getClass();
        
        final Constructor<T> noParamConstructor = class_.getConstructor();
        final T newInstance = noParamConstructor.newInstance();
        
        final Field[] declaredFields = class_.getDeclaredFields();
        for (Field field_ : declaredFields) {
            field_.setAccessible(true);
            field_.set(newInstance, field_.get(inp));
        }
        
        final Field[] fields = class_.getFields();
        for (Field field_ : fields) {
            field_.setAccessible(true);
            field_.set(newInstance, field_.get(inp));
        }
        
        return newInstance;
    }
}
