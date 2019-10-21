package io.army.meta.mapping;

import io.army.util.Precision;
import org.springframework.lang.NonNull;

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
