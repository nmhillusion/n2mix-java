package app.netlify.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberUtilTest {

    @Test
    void isNumber() {
        assertFalse(NumberUtil.isNumber("*"));
        assertFalse(NumberUtil.isNumber("ab"));
        assertFalse(NumberUtil.isNumber("4ee4"));
        assertFalse(NumberUtil.isNumber("5-6"));

        assertTrue(NumberUtil.isNumber("5"));
        assertTrue(NumberUtil.isNumber("5e6"));
        assertTrue(NumberUtil.isNumber("5.9"));
        assertTrue(NumberUtil.isNumber("5643756843564785643"));
    }

    @Test
    void isInteger() {
        assertTrue(NumberUtil.isInteger(12));
        assertTrue(NumberUtil.isInteger("8"));
        assertTrue(NumberUtil.isInteger(Integer.valueOf("9")));

        assertFalse(NumberUtil.isInteger(null));
        assertFalse(NumberUtil.isInteger(new Object()));
        assertFalse(NumberUtil.isInteger(0.9));
        assertFalse(NumberUtil.isInteger("12.8"));
    }

    @Test
    void isLong() {
        assertTrue(NumberUtil.isLong(1200000000125L));
        assertTrue(NumberUtil.isLong("1200000000925"));
        assertTrue(NumberUtil.isLong(Long.valueOf("9")));

        assertFalse(NumberUtil.isLong(null));
        assertFalse(NumberUtil.isLong(new Object()));
        assertFalse(NumberUtil.isLong(0.9));
        assertFalse(NumberUtil.isLong("12.8"));
    }

    @Test
    void isFloat() {
        assertTrue(NumberUtil.isFloat(12.7f));
        assertTrue(NumberUtil.isFloat("8.0"));
        assertTrue(NumberUtil.isFloat(new Float("0.9")));

        assertFalse(NumberUtil.isFloat(null));
        assertFalse(NumberUtil.isFloat(new Object()));
    }

    @Test
    void isDouble() {
        assertTrue(NumberUtil.isDouble(1288888888888888888888.7));
        assertTrue(NumberUtil.isDouble("800102304043.01"));
        assertTrue(NumberUtil.isDouble(new Float("0.9")));

        assertFalse(NumberUtil.isDouble(null));
        assertFalse(NumberUtil.isDouble(new Object()));
    }
}