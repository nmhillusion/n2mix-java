package app.netlify.nmhillusion.n2mix.util;

import app.netlify.nmhillusion.n2mix.helper.log.LogHelper;
import app.netlify.nmhillusion.n2mix.validator.StringValidator;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * date: 2022-11-18
 * <p>
 * created-by: nmhillusion
 */

public abstract class DateUtil {
    public static final String DATEFORMAT_DDMMYYYY = "dd/MM/yyyy";
    public static final String DATEFORMAT_DDMMYYYY_h = "dd-MM-yyyy";
    public static final String YYYYMMDD_1 = "yyyy-MM-dd";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat();
    
    public static Month convertMonthFromShortNameOfMonth(String shortNameOfMonth) {
        final String trimmedMonth = StringUtil.trimWithNull(shortNameOfMonth);
        if (3 > trimmedMonth.length()) {
            return Month.JANUARY;
        }
        
        switch (trimmedMonth.substring(0, 3)) {
            case "Feb":
                return Month.FEBRUARY;
            case "Mar":
                return Month.MARCH;
            case "Apr":
                return Month.APRIL;
            case "May":
                return Month.MAY;
            case "Jun":
                return Month.JUNE;
            case "Jul":
                return Month.JULY;
            case "Aug":
                return Month.AUGUST;
            case "Sep":
                return Month.SEPTEMBER;
            case "Oct":
                return Month.OCTOBER;
            case "Nov":
                return Month.NOVEMBER;
            case "Dec":
                return Month.DECEMBER;
            default:
                return Month.JANUARY;
        }
    }
    
    public static boolean isLeapYear(int year) {
        if (0 == year % 4) {
            if (0 == year % 100) {
                return 0 == year % 400;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    
    public static LocalDate buildDateFromString(String day, String month, String year) {
        try {
            final Month month_ = DateUtil.convertMonthFromShortNameOfMonth(StringUtil.trimWithNull(month));
            
            day = StringUtil.trimWithNull(day);
            int day_ = 0 == day.length() ? 1 : Integer.parseInt(day);
            
            year = StringUtil.trimWithNull(year);
            final int year_ = 0 == year.length() ? 1 : Integer.parseInt(year);
            
            if (!isLeapYear(year_)) {
                if (Month.FEBRUARY.equals(month_)) {
                    day_ = Math.min(day_, 28);
                }
            }
            
            return LocalDate.of(year_, month_, day_);
        } catch (Exception ex) {
            LogHelper.getLogger(DateUtil.class).error(ex);
            return null;
        }
    }
    
    public static String format(Date date, String datePattern) {
        String formattedDate = "";
        try {
            if (null != date) {
                dateFormatter.applyPattern(datePattern);
                formattedDate = dateFormatter.format(date);
            }
        } catch (Exception ex) {
            LogHelper.getLogger(DateUtil.class).error(ex);
        }
        return formattedDate;
    }
    
    public static String format(Timestamp times, String datePattern) {
        return format(new Date(times.getTime()), datePattern);
    }
    
    public static Date parse(String dateText, String datePattern) {
        Date date = null;
        try {
            if (!StringValidator.isBlank(dateText) &&
                        !StringValidator.isBlank(datePattern)) {
                dateFormatter.applyPattern(datePattern);
                date = dateFormatter.parse(dateText);
            }
        } catch (Exception ex) {
            LogHelper.getLogger(DateUtil.class).error(ex);
        }
        return date;
    }
    
    public static java.sql.Date parseSqlDate(Date javaDate) {
        java.sql.Date date = null;
        try {
            if (null != javaDate) {
                date = new java.sql.Date(javaDate.getTime());
            }
        } catch (Exception ex) {
            LogHelper.getLogger(DateUtil.class);
        }
        return date;
    }
    
    //duythanh 202102
    public static int getDiffDate(Date first, Date last) {
        return (int) ((last.getTime() - first.getTime()) / (1000 * 60 * 60 * 24));
    }
    //duythanh 202102
    
    public static long getDiff2Date(Date beforeDate, Date afterDate) {
        return ChronoUnit.DAYS.between(beforeDate.toInstant().atZone(ZoneId.systemDefault()),
                afterDate.toInstant().atZone(ZoneId.systemDefault()));
    }
    
    public static Date truncateDate(Date date) {
        return parse(format(date, "yyyy-MM-dd"), "yyyy-MM-dd");
    }
    
    /**
     * @param dateText
     * @param datePattern
     * @return date in SQL Date or NULL if failed
     */
    public static java.sql.Date parseSqlDate(String dateText, String datePattern) {
        java.sql.Date date = null;
        try {
            Date javaDate = parse(dateText, datePattern);
            date = parseSqlDate(javaDate);
        } catch (Exception ex) {
            LogHelper.getLogger(DateUtil.class).error(ex);
        }
        return date;
    }
    
    public static int getDaysOfMonth(int month, int year) {
        if (month < 1 || month > 12) { // invalid month
            return 0;
        } else if (Arrays.asList(1, 3, 5, 7, 8, 10, 12).contains(month)) {
            return 31;
        } else if (2 == month) {
            if (0 != year % 100 &&
                        0 == year % 4) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 30;
        }
    }
    
    public static Date addDays(Date date, int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        return calendar.getTime();
    }
    
    /**
     * call to {@link DateUtil#convertToZonedDateTime(Date, ZoneId)} with zoneId is {@link ZoneId#systemDefault()}
     */
    public static ZonedDateTime convertToZonedDateTime(Date date_) {
        return convertToZonedDateTime(date_, ZoneId.systemDefault());
    }
    
    public static ZonedDateTime convertToZonedDateTime(Date date_, ZoneId zoneId) {
        return ZonedDateTime.from(
                Instant.ofEpochMilli(date_.getTime())
                        .atZone(zoneId)
        );
    }
    
    public static Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime_) {
        final long epochMillis = zonedDateTime_.toInstant().toEpochMilli();
        return new java.sql.Date(epochMillis);
    }
}
