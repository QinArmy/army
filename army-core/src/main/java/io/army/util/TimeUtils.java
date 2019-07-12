package io.army.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    public static final String MONTH_DAY_FORMAT = "MM-dd";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String CLOSE_DATE_FORMAT = "uuuuMMdd";

    public static final String CLOSE_DATE_TIME_FORMAT = "uuuuMMddHHmmss";

    public static final String FULL_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";

    public static final String ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ssZ";

    public static final String FULL_ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSZ";

    public static final String LOCALE_ZONE_DATE_TIME_FORMAT = "E MMM dd HH:mm:ss Z uuuu";


    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern(MONTH_DAY_FORMAT);

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static final DateTimeFormatter CLOSE_DATE_FORMATTER = DateTimeFormatter.ofPattern(CLOSE_DATE_FORMAT);

    public static final DateTimeFormatter CLOSE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(CLOSE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter FULL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT);

    public static final DateTimeFormatter ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ZONE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter FULL_ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FULL_ZONE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter ENGLISH_ZONE_FORMATTER =
            DateTimeFormatter.ofPattern(LOCALE_ZONE_DATE_TIME_FORMAT, Locale.ENGLISH);

    public static final LocalDateTime SOURCE_DATE_TIME = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), GMT);

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


}
