package app.netlify.nmhillusion.n2mix.util;

import app.netlify.nmhillusion.n2mix.helper.log.LogHelper;

/**
 * date: 2020-09-28
 * created-by: nmhillusion
 */

public abstract class CastUtil {
    public static <T> T safeCast(Object value, Class<T> classToCast) throws ClassCastException {
        T result;
        try {
            if (classToCast.isAssignableFrom(String.class)) {
                result = classToCast.cast(String.valueOf(value));
            } else if (classToCast.isAssignableFrom(Integer.class) || classToCast.isAssignableFrom(int.class)) {
                result = classToCast.cast(Integer.parseInt(String.valueOf(value)));
            } else if (classToCast.isAssignableFrom(Long.class) || classToCast.isAssignableFrom(long.class)) {
                result = classToCast.cast(Long.parseLong(String.valueOf(value)));
            } else if (classToCast.isAssignableFrom(Float.class) || classToCast.isAssignableFrom(float.class)) {
                result = classToCast.cast(Float.parseFloat(String.valueOf(value)));
            } else if (classToCast.isAssignableFrom(Double.class) || classToCast.isAssignableFrom(double.class)) {
                result = classToCast.cast(Double.parseDouble(String.valueOf(value)));
            } else {
                result = classToCast.cast(value);
            }
        } catch (ClassCastException ex) {
            LogHelper.getLog(CastUtil.class).error(ex);
            throw ex;
        }
        return result;
    }
}
