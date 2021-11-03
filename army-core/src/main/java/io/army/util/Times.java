package io.army.util;

import io.qinarmy.util.TimeUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public abstract class Times extends TimeUtils {

    public static final String CHINA_ZONE = "+08:00";

    public static final ZoneId ZONE8 = ZoneId.of(CHINA_ZONE);

    public static final OffsetDateTime ZERO_DATE_TIME = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

    public static final ZoneId GMT = ZoneId.of("GMT");

    public static final TimeZone TIME_ZONE8 = TimeZone.getTimeZone(ZONE8);

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String ZONE_TIME_FORMAT = "HH:mm:ssZ";

    public static final String SIX_FRACTION_TIME_FORMAT = "HH:mm:ss.SSSSSS";

    public static final String SIX_FRACTION_ZONED_TIME_FORMAT = "HH:mm:ss.SSSSSSZ";

    public static final String DATE_FORMAT = "uuuu-MM-dd";

    public static final String DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss";

    public static final String YEAR_FORMAT = "uuuu";

    public static final String MONTH_DAY_FORMAT = "MM-dd";

    public static final String YEAR_MONTH_FORMAT = "uuuu-MM";

    public static final String CLOSE_DATE_FORMAT = "uuuuMMdd";

    public static final String CLOSE_DATE_TIME_FORMAT = "uuuuMMddHHmmss";

    public static final String FULL_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";

    public static final String SIX_FRACTION_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSSSS";

    public static final String ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ssZ";

    public static final String FULL_ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSZ";

    public static final String SIX_FRACTION_ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSSSSZ";

    public static final String LOCALE_ZONE_DATE_TIME_FORMAT = "E MMM dd HH:mm:ss Z uuuu";

    private static final ConcurrentMap<String, DateTimeFormatter> dateTimeFormatterHolder = new ConcurrentHashMap<>();


    public static DateTimeFormatter dateTimeFormatter(String format) {
        return dateTimeFormatterHolder.computeIfAbsent(format, DateTimeFormatter::ofPattern);
    }


    public static ChronoUnit convertTimeUnit(TimeUnit timeUnit) {
        ChronoUnit unit;
        switch (timeUnit) {
            case SECONDS:
                unit = ChronoUnit.SECONDS;
                break;
            case MINUTES:
                unit = ChronoUnit.MINUTES;
                break;
            case HOURS:
                unit = ChronoUnit.HOURS;
                break;
            case DAYS:
                unit = ChronoUnit.DAYS;
                break;
            case MICROSECONDS:
                unit = ChronoUnit.MICROS;
                break;
            case NANOSECONDS:
                unit = ChronoUnit.NANOS;
                break;
            case MILLISECONDS:
                unit = ChronoUnit.MILLIS;
                break;
            default:
                throw new IllegalArgumentException();

        }
        return unit;
    }


    public static boolean isZero(LocalDate date) {
        return ZERO_DATE_TIME.toLocalDate().isEqual(date);
    }

    public static boolean isZero(LocalDateTime dateTime) {
        return ZERO_DATE_TIME.toLocalDateTime().isEqual(dateTime);
    }

    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static LocalDateTime toDateTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static ZonedDateTime toZoneDateTime(long millis) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }


    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static LocalDate toDate(long millis) {
        return toDateTime(millis).toLocalDate();
    }

    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static LocalTime toTime(long millis) {
        return toDateTime(millis).toLocalTime();
    }

    public static LocalDate toMinDate(YearMonth yearMonth) {
        return LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
    }

    public static LocalDate toMaxDate(YearMonth yearMonth) {
        LocalDate minDate = toMinDate(yearMonth);
        return minDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDateTime toMinDateTime(YearMonth yearMonth) {
        return LocalDateTime.of(toMinDate(yearMonth), LocalTime.MIDNIGHT);
    }

    public static LocalDateTime toMaxDateTime(YearMonth yearMonth) {
        return LocalDateTime.of(toMaxDate(yearMonth), LocalTime.MAX);
    }

    public static LocalDate toMinDate(Year year) {
        return LocalDate.of(year.getValue(), Month.JANUARY, 1);
    }

    public static LocalDate toMaxDate(Year year) {
        return LocalDate.of(year.getValue(), Month.DECEMBER, 31);
    }

    public static LocalDateTime toMinWeekDateTime(final LocalDate date) {
        return LocalDateTime.of(date.minusDays(date.getDayOfWeek().ordinal()), LocalTime.MIDNIGHT);
    }


    public static LocalDateTime toMaxWeekDateTime(LocalDate date) {
        int dayCount = 6 - date.getDayOfWeek().ordinal();
        return LocalDateTime.of(date.plusDays(dayCount), LocalTime.MAX);
    }

    public static LocalDateTime toQuarterMinDateTime(YearMonth month) {
        YearMonth first = YearMonth.of(month.getYear(), month.getMonth().firstMonthOfQuarter());
        return toMinDateTime(first);
    }

    public static LocalDateTime toQuarterMaxDateTime(YearMonth month) {
        YearMonth last = YearMonth.of(month.getYear(), month.getMonth().firstMonthOfQuarter().plus(2));
        return toMaxDateTime(last);
    }

    public static LocalDateTime toMinDateTime(Year year) {
        return LocalDateTime.of(toMinDate(year), LocalTime.MIDNIGHT);
    }

    public static LocalDateTime toMaxDateTime(Year year) {
        return LocalDateTime.of(toMaxDate(year), LocalTime.MAX);
    }


    public static boolean isLastOfQuarter(Month month) {
        return month == Month.MAY
                || month == Month.JUNE
                || month == Month.SEPTEMBER
                || month == Month.DECEMBER;

    }
}
