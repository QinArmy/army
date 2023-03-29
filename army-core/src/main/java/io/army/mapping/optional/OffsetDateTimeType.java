package io.army.mapping.optional;

import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * @see OffsetDateTime
 */
public final class OffsetDateTimeType extends _ArmyNoInjectionMapping {

    public static final OffsetDateTimeType INSTANCE = new OffsetDateTimeType();

    public static OffsetDateTimeType from(Class<?> javaType) {
        if (javaType != OffsetDateTime.class) {
            throw errorJavaType(OffsetDateTimeType.class, javaType);
        }
        return INSTANCE;
    }


    private OffsetDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetDateTime.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case PostgreSQL:
                sqlType = PostgreTypes.TIMESTAMPTZ;
                break;
            case Oracle:
                sqlType = OracleDataType.TIMESTAMPTZ;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public OffsetDateTime beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final OffsetDateTime value;
        if (nonNull instanceof OffsetDateTime) {
            value = (OffsetDateTime) nonNull;
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime();
        } else if (nonNull instanceof String) {
            try {
                value = OffsetDateTime.parse((String) nonNull, _TimeUtils.getDatetimeOffsetFormatter(6));
            } catch (DateTimeException e) {
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public OffsetDateTime afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof OffsetDateTime)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (OffsetDateTime) nonNull;
    }


}
