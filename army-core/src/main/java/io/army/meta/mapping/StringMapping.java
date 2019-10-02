package io.army.meta.mapping;

import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;
import java.sql.SQLException;

public final class StringMapping extends AbstractMappingType<String> {

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
    protected Object nonNullToSql(String text) {
        return text;
    }


    @Override
    protected String nonNullToJava(Object databaseValue) throws SQLException {
        String value;
        if (databaseValue instanceof String) {
            value = (String) databaseValue;
        } else if (databaseValue instanceof Character) {
            value = ((Character) databaseValue).toString();
        } else {
            throw new IllegalArgumentException(String.format(
                    "object[%s] couldn't convert to %s", databaseValue, getClass()));
        }
        return value;
    }


    @NonNull
    @Override
    public Precision precision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }
}
