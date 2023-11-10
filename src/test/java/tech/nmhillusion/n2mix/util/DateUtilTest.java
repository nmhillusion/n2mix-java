package tech.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.log.LogHelper;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

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

    @Test
    void convertToZonedDateTime() {
        final Date date_ = new Date(System.currentTimeMillis());
        final ZonedDateTime zonedDateTime = DateUtil.convertToZonedDateTime(date_);
        assertEquals(zonedDateTime.toInstant().toEpochMilli(), date_.getTime());
        getLogger(this).info("timestamp: %s -> %s".formatted(date_, zonedDateTime));
        final int hour_ = zonedDateTime.getHour();
        final int minute_ = zonedDateTime.getMinute();
        final int second_ = zonedDateTime.getSecond();

        getLogger(this).info("zoned date time  - time(HH:mm:ss): %s:%s:%s".formatted(hour_, minute_, second_));
    }

    @Test
    void convertToZonedDateTimeWithNull() {
        assertNull(DateUtil.convertToZonedDateTime(null));
    }

    @Test
    void convertZonedDateTimeToDate() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        final Date date_ = DateUtil.convertZonedDateTimeToDate(zonedDateTime);

        assertEquals(date_.getTime(), zonedDateTime.toInstant().toEpochMilli());
        getLogger(this).info("timestamp: %s -> %s".formatted(zonedDateTime, date_));

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date_);
        final int hour_ = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute_ = calendar.get(Calendar.MINUTE);
        final int second_ = calendar.get(Calendar.SECOND);

        getLogger(this).info("zoned date time  - time(HH:mm:ss): %s:%s:%s".formatted(hour_, minute_, second_));
    }

    @Test
    void convertZonedDateTimeToDateWithNull() {
        assertNull(DateUtil.convertZonedDateTimeToDate(null));
    }

    @Test
    void testMassConvertDate() {
        final int MAX_TIMES = 100_000;

        Assertions.assertDoesNotThrow(() -> {
            final Calendar calendar_ = Calendar.getInstance();
            calendar_.clear();
            calendar_.set(2022, Calendar.SEPTEMBER, 10, 0, 0, 0);
            final Date expectedDate_ = calendar_.getTime();

            final Executor executor_ = Executors.newWorkStealingPool();
            final AtomicBoolean finishedRef = new AtomicBoolean(false);
            final AtomicBoolean successExecution = new AtomicBoolean(true);

            for (int idx = 0; idx < MAX_TIMES; idx++) {
                final int finalIdx = idx;
                executor_.execute(() -> {
                    LogHelper.getLogger(this).info("test on times #" + finalIdx);

                    final String rawDate = "10/09/2022";
                    final String dateFormat = "dd/MM/yyyy";
                    if (!expectedDate_.equals(DateUtil.parse(rawDate, dateFormat))) {
                        finishedRef.set(true);
                        successExecution.set(false);
                        throw new RuntimeException("Dates are not equal");
                    }

                    if (finalIdx + 1 == MAX_TIMES) {
                        finishedRef.set(true);
                    }
                });
            }

            while (!finishedRef.get()) ;

            assertTrue(successExecution.get());
        });

        System.gc();
    }
}