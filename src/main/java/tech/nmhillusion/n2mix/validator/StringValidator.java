package tech.nmhillusion.n2mix.validator;

import tech.nmhillusion.n2mix.helper.log.LogHelper;

/**
 * date: 2022-10-08
 * <p>
 * created-by: nmhillusion
 */

public class StringValidator {
    public static boolean isBlank(String inp) {
        return null == inp || 0 == inp.trim().length();
    }

    public static boolean isValidStringId(String stringId) {
        final boolean valid = stringId.matches("[\\w-_]+");
        if (!valid) {
            LogHelper.getLogger(StringValidator.class).debug("stringId is not valid: " + stringId);
        }
        return valid;
    }
}
