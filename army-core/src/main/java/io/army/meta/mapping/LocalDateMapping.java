package io.army.meta.mapping;

import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;
import java.time.LocalDate;

public final class LocalDateMapping extends MappingSupport implements MappingType<LocalDate> {

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
    public Object toSql(LocalDate date) {
        return date == null ? null : java.sql.Date.valueOf(date);
    }

    @Override
    public LocalDate toJava(Object databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        LocalDate value;
        if (databaseValue instanceof java.sql.Date) {
            value = ((java.sql.Date) databaseValue).toLocalDate();
        } else {
            throw databaseValueError(databaseValue);
        }
        return value;
    }


    @NonNull
    @Override
    public Precision precision() {
        return Precision.EMPTY;
    }
}
