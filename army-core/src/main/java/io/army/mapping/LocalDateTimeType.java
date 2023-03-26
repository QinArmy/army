package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;

/**
 * @see LocalDateTime
 */
public final class LocalDateTimeType extends _ArmyNoInjectionMapping {


    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType from(final Class<?> fieldType) {
        if (fieldType != LocalDateTime.class) {
            throw errorJavaType(LocalDateTimeType.class, fieldType);
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
                sqlType = MySQLTypes.DATETIME;
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
    public LocalDateTime beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final LocalDateTime value;
        if (nonNull instanceof LocalDateTime) {
            value = (LocalDateTime) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDateTime.parse((String) nonNull, _TimeUtils.getDatetimeFormatter(6));
            } catch (DateTimeException e) {
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public LocalDateTime afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof LocalDateTime)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (LocalDateTime) nonNull;
    }


}
