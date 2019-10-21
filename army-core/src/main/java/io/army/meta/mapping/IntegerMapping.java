package io.army.meta.mapping;

import io.army.util.Precision;

import java.sql.JDBCType;

public final class IntegerMapping extends MappingSupport implements MappingType {

    public static final IntegerMapping INSTANCE = new IntegerMapping();

    private IntegerMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
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
    public Precision precision() {
        return Precision.DEFAULT_INT_PRECISION;
    }
}
