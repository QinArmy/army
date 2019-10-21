package io.army.meta.mapping;

import io.army.util.TimeUtils;

import java.sql.JDBCType;
import java.time.LocalDate;

public final class LocalDateMapping extends MappingSupport implements MappingType {

    public static final LocalDateMapping INSTANCE = new LocalDateMapping();


    private LocalDateMapping() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public String nullSafeTextValue(Object value) {
        LocalDate date = (LocalDate) value;
        return date.format(TimeUtils.DATE_FORMATTER);
    }


    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            LocalDate.parse(textValue, TimeUtils.DATE_FORMATTER);
            yes = true;
        } catch (Exception e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public int precision() {
        return -1;
    }

    @Override
    public int scale() {
        return -1;
    }
}
