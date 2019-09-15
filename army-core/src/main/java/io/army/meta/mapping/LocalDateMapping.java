package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.meta.sqltype.mysql.MySQLDate;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;
import java.time.LocalDate;

public final class LocalDateMapping extends MappingSupport implements MappingType<LocalDate> {

    public static final LocalDateMapping INSTANCE = new LocalDateMapping();

    private LocalDateMapping() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public Object toSql(LocalDate date) {
        return date == null ? null : java.sql.Date.valueOf(date);
    }

    @Override
    public LocalDate toJava(Object databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        LocalDate value;
        if (databaseValue instanceof java.sql.Date) {
            value = ((java.sql.Date) databaseValue).toLocalDate();
        } else {
            throw databaseValueError(databaseValue);
        }
        return value;
    }

    @Override
    public SQLDataType sqlType(Dialect dialect) {
        SQLDataType sqlDataType;
        if (dialect instanceof MySQLDialect) {
            sqlDataType = MySQLDate.INSTANCE;
        } else {
            throw unsupportedDialect(dialect);
        }
        return sqlDataType;
    }

    @NonNull
    @Override
    public Precision precision() {
        return Precision.EMPTY;
    }
}
