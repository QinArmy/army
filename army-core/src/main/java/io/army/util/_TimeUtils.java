package io.army.util;

import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;


/**
*/
public abstract class _TimeUtils {

    private _TimeUtils() {
        throw new UnsupportedOperationException();
    }


    private static final String PATTERN = "+HH:MM:ss";

    private static final String NO_OFFSET_TEXT = "+00:00";

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/time.html">The TIME Type</a>
     */
    private static final int MYSQL_DURATION_MAX_SECOND = 838 * 3600 + 59 * 60 + 59;


    public static final DateTimeFormatter TIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_TIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .append(TIME_FORMATTER_0)
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter TIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(TIME_FORMATTER_0)
            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_TIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(TIME_FORMATTER_6)
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter DATETIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(TIME_FORMATTER_0)
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(DATETIME_FORMATTER_0)
            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .append(DATETIME_FORMATTER_0)
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);


    public static final DateTimeFormatter DATETIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(DATETIME_FORMATTER_0)
            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);


    @Deprecated
    public static DateTimeFormatter dateTimeFormatter(String format) {
        throw new UnsupportedOperationException();
    }

    public static ZoneOffset systemZoneOffset() {
        return ZoneId.systemDefault().getRules().getOffset(Instant.EPOCH);
    }


    public static String format(final LocalTime time, final TypeMeta paramMeta) {
        final int scale;
        if (paramMeta instanceof FieldMeta) {
            scale = ((FieldMeta<?>) paramMeta).scale();
        } else {
            scale = 6;
        }
        final String text;
        switch (scale) {
            case 0:
                text = time.format(TIME_FORMATTER_0);
                break;
            case 1:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_1);
                break;
            case 2:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_2);
                break;
            case 3:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_3);
                break;
            case 4:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_4);
                break;
            case 5:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_5);
                break;
            default:
                text = time.format(TIME_FORMATTER_6);

        }
        return text;

    }


    public static String format(final LocalDateTime dateTime, final TypeMeta paramMeta) {
        final int scale;
        if (paramMeta instanceof FieldMeta) {
            scale = ((FieldMeta<?>) paramMeta).scale();
        } else {
            scale = 6;
        }
        final String text;
        switch (scale) {
            case 0:
                text = dateTime.format(DATETIME_FORMATTER_0);
                break;
            case 1:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_1);
                break;
            case 2:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_2);
                break;
            case 3:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_3);
                break;
            case 4:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_4);
                break;
            case 5:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_5);
                break;
            default:
                text = dateTime.format(DATETIME_FORMATTER_6);

        }
        return text;

    }

    public static String format(final OffsetTime time, final TypeMeta paramMeta) {
        final int scale;
        if (paramMeta instanceof FieldMeta) {
            scale = ((FieldMeta<?>) paramMeta).scale();
        } else {
            scale = 6;
        }
        final String text;
        switch (scale) {
            case 0:
                text = time.format(OFFSET_TIME_FORMATTER_0);
                break;
            case 1:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_1);
                break;
            case 2:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_2);
                break;
            case 3:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_3);
                break;
            case 4:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_4);
                break;
            case 5:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_5);
                break;
            default:
                text = time.format(OFFSET_TIME_FORMATTER_6);

        }
        return text;

    }


    public static String format(final OffsetDateTime dateTime, final TypeMeta paramMeta) {
        final int scale;
        if (paramMeta instanceof FieldMeta) {
            scale = ((FieldMeta<?>) paramMeta).scale();
        } else {
            scale = 6;
        }
        final String text;
        switch (scale) {
            case 0:
                text = dateTime.format(OFFSET_DATETIME_FORMATTER_0);
                break;
            case 1:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_1);
                break;
            case 2:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_2);
                break;
            case 3:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_3);
                break;
            case 4:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_4);
                break;
            case 5:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_5);
                break;
            default:
                text = dateTime.format(OFFSET_DATETIME_FORMATTER_6);

        }
        return text;

    }

    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T truncatedIfNeed(final int scale, final T temporal) {
        final TemporalUnit unit;
        switch (scale) {
            case 0: {
                if (temporal.get(MICRO_OF_SECOND) == 0) {
                    unit = null;
                } else {
                    unit = ChronoUnit.SECONDS;
                }
            }
            break;
            case 1: {
                if (temporal.get(MICRO_OF_SECOND) % 100_000 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MILLIS_100;
                }
            }
            break;
            case 2: {
                if (temporal.get(MICRO_OF_SECOND) % 10_000 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MILLIS_10;
                }
            }
            break;
            case 3: {
                if (temporal.get(MICRO_OF_SECOND) % 1000 == 0) {
                    unit = null;
                } else {
                    unit = ChronoUnit.MILLIS;
                }
            }
            break;
            case 4: {
                if (temporal.get(MICRO_OF_SECOND) % 100 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MICROS_100;
                }
            }
            break;
            case 5: {
                if (temporal.get(MICRO_OF_SECOND) % 10 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MICROS_10;
                }
            }
            break;
            case 6: {
                if (temporal.get(NANO_OF_SECOND) % 100 == 0) {
                    unit = null;
                } else {
                    unit = ChronoUnit.MICROS;
                }
            }
            break;
            default:
                unit = null;
        }

        final Temporal value;
        if (unit == null) {
            value = temporal;
        } else if (temporal instanceof LocalDateTime) {
            value = ((LocalDateTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof OffsetDateTime) {
            value = ((OffsetDateTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof ZonedDateTime) {
            value = ((ZonedDateTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof LocalTime) {
            value = ((LocalTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof OffsetTime) {
            value = ((OffsetTime) temporal).truncatedTo(unit);
        } else {
            // unknown
            value = temporal;
        }
        return (T) value;
    }


    /**
     * @return true: timeText representing {@link Duration}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/time.html">The TIME Type</a>
     */
    public static boolean isDuration(final String timeText) {
        int index = timeText.indexOf(':');
        final boolean duration;
        if (index < 0) {
            duration = false;
        } else {
            final int hours;
            hours = Integer.parseInt(timeText.substring(0, index));
            duration = hours < 0 || hours > 23;
        }
        return duration;
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/time.html">The TIME Type</a>
     * @see #convertToDuration(LocalTime)
     */
    public static Duration parseTimeAsDuration(final String timeText) throws DateTimeException {

        try {
            final String[] itemArray = timeText.trim().split(":");
            if (itemArray.length != 3) {
                throw new DateTimeException(mySqlTimeFormatErrorMessage(timeText));
            }
            final int hours, minutes, seconds, micros;
            hours = Integer.parseInt(itemArray[0]);
            minutes = Integer.parseInt(itemArray[1]);

            if (itemArray[2].contains(".")) {
                String[] secondPartArray = itemArray[2].split("\\.");
                if (secondPartArray.length != 2) {
                    throw new DateTimeException(mySqlTimeFormatErrorMessage(timeText));
                }
                seconds = Integer.parseInt(secondPartArray[0]);
                micros = Integer.parseInt(secondPartArray[1]);
            } else {
                seconds = Integer.parseInt(itemArray[2]);
                micros = 0;
            }
            if (hours < -838 || hours > 838
                    || minutes < 0 || minutes > 59
                    || seconds < 0 || seconds > 59
                    || micros < 0 || micros > 999_999) {
                throw new DateTimeException(mySqlTimeFormatErrorMessage(timeText));
            } else if (Math.abs(hours) == 838 && minutes == 59 && seconds == 59 && micros != 0) {
                throw new DateTimeException(mySqlTimeFormatErrorMessage(timeText));
            }
            final long totalSecond;
            if (hours < 0) {
                totalSecond = (hours * 3600L) - (minutes * 60L) - seconds;
            } else {
                totalSecond = (hours * 3600L) + (minutes * 60L) + seconds;
            }
            //nanoAdjustment must be positive ,java.time.Duration.ofSeconds(long, long) method invoke java.lang.Math.floorDiv(long, long) cause bug.
            return Duration.ofSeconds(totalSecond, micros * 1000L);
        } catch (Throwable e) {
            throw new DateTimeException(mySqlTimeFormatErrorMessage(timeText), e);
        }


    }


    public static boolean isOverflowDuration(final Duration duration) {
        final long abs = Math.abs(duration.getSeconds());
        return (abs > MYSQL_DURATION_MAX_SECOND) || (abs == MYSQL_DURATION_MAX_SECOND && duration.getNano() > 0L);
    }

    public static String durationToTimeText(final Duration duration) {
        if (isOverflowDuration(duration)) {
            throw new DateTimeException("duration too big,can't convert to MySQL TIME type.");
        }
        int restSecond = (int) Math.abs(duration.getSeconds());
        final int hours, minutes, seconds;
        hours = restSecond / 3600;
        restSecond %= 3600;
        minutes = restSecond / 60;
        seconds = restSecond % 60;

        StringBuilder builder = new StringBuilder(17);
        if (duration.isNegative()) {
            builder.append("-");
        }
        if (hours < 10) {
            builder.append("0");
        }
        builder.append(hours)
                .append(":");
        if (minutes < 10) {
            builder.append("0");
        }
        builder.append(minutes)
                .append(":");
        if (seconds < 10) {
            builder.append("0");
        }
        builder.append(seconds);

        final long micro = duration.getNano() / 1000L;
        if (micro > 999_999L) {
            throw new IllegalArgumentException(String.format("duration nano[%s] too big", duration.getNano()));
        }
        if (micro > 0L) {
            builder.append(".");
            String microText = Long.toString(micro);
            for (int i = 0, count = 6 - microText.length(); i < count; i++) {
                builder.append('0');
            }
            builder.append(microText);
        }
        return builder.toString();
    }

    /**
     * @param time {@link LocalTime} that underlying {@link java.time.ZoneOffset} match with database.
     * @see #parseTimeAsDuration(String)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/time.html">The TIME Type</a>
     */
    public static Duration convertToDuration(LocalTime time) throws IllegalArgumentException {
        final long totalSecond;
        totalSecond = time.getHour() * 3600L + time.getMinute() * 60L + time.getSecond();
        return Duration.ofSeconds(totalSecond, time.getNano());
    }


    /*-------------------below private methods -------------------*/

    private static String mySqlTimeFormatErrorMessage(final String timeText) {
        return String.format("MySQL TIME[%s] format error.", timeText);
    }


    private static abstract class DateTimeFormatterHolder {

        private DateTimeFormatterHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter DATETIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 1, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 2, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 4, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 5, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

    }//v


    private static abstract class OffsetDataTimeFormatterExtensionHolder {

        private OffsetDataTimeFormatterExtensionHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 1, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 2, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 4, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 5, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);


    }//OffsetDataTimeFormatterExtensionHolder


    private static abstract class TimeFormatterHolder {

        private TimeFormatterHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter TIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 1, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 2, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 4, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 5, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);


    }//TimeFormatterHolder


    private static abstract class OffsetTimeFormatterExtensionHolder {

        private OffsetTimeFormatterExtensionHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_1)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_2)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_3)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_4)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);


        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_5)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);


    }//OffsetTimeFormatterExtensionHolder


    /**
     * This TemporalUnit is designed for following :
     * <ul>
     *     <li>{@link LocalDateTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link OffsetDateTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link ZonedDateTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link LocalTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link OffsetTime#truncatedTo(TemporalUnit)}</li>
     * </ul>
     *
     * @since 1.0
     */
    private enum TruncatedUnit implements TemporalUnit {

        /**
         * @see ChronoUnit#MICROS
         */
        MICROS_10(Duration.ofNanos(10_000)),
        MICROS_100(Duration.ofNanos(100_000)),

        /**
         * @see ChronoUnit#MILLIS
         */
        MILLIS_10(Duration.ofMillis(10)),
        MILLIS_100(Duration.ofMillis(100));


        private final Duration duration;

        TruncatedUnit(Duration duration) {
            this.duration = duration;
        }

        @Override
        public final Duration getDuration() {
            return this.duration;
        }

        @Override
        public final boolean isDurationEstimated() {
            return false;
        }

        @Override
        public final boolean isDateBased() {
            return false;
        }

        @Override
        public final boolean isTimeBased() {
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <R extends Temporal> R addTo(R temporal, long amount) {
            return (R) temporal.plus(amount, this);
        }

        @Override
        public final long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            return temporal1Inclusive.until(temporal2Exclusive, this);
        }


    }


}
