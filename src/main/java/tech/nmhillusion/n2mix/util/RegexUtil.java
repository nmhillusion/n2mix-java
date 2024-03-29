package tech.nmhillusion.n2mix.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * date: 2022-11-18
 * <p>
 * created-by: nmhillusion
 */
public abstract class RegexUtil {

    public static List<List<String>> parse(String input, String regexPattern, int patternFlags) {
        final List<List<String>> parsedList = new ArrayList<>();

        final Pattern pattern_ = Pattern.compile(regexPattern, patternFlags);
        final Matcher matcher_ = pattern_.matcher(input);

        while (matcher_.find()) {
            final List<String> matchedItem = new ArrayList<>();
            for (int matchedIdx = 0; matchedIdx <= matcher_.groupCount(); ++matchedIdx) {
                matchedItem.add(matcher_.group(matchedIdx));
            }
            parsedList.add(matchedItem);
        }

        return parsedList;
    }

}
