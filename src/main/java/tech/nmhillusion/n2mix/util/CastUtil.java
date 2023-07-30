package tech.nmhillusion.n2mix.util;

import tech.nmhillusion.n2mix.helper.log.LogHelper;

/**
 * date: 2020-09-28
 * created-by: nmhillusion
 */

public abstract class CastUtil {
    public static <T> T safeCast(Object value, Class<T> classToCast) {
        return safeCast(value, classToCast, null);
    }
    
    @SuppressWarnings({"unchecked"})
    public static <T> T safeCast(Object value, Class<T> classToCast, T defaultValue) {
        T result;
        try {
            if (classToCast.isAssignableFrom(Object.class)) {
                result = classToCast.cast(value);
            } else if (classToCast.isAssignableFrom(Integer.class) || classToCast.isAssignableFrom(int.class)) {
                final int intVal_ = Integer.parseInt(StringUtil.trimWithNull(value));
                result = (T) (Integer) intVal_;
            } else if (classToCast.isAssignableFrom(Long.class) || classToCast.isAssignableFrom(long.class)) {
                final long longVal_ = Long.parseLong(StringUtil.trimWithNull(value));
                result = (T) (Long) longVal_;
            } else if (classToCast.isAssignableFrom(Float.class) || classToCast.isAssignableFrom(float.class)) {
                final float floatVal_ = Float.parseFloat(StringUtil.trimWithNull(value));
                result = (T) (Float) floatVal_;
            } else if (classToCast.isAssignableFrom(Double.class) || classToCast.isAssignableFrom(double.class)) {
                final double doubleVal_ = Double.parseDouble(StringUtil.trimWithNull(value));
                result = (T) (Double) doubleVal_;
            } else if (classToCast.isAssignableFrom(Boolean.class) || classToCast.isAssignableFrom(boolean.class)) {
                final boolean booleanVal_ = Boolean.parseBoolean(StringUtil.trimWithNull(value));
                result = (T) (Boolean) booleanVal_;
            } else if (classToCast.equals(String.class)) {
                result = classToCast.cast(StringUtil.trimWithNull(value));
            } else {
                result = classToCast.cast(value);
            }
        } catch (ClassCastException ex) {
            LogHelper.getLogger(CastUtil.class).error(ex);
            result = defaultValue;
        }
        return result;
    }
}
