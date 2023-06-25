package app.netlify.nmhillusion.n2mix.util;

import app.netlify.nmhillusion.n2mix.type.ChainList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SelectUtilTest {
    @Test
    public void testGetOrDefault() {
        final String orDefault1 = SelectUtil.getOrDefault(null, "90");
        assertEquals(orDefault1, "90");

        final String orDefault2 = SelectUtil.getOrDefault("abc", "90");
        assertEquals(orDefault2, "abc");

        final int orDefault3 = SelectUtil.getOrDefault(null, 1);
        assertEquals(orDefault3, 1);

        final int orDefault4 = SelectUtil.getOrDefault(4, 1);
        assertEquals(orDefault4, 4);
    }

    @Test
    void testGetFirstValueNotNullArgv() {
        assertEquals("abc", SelectUtil.getFirstValueNotNullArgv("abc", "def", "ghi"));
        assertNotEquals("abc", SelectUtil.getFirstValueNotNullArgv("bbc", "abc", "def", "ghi"));
        assertNotEquals("abc", SelectUtil.getFirstValueNotNullArgv());
        assertNull(SelectUtil.getFirstValueNotNullArgv());
    }

    @Test
    void testGetFirstValueNotNull() {
        assertEquals("abc", SelectUtil.getFirstValueNotNull(new ChainList<>()
                .chainAdd("abc")
                .chainAdd("bcd")
        ));
        assertNotEquals("abc", SelectUtil.getFirstValueNotNull(
                new ChainList<>()
                        .chainAdd("def")
                        .chainAdd("bcd")

        ));
        assertNotEquals("abc", SelectUtil.getFirstValueNotNull(new ArrayList<>()));
        assertNull(SelectUtil.getFirstValueNotNull(new ArrayList<>()));
        assertNull(SelectUtil.getFirstValueNotNull(null));
    }
}