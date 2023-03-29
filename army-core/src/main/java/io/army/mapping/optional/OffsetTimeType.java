package io.army.mapping.optional;

import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.OffsetTime;

/**
 * @see OffsetTime
 */
public final class OffsetTimeType extends _ArmyNoInjectionMapping {

    public static final OffsetTimeType INSTANCE = new OffsetTimeType();

    public static OffsetTimeType from(Class<?> javaType) {
        if (javaType != OffsetTime.class) {
            throw errorJavaType(OffsetTimeType.class, javaType);
        }
        return INSTANCE;
    }

    private OffsetTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetTime.class;
    }


    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case PostgreSQL:
                sqlType = PostgreTypes.TIMETZ;
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
    public OffsetTime beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        final OffsetTime value;
        if (nonNull instanceof OffsetTime) {
            value = (OffsetTime) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = OffsetTime.parse((String) nonNull, _TimeUtils.getOffsetTimeFormatter(6));
            } catch (DateTimeException e) {
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public OffsetTime afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof OffsetTime)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (OffsetTime) nonNull;
    }


}
