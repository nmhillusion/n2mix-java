package tech.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HtmlUtilTest {

    @Test
    void htmlUnescape() {
        Assertions.assertEquals("abc def", HtmlUtil.htmlUnescape("abc&nbsp;def"), "test html unescape");
        Assertions.assertEquals("abc def", StringUtil.trimWithNull(HtmlUtil.htmlUnescape("&nbsp;abc&nbsp;def&nbsp;")), "test html unescape 2");
    }

    @Test
    void removeTagSyntax() {
        Assertions.assertEquals("abcdefgogo", HtmlUtil.removeTagSyntax("<b>abcdef</b><i>gogo</i>"), "test remove tag syntax");
        Assertions.assertEquals("abcdef gogo", HtmlUtil.removeTagSyntax("<b>abcdef</b>&nbsp;<i>gogo</i>"), "test remove tag syntax 2");
    }
}