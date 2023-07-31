package tech.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Base64;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

class IOStreamUtilTest {
    
    @Test
    void nullStreamTest() {
        Assertions.assertDoesNotThrow(() -> {
            byte[] outData1 = IOStreamUtil.convertInputStreamToByteArray(null);
            
            Assertions.assertEquals(0, outData1.length);
        });
    }
    
    @Test
    void convertStreamToByteArray() {
        Assertions.assertDoesNotThrow(() -> {
            final byte[] expected = {1, 67, 3, 25, 65, 89, 3, 5, 78, 111, 78, 90};
            final byte[] actual = IOStreamUtil.convertInputStreamToByteArray(new ByteArrayInputStream(expected));
            getLogger(this).info(" file size: %s, value: %s".formatted(actual.length, Arrays.toString(actual)));
            Assertions.assertNotEquals(0, actual.length);
            Assertions.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
        });
    }
    
    @Test
    void convertStreamToString() {
        Assertions.assertDoesNotThrow(() -> {
            final String expected = "Data is very interesting!";
            final String actual = IOStreamUtil.convertInputStreamToString(new ByteArrayInputStream(expected.getBytes()));
            getLogger(this).info(" result: " + actual);
            Assertions.assertFalse(StringValidator.isBlank(actual));
            Assertions.assertEquals(expected, actual);
        });
    }
    
    @Test
    void convertStreamToBase64() {
        Assertions.assertDoesNotThrow(() -> {
            final String expected = "Data is very interesting!";
            final String actualBase64 = IOStreamUtil.convertInputStreamToBase64(new ByteArrayInputStream(expected.getBytes()));
            getLogger(this).info(" result: " + actualBase64);
            Assertions.assertFalse(StringValidator.isBlank(actualBase64));
            Assertions.assertEquals(expected, new String(Base64.getDecoder().decode(actualBase64)));
        });
    }
    
}