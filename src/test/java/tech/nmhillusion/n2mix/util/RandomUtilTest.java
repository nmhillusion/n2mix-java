package tech.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilTest {

    @Test
    void generateRandomString() {
        final int LENGTH = 8;
        String ranString = RandomUtil.generateRandomString(LENGTH);
        assertNotNull(ranString);
        assertEquals(LENGTH, ranString.length());
    }

    @Test
    void nextInt() {
        Integer randNumber = RandomUtil.nextInt();
        assertNotNull(randNumber);

        Integer randNumberInBound = RandomUtil.nextInt(20);
        assertNotNull(randNumberInBound);
        assertTrue(randNumberInBound < 20);

        System.out.println("randNumber: " + randNumber);
        System.out.println("randNumberInBound: " + randNumberInBound);
    }

    @Test
    void generatedOTP() {
        final int LENGTH = 9;
        String otp = RandomUtil.generatedOTP(LENGTH);
        assertNotNull(otp);
        assertEquals(otp.length(), LENGTH);
        assertDoesNotThrow(() -> {
            long parsedLong = Long.parseLong(otp);
            System.out.println("parsedLong: " + parsedLong);
        });
    }
}