package app.netlify.nmhillusion.n2mix.util;

import java.security.SecureRandom;
import java.util.Random;

public abstract class RandomUtil {
    private static Random randomise;

    static {
        try {
            randomise = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception ex) {
            ex.printStackTrace();
            randomise = new Random();
        }
    }

    public static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        try {
            int currentCnt = 0;
            byte[] tmpBytes = new byte[length];
            randomise.nextBytes(tmpBytes);

            while (currentCnt < length) {
                byte idxChar = (byte) (Math.abs(tmpBytes[currentCnt]) % 25);
                char startChar = randomise.nextBoolean() ? 'A' : 'a';

                String randomChar = new String(new byte[]{(byte) (idxChar + startChar)});
                builder.append(randomChar);
                ++currentCnt;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    public static int nextInt(int bound) {
        return randomise.nextInt(bound);
    }

    public static int nextInt() {
        return randomise.nextInt();
    }

    public static String generatedOTP(int length) {
        Random rgen = new Random();
        byte numValue;

        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            numValue = (byte) rgen.nextInt(10);
            sb.append(numValue + "");
        }
        return sb.toString();

    }
}
