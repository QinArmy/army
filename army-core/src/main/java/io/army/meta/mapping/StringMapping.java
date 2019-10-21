package io.army.meta.mapping;

import io.army.util.Precision;
import org.springframework.lang.NonNull;

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

    @NonNull
    @Override
    public Precision precision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }
}
