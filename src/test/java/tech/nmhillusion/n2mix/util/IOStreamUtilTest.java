package tech.nmhillusion.n2mix.util;

import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.validator.StringValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

class IOStreamUtilTest {

    @Test
    void nullStreamTest(){
        Assertions.assertDoesNotThrow(() -> {
            byte[] outData1 = IOStreamUtil.convertInputStreamToByteArray(null);

            Assertions.assertEquals(0, outData1.length);
        });
    }

    @Test
    void convertStreamToByteArray() {
        byte[] out = new byte[0];
        try {
            out = IOStreamUtil.convertInputStreamToByteArray(new ByteArrayInputStream(new byte[]{
                    1, 67, 3, 25, 65, 89, 3, 5, 78, 111, 78, 90
            }));
            LogHelper.getLogger(this).info(" file size: " + out.length);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assertions.assertNotEquals(out.length, 0);
    }

    @Test
    void convertStreamToString() {
        String result = "";
        try {
            result = IOStreamUtil.convertInputStreamToString(new ByteArrayInputStream("Data is very interesting!".getBytes()));
            LogHelper.getLogger(this).info(" result: " + result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assertions.assertFalse(StringValidator.isBlank(result));
    }

    @Test
    void convertStreamToBase64() {
        String result = "";
        try {
            result =
                    IOStreamUtil.convertInputStreamToBase64(new ByteArrayInputStream("Data is very interesting!".getBytes()));
            LogHelper.getLogger(this).info(" result: " + result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assertions.assertFalse(StringValidator.isBlank(result));
    }

}