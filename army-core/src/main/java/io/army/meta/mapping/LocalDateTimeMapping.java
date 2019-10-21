package io.army.meta.mapping;

import java.sql.JDBCType;
import java.time.LocalDateTime;

public final class LocalDateTimeMapping extends MappingSupport implements MappingType {

    public static final LocalDateTimeMapping INSTANCE = new LocalDateTimeMapping();

    private LocalDateTimeMapping() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP;
    }

    @Override
    public String nullSafeTextValue(Object value) {
        return null;
    }

    @Override
    public boolean isTextValue(String textValue) {
        return false;
    }


    @Override
    public int precision() {
        return 6;
    }

    @Override
    public int scale() {
        return -1;
    }
}
