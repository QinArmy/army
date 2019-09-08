package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.meta.sqltype.mysql.MySQLDateTime;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public final class LocalDateTimeMapping extends MappingSupport implements MappingType<LocalDateTime> {

    public static final LocalDateTimeMapping INSTANCE = new LocalDateTimeMapping();

    private LocalDateTimeMapping() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP;
    }

    @Override
    public Object toSql(LocalDateTime dateTime) {
        return Timestamp.valueOf(dateTime);
    }

    @Override
    public LocalDateTime toJava(Object databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        LocalDateTime value;
        if (databaseValue instanceof Timestamp) {
            value = ((Timestamp) databaseValue).toLocalDateTime();
        } else {
            throw databaseValueError(databaseValue);
        }
        return value;
    }

    @Override
    public SQLDataType sqlType(Dialect dialect) {
        SQLDataType sqlDataType;
        if (dialect instanceof MySQLDialect) {
            sqlDataType = MySQLDateTime.INSTANCE;
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
