package tech.nmhillusion.n2mix.util;

import tech.nmhillusion.n2mix.validator.StringValidator;
import org.springframework.lang.Nullable;
import tech.nmhillusion.n2mix.type.FunctionalFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static tech.nmhillusion.n2mix.type.FunctionalFactory.not;

/**
 * date: 2022-11-19
 * <p>
 * created-by: nmhillusion
 */

public abstract class StringUtil {
    public static final String EMPTY = "";

    public static String trimWithNull(@Nullable Object input) {
        if (null == input) {
            return EMPTY;
        }
        final String strInput = String.valueOf(input);

        if (StringValidator.isBlank(strInput)) {
            return EMPTY;
        }
        return strInput
                .trim()
                ;
    }

    public static String removeHtmlTag(@Nullable String input) {
        input = null == input ? "" : input;

        input = input.replaceAll("</?.+?>", "");

        return input;
    }

    public static String getFirstValueNotNull(Object... optionalValues) {
        String strValue = "";
        if (!CollectionUtil.isNullOrEmptyArgv(optionalValues)) {
            for (Object rOptionalValue : optionalValues) {
                final String optionalValue = String.valueOf(rOptionalValue);
                if (null != rOptionalValue && !StringValidator.isBlank(optionalValue)) {
                    strValue = optionalValue;
                    break;
                }
            }
        }
        return strValue;
    }

    /**
     * oneTwo -> OneTwo
     *
     * @param camelCase
     * @return pascalCase of input string
     */
    public static String convertPascalCaseFromCamelCase(String camelCase) {
        if (StringValidator.isBlank(camelCase)) {
            return camelCase;
        }
        if (1 == camelCase.length()) {
            return camelCase.toUpperCase();
        }

        String firstChar = String.valueOf(camelCase.charAt(0));
        String remainString = camelCase.substring(1);

        return firstChar.toUpperCase() + remainString;
    }

    /**
     * OneTwo -> oneTwo
     *
     * @param pascalCase
     * @return camelCase of input string
     */
    public static String convertCamelCaseFromPascalCase(String pascalCase) {
        if (StringValidator.isBlank(pascalCase)) {
            return pascalCase;
        }
        if (1 == pascalCase.length()) {
            return pascalCase.toLowerCase();
        }

        String firstChar = String.valueOf(pascalCase.charAt(0));
        String remainString = pascalCase.substring(1);

        return firstChar.toLowerCase() + remainString;
    }

    public static String truncate(String value, int maxLength) {
        if (null == value || maxLength >= value.length()) {
            return value;
        }

        return value.substring(0, maxLength) + "...";
    }

    public static String format(String template, Object... args) {
        template = template.replaceAll("%([^abcdefghnostx])", "%%$1");
        return String.format(template, args);
    }

    public static boolean trimCompare(String firstString, String secondString) {
        return trimCompare(firstString, secondString, false);
    }

    public static boolean trimCompare(String firstString, String secondString, boolean ignoreCase) {
        firstString = String.valueOf(firstString).trim();
        secondString = String.valueOf(secondString).trim();

        if (ignoreCase) {
            return firstString.equalsIgnoreCase(secondString);
        } else {
            return firstString.equals(secondString);
        }
    }

    public static String rebuildNumberIdsFromString(String input) {
        final List<String> result = new ArrayList<>();
        if (!StringValidator.isBlank(input)) {
            result.addAll(
                    Arrays.stream(input.split(","))
                            .filter(FunctionalFactory.not(StringValidator::isBlank))
                            .map(String::trim)
                            .filter(NumberUtil::isLong)
                            .collect(Collectors.toList())
            );
        }
        return String.join(",", result);
    }

    public static String rebuildStringIdsFromString(String input) {
        final List<String> result = new ArrayList<>();
        if (!StringValidator.isBlank(input)) {
            result.addAll(
                    Arrays.stream(input.split(","))
                            .filter(FunctionalFactory.not(StringValidator::isBlank))
                            .filter(StringValidator::isValidStringId)
                            .map(item -> "'" + item.trim() + "'")
                            .collect(Collectors.toList())
            );
        }
        return String.join(",", result);
    }
}
