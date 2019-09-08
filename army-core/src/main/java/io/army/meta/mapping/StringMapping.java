package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.meta.sqltype.Varchar;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;

public final class StringMapping extends MappingSupport implements MappingType<String> {

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
    public Object toSql(String textValue) {
        return textValue;
    }

    @Override
    public String toJava(Object databaseValue) {
        if (databaseValue == null) {
            return null;
        }
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

    @Override
    public SQLDataType sqlType(Dialect dialect) {

        SQLDataType sqlDataType;
        if (dialect instanceof MySQLDialect) {
            sqlDataType = Varchar.INSTANCE;
        } else {
            throw unsupportedDialect(dialect);
        }
        return sqlDataType;
    }

    @NonNull
    @Override
    public Precision precision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }
}
