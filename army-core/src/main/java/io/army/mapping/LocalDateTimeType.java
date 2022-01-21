package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.TimeUtils;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDateTime;

/**
 * @see LocalDateTime
 */
public final class LocalDateTimeType extends _ArmyNoInjectionMapping {


    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType create(Class<?> javaType) {
        if (javaType != LocalDateTime.class) {
            throw errorJavaType(LocalDateTimeType.class, javaType);
        }
        return INSTANCE;
    }


    private LocalDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.DATETIME;
                break;
            case PostgreSQL:
                sqlType = PostgreType.TIMESTAMP;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public LocalDateTime beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        final LocalDateTime value;
        if (nonNull instanceof LocalDateTime) {
            value = (LocalDateTime) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDateTime.parse((String) nonNull, TimeUtils.getDatetimeFormatter(6));
            } catch (DateTimeException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public LocalDateTime afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof LocalDateTime)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (LocalDateTime) nonNull;
    }


}
