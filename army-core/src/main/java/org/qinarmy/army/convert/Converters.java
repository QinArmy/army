package org.qinarmy.army.convert;

import org.qinarmy.army.util.TimeUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * created  on 2019-02-22.
 */
public abstract class Converters {

    public static final Converter<String, String> STRING_TO_STRING = source -> source;

    public static final Converter<String, Integer> STRING_TO_INTERGER = Integer::parseInt;

    public static final Converter<String, Long> STRING_TO_LONG = Long::parseLong;

    public static final Converter<String, Boolean> STRING_TO_BOOLEAN = Boolean::parseBoolean;

    public static final Converter<String, BigDecimal> STRING_TO_BIGDECIMAL = BigDecimal::new;

    public static final Converter<String, LocalTime> STRING_TO_TIME = source ->
            LocalTime.parse(source, TimeUtils.TIME_FORMATTER);

    public static final Converter<String, LocalDate> STRING_TO_DATE = source ->
            LocalDate.parse(source, TimeUtils.DATE_FORMATTER);

    public static final Converter<String, LocalDateTime> STRING_TO_DATE_TIME = source ->
            LocalDateTime.parse(source, TimeUtils.DATE_TIME_FORMATTER);

    private static final Map<Class<?>, Converter<String, ?>> CONVERTER_MAP = createConverterMap();


    private static Map<Class<?>, Converter<String, ?>> createConverterMap() {
        Map<Class<?>, Converter<String, ?>> map = new HashMap<>();

        map.put(String.class, STRING_TO_STRING);
        map.put(Integer.class, STRING_TO_INTERGER);
        map.put(Long.class, STRING_TO_LONG);
        map.put(Boolean.class, STRING_TO_BOOLEAN);

        map.put(BigDecimal.class, STRING_TO_BIGDECIMAL);
        map.put(LocalTime.class, STRING_TO_TIME);
        map.put(LocalDate.class, STRING_TO_DATE);
        map.put(LocalDateTime.class, STRING_TO_DATE_TIME);

        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> Converter<String, T> getConverter(@NonNull Class<T> type) {
        return (Converter<String, T>) CONVERTER_MAP.get(type);
    }

}
