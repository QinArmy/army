package io.army.meta.mapping;

import java.sql.JDBCType;

public final class StringMapping extends AbstractMappingType {

    public static final StringMapping INSTANCE = new StringMapping();


    private StringMapping() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.VARCHAR;
    }

    @Override
    public String nullSafeTextValue(Object value) {
        return String.valueOf(value);
    }

    @Override
    public boolean isTextValue(String textValue) {
        return true;
    }

    @Override
    public int precision() {
        return 255;
    }

    @Override
    public int scale() {
        return -1;
    }
}
