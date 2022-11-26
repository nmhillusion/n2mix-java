package app.netlify.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTest {

    @Test
    void format() {
        Calendar cal = Calendar.getInstance();
        cal.set(2021, Calendar.MAY, 14, 12, 45, 4);
        Date date = cal.getTime();

        String pattern1 = "dd/MM/yyyy";
        String pattern2 = "M-d-yy";
        String pattern3 = "HH:mm:ss";
        String pattern4 = "dd/MM/yyyy HH:mm:ss";

        assertEquals("14/05/2021", DateUtil.format(date, pattern1));
        assertEquals("5-14-21", DateUtil.format(date, pattern2));
        assertEquals("12:45:04", DateUtil.format(date, pattern3));
        assertEquals("14/05/2021 12:45:04", DateUtil.format(date, pattern4));
    }

    @Test
    void parse() {
        String rawDate = "09/11/1998";

        String pattern = "dd/MM/yyyy";

        Calendar cal = Calendar.getInstance();
        cal.set(1998, 10, 9, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime().getTime(), DateUtil.parse(rawDate, pattern).getTime());
    }

    @Test
    void getDaysOfMonth() {
        assertEquals(31, DateUtil.getDaysOfMonth(1, 1999));
        assertEquals(30, DateUtil.getDaysOfMonth(4, 1999));
        assertEquals(31, DateUtil.getDaysOfMonth(7, 2000));
        assertEquals(31, DateUtil.getDaysOfMonth(8, 2001));
        assertEquals(31, DateUtil.getDaysOfMonth(12, 2021));
        assertEquals(28, DateUtil.getDaysOfMonth(2, 1999));
        assertEquals(28, DateUtil.getDaysOfMonth(2, 2100));
        assertEquals(29, DateUtil.getDaysOfMonth(2, 2004));
    }
}