package io.army.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * created  on 2018/4/18.
 */
public abstract class TimeUtils {

    public static final String CHINA_ZONE = "+08:00";

    public static final ZoneId ZONE8 = ZoneId.of(CHINA_ZONE);

    public static final ZoneId GMT = ZoneId.of("GMT");

    public static final TimeZone TIME_ZONE8 = TimeZone.getTimeZone(ZONE8);

    public static final ZoneOffset ZONE_OFFSET8 = ZoneOffset.of(CHINA_ZONE);


    public static final String DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "uuuu-MM-dd";

    public static final String YEAR_FORMAT = "uuuu";

    public static final String MONTH_DAY_FORMAT = "MM-dd";

    public static final String YEAR_MONTH_FORMAT = "uuuu-MM";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String CLOSE_DATE_FORMAT = "uuuuMMdd";

    public static final String CLOSE_DATE_TIME_FORMAT = "uuuuMMddHHmmss";

    public static final String FULL_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";

    public static final String SIX_FRACTION_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSSSS";

    public static final String ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ssZ";

    public static final String FULL_ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSZ";

    public static final String LOCALE_ZONE_DATE_TIME_FORMAT = "E MMM dd HH:mm:ss Z uuuu";


    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern(YEAR_FORMAT);

    public static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern(MONTH_DAY_FORMAT);

    public static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern(YEAR_MONTH_FORMAT);

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static final DateTimeFormatter CLOSE_DATE_FORMATTER = DateTimeFormatter.ofPattern(CLOSE_DATE_FORMAT);

    public static final DateTimeFormatter CLOSE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(CLOSE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter FULL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT);

    public static final DateTimeFormatter ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ZONE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter FULL_ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FULL_ZONE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter SIX_FRACTION_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(SIX_FRACTION_DATE_TIME_FORMAT);

    public static final DateTimeFormatter ENGLISH_ZONE_FORMATTER =
            DateTimeFormatter.ofPattern(LOCALE_ZONE_DATE_TIME_FORMAT, Locale.ENGLISH);


    /**
     *
     * @see System#currentTimeMillis()
     */
    public static final LocalDateTime SOURCE_DATE_TIME = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());


    public static final LocalDate SOURCE_DATE = SOURCE_DATE_TIME.toLocalDate();


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


    /**
     * @return true 表示 date 等于 {@link #SOURCE_DATE}
     */
    public static boolean isSource(LocalDate date) {
        return SOURCE_DATE.isEqual(date);
    }

    /**
     * 比较 dateTime 是否等于 东八区 1970-01-01 08:00:00 ,这个过程将会忽略毫秒.
     * 如果 需要精确到毫秒请使用 {@link LocalDateTime#equals(Object)}
     *
     * @return true 表示 dateTime 是 东八区 1970-01-01 08:00:00
     */
    public static boolean isSource(LocalDateTime dateTime) {
        return SOURCE_DATE_TIME.isEqual(dateTime);
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
