package io.army.meta.mapping;

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
        boolean yes;
        try {
            Long.parseLong(textValue);
            yes = true;
        } catch (NumberFormatException e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public int precision() {
        return 20;
    }

    @Override
    public int scale() {
        return -1;
    }
}
