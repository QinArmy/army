package io.army.mapping.optional;

import io.army.mapping.MappingEnvironment;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.TimeUtils;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * @see OffsetDateTime
 */
public final class OffsetDateTimeType extends _ArmyNoInjectionMapping {

    public static final OffsetDateTimeType INSTANCE = new OffsetDateTimeType();

    public static OffsetDateTimeType create(Class<?> javaType) {
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
                sqlType = PostgreType.TIMESTAMPTZ;
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
    public OffsetDateTime beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        final OffsetDateTime value;
        if (nonNull instanceof OffsetDateTime) {
            value = (OffsetDateTime) nonNull;
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime();
        } else if (nonNull instanceof String) {
            try {
                value = OffsetDateTime.parse((String) nonNull, TimeUtils.getDatetimeOffsetFormatter(6));
            } catch (DateTimeException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public OffsetDateTime afterGet(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        if (!(nonNull instanceof OffsetDateTime)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (OffsetDateTime) nonNull;
    }


}
