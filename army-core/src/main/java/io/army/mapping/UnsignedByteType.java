package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class representing the mapping from {@link Short} to (unsigned TINY)  INT.
 * </p>
 *
 * @see Short
 */
public final class UnsignedByteType extends _NumericType._UnsignedIntegerType {

    public static final UnsignedByteType INSTANCE = new UnsignedByteType();

    public static UnsignedByteType from(final Class<?> fieldType) {
        if (fieldType != Short.class) {
            throw errorJavaType(UnsignedByteType.class, fieldType);
        }
        return INSTANCE;
    }


    private UnsignedByteType() {
    }


    @Override
    public Class<?> javaType() {
        return Short.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.SMALL;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.TINYINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreDataType.SMALLINT;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public Short convert(MappingEnv env, Object nonNull) throws CriteriaException {
        final int value;
        value = IntegerType._convertToInt(this, nonNull, 0, 0xFF, PARAM_ERROR_HANDLER_0);
        return (short) value;
    }

    @Override
    public Short beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        final int value;
        value = IntegerType._convertToInt(this, nonNull, 0, 0xFF, PARAM_ERROR_HANDLER_0);
        return (short) value;
    }

    @Override
    public Short afterGet(SqlType type, MappingEnv env, Object nonNull) {
        final int value;
        value = IntegerType._convertToInt(this, nonNull, 0, 0xFF, DATA_ACCESS_ERROR_HANDLER_0);
        return (short) value;
    }


}
