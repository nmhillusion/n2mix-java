package app.netlify.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}