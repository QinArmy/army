package io.army.example.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

public abstract class CommonUtils {


    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.NORMAL)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter(Locale.ENGLISH);


    protected CommonUtils() {
        throw new UnsupportedOperationException();
    }


    public static LocalDate birthdayFrom(String certificateNo) {
        return LocalDate.parse(certificateNo.substring(6, 14), DATE_FORMATTER);
    }

}
