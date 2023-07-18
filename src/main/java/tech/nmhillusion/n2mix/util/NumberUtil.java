package tech.nmhillusion.n2mix.util;

/**
 * date: 2021-09-20
 * <p>
 * created-by: nmhillusion
 */

public abstract class NumberUtil {
    public static boolean isNumber(Object rawValue) {
        return isDouble(rawValue)
                || isFloat(rawValue)
                || isInteger(rawValue)
                || isLong(rawValue);
    }

    public static boolean isInteger(Object rawValue) {
        try {
            Integer.parseInt(String.valueOf(rawValue));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isLong(Object rawValue) {
        try {
            Long.parseLong(String.valueOf(rawValue));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isFloat(Object rawValue) {
        try {
            Float.parseFloat(String.valueOf(rawValue));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isDouble(Object rawValue) {
        try {
            Double.parseDouble(String.valueOf(rawValue));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
