package io.army.util;

import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;


/**
 *
 */
public abstract class _TimeUtils extends io.qinarmy.util.TimeUtils {

    private _TimeUtils() {
        throw new UnsupportedOperationException();
    }


    private static final String PATTERN = "+HH:MM:ss";

    private static final String NO_OFFSET_TEXT = "+00:00";


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
            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 0, true)
            .optionalEnd()
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
        return ZoneId.systemDefault().getRules().getOffset(Instant.now());
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


}
