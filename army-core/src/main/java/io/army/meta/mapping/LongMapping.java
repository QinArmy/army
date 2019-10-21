package io.army.meta.mapping;

import io.army.util.Precision;

import java.sql.JDBCType;

public final class LongMapping extends AbstractMappingType {

    public static final LongMapping INSTANCE = new LongMapping();

    private LongMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.BIGINT;
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
        return Precision.DEFAULT_BIGINT_PRECISION;
    }
}
