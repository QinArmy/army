package io.army.meta.mapping;

import io.army.domain.IDomain;
import io.army.util.Precision;

import java.sql.JDBCType;

public final class BooleanMapping extends AbstractMappingType {

    public static final BooleanMapping INSTANCE = new BooleanMapping();

    private static final Precision PRECISION = new Precision(1, 0);


    private BooleanMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.CHAR;
    }

    @Override
    public String nullSafeTextValue(Object value) {
        String text;
        if (Boolean.TRUE.equals(value)) {
            text = IDomain.Y;
        } else {
            text = IDomain.N;
        }
        return text;
    }

    @Override
    public boolean isTextValue(String textValue) {
        return IDomain.Y.equals(textValue)
                || IDomain.N.equals(textValue);
    }

    @Override
    public int precision() {
        return 1;
    }

    @Override
    public int scale() {
        return -1;
    }
}
