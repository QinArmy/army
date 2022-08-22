package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * @see Long
 */
public final class UnsignedIntegerType extends _NumericType._UnsignedIntegerType {

    public static final UnsignedIntegerType INSTANCE = new UnsignedIntegerType();

    public static UnsignedIntegerType from(final Class<?> fieldType) {
        if (fieldType != Long.class) {
            throw errorJavaType(UnsignedIntegerType.class, fieldType);
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
                sqlType = MySQLTypes.INT_UNSIGNED;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BIGINT;
                break;

            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Long beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        return LongType.beforeBind(sqlType, nonNull, 0L, 0xFFFF_FFFFL);
    }

    @Override
    public Long afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
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

            case Oracle:
            case H2:
            default:
                throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }


}
