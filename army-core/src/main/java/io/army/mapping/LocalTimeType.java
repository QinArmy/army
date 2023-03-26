package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.LocalTime;

/**
 * @see LocalTime
 */
public final class LocalTimeType extends _ArmyNoInjectionMapping {


    public static final LocalTimeType INSTANCE = new LocalTimeType();

    public static LocalTimeType from(final Class<?> fieldType) {
        if (fieldType != LocalTime.class) {
            throw errorJavaType(LocalTimeType.class, fieldType);
        }
        return INSTANCE;
    }


    private LocalTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalTime.class;
    }


    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.TIME;
                break;
            case PostgreSQL:
                sqlType = PostgreType.TIME;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public LocalTime beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final LocalTime value;
        if (nonNull instanceof LocalTime) {
            value = (LocalTime) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalTime.parse((String) nonNull, _TimeUtils.getTimeFormatter(6));
            } catch (DateTimeException e) {
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public LocalTime afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof LocalTime)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (LocalTime) nonNull;
    }


}
