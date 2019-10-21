package io.army.meta.mapping;

import io.army.util.Precision;
import org.springframework.lang.NonNull;

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

    @NonNull
    @Override
    public Precision precision() {
        return Precision.EMPTY;
    }
}
