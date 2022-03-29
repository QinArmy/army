package io.army.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


/**
 *
 */
public abstract class _TimeUtils extends io.qinarmy.util.TimeUtils {

    protected _TimeUtils() {
        throw new UnsupportedOperationException();
    }

    public static final String CHINA_ZONE = "+08:00";

    public static final ZoneId ZONE8 = ZoneId.of(CHINA_ZONE);

    public static final OffsetDateTime ZERO_DATE_TIME = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

    public static final TimeZone TIME_ZONE8 = TimeZone.getTimeZone(ZONE8);

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String DATE_FORMAT = "uuuu-MM-dd";

    public static final String DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss";

    public static final String CLOSE_DATE_FORMAT = "uuuuMMdd";


    @Deprecated
    public static DateTimeFormatter dateTimeFormatter(String format) {
        throw new UnsupportedOperationException();
    }


}
