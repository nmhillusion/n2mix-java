package tech.nmhillusion.n2mix.validator;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.type.FunctionalFactory;

import java.util.ArrayList;
import java.util.Arrays;

import static tech.nmhillusion.n2mix.type.FunctionalFactory.not;
import static org.junit.jupiter.api.Assertions.*;

class StringValidatorTest {

    @Test
    void isBlank() {
        assertTrue(StringValidator.isBlank(null));
        assertTrue(StringValidator.isBlank(""));
        assertTrue(StringValidator.isBlank("   "));

        assertFalse(StringValidator.isBlank("a"));
        assertFalse(StringValidator.isBlank("9"));
        assertFalse(StringValidator.isBlank("e "));
        assertFalse(StringValidator.isBlank(" af "));
    }

    @Test
    void isValidStringId() {
        assertTrue(StringValidator.isValidStringId("one"));
        assertTrue(StringValidator.isValidStringId("one-two"));
        assertTrue(StringValidator.isValidStringId("-one"));
        assertTrue(StringValidator.isValidStringId("oNe"));
        assertTrue(StringValidator.isValidStringId("one-8"));
        assertTrue(StringValidator.isValidStringId("one--8"));
        assertTrue(StringValidator.isValidStringId("onE_90"));
        assertTrue(StringValidator.isValidStringId("one-90-6e"));

        assertFalse(StringValidator.isValidStringId("one-90-6e#"));
        assertFalse(StringValidator.isValidStringId("one-90-6e@"));
        assertFalse(StringValidator.isValidStringId("one-90- 6e"));
        assertFalse(StringValidator.isValidStringId("one-#"));
        assertFalse(StringValidator.isValidStringId("one-0-6e@"));
        assertFalse(StringValidator.isValidStringId("one0-6e#_%"));
    }

    @Test
    void testStream() {
        final String[] filteredList = new ArrayList<String>(Arrays.asList("45", null, "hello", "", "   ", "equipment"))
                .stream()
                .filter(FunctionalFactory.not(StringValidator::isBlank))
                .map(String::trim)
                .toArray(String[]::new);

        assertArrayEquals(new String[]{"45", "hello", "equipment"}, filteredList);
    }
}