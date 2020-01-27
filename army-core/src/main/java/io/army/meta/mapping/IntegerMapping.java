package io.army.meta.mapping;

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
        return String.valueOf(value);
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            Integer.parseInt(textValue);
            yes = true;
        } catch (NumberFormatException e) {
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
