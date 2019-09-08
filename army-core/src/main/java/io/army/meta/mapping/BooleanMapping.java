package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.meta.sqltype.Char;
import io.army.meta.sqltype.SQLDataType;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;

public final class BooleanMapping extends MappingSupport implements MappingType<Boolean> {

    public static final BooleanMapping INSTANCE = new BooleanMapping();

    private static final Precision PRECISION = new Precision(1, 0);

    private BooleanMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.CHAR;
    }

    @Override
    public Object toSql(Boolean boolValue) {
        return boolValue;
    }

    @Override
    public Boolean toJava(Object databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        Boolean value;
        if (databaseValue instanceof Character) {
            char charValue = (Character) databaseValue;
            if (charValue == 'Y') {
                value = Boolean.TRUE;
            } else if (charValue == 'N') {
                value = Boolean.FALSE;
            } else {
                throw new IllegalArgumentException(String.format("databaseValue[%s] error", databaseValue));
            }
        } else if (databaseValue instanceof String) {
            String textValue = (String) databaseValue;
            if ("Y".equals(textValue)) {
                value = Boolean.TRUE;
            } else if ("N".equals(textValue)) {
                value = Boolean.FALSE;
            } else {
                throw new IllegalArgumentException(String.format("databaseValue[%s] error", databaseValue));
            }
        } else {
            throw new IllegalArgumentException(String.format("databaseValue[%s] error", databaseValue));
        }
        return value;
    }

    @Override
    public SQLDataType sqlType(Dialect dialect) {
        return Char.INSTANCE;
    }

    @NonNull
    @Override
    public Precision precision() {
        return PRECISION;
    }
}
