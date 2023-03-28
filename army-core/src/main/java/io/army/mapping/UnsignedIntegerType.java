package io.army.mapping;

import io.army.criteria.CriteriaException;
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
    public Long convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return LongType._convertToLong(this, nonNull, 0L, 0xFFFF_FFFFL, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return LongType._convertToLong(this, nonNull, 0L, 0xFFFF_FFFFL, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return LongType._convertToLong(this, nonNull, 0L, 0xFFFF_FFFFL, DATA_ACCESS_ERROR_HANDLER);
    }


}
