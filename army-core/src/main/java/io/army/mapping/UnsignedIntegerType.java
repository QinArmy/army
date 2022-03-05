package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;

/**
 * @see Long
 */
public final class UnsignedIntegerType extends _ArmyNoInjectionMapping {

    public static final UnsignedIntegerType INSTANCE = new UnsignedIntegerType();

    public static UnsignedIntegerType create(Class<?> javaType) {
        if (javaType != BigDecimal.class) {
            throw errorJavaType(UnsignedIntegerType.class, javaType);
        }
        return INSTANCE;
    }

    private UnsignedIntegerType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }


    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.INT_UNSIGNED;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BIGINT;
                break;
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Long beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return LongType.beforeBind(sqlType, nonNull, 0L, 0xFFFF_FFFFL);
    }

    @Override
    public Long afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        final long value;
        switch (sqlType.database()) {
            case MySQL:
            case PostgreSQL: {
                if (!(nonNull instanceof Long)) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
                value = (Long) nonNull;
                if (value < 0L || value > 0xFFFF_FFFFL) {
                    throw errorValueForSqlType(sqlType, nonNull, null);
                }
            }
            break;
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }


}
