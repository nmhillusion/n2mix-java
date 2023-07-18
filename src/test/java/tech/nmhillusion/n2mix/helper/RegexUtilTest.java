package tech.nmhillusion.n2mix.helper;

import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.util.RegexUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexUtilTest {

    @Test
    void parse() {
        final List<List<String>> actualList = RegexUtil.parse("a-b-c-d", "(?:-?(\\w))", Pattern.CASE_INSENSITIVE);
        assertEquals(4, actualList.size(), "size of matcher");

        final List<List<String>> expectedList = new ArrayList<>();
        {
            expectedList.add(Arrays.asList("a", "a"));
            expectedList.add(Arrays.asList("-b", "b"));
            expectedList.add(Arrays.asList("-c", "c"));
            expectedList.add(Arrays.asList("-d", "d"));
        }

        assertEquals(expectedList, actualList);

        LogHelper.getLogger(this).info("actualList: " + actualList);
    }
}