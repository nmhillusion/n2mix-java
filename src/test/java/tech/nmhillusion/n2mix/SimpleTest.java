package tech.nmhillusion.n2mix;

import tech.nmhillusion.n2mix.helper.log.LogHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * date: 2022-10-03
 * <p>
 * created-by: nmhillusion
 */

public class SimpleTest {
    @Test
    void testDateTimeFormatter() {
        final String formattedDateTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        LogHelper.getLogger(this, true).info("formattedDateTime -> " + formattedDateTime);
        Assertions.assertNotNull(formattedDateTime);
        Assertions.assertTrue(0 < formattedDateTime.trim().length());

        Assertions.assertEquals(3, Math.ceil((float)7/3));
    }
}
