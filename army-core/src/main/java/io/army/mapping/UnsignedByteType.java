package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class representing the mapping from {@link Short} to (unsigned TINY)  INT.
*
 * @see Short
 */
public final class UnsignedByteType extends _NumericType._UnsignedIntegerType {

    public static UnsignedByteType from(final Class<?> fieldType) {
        if (fieldType != Short.class) {
            throw errorJavaType(UnsignedByteType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final UnsignedByteType INSTANCE = new UnsignedByteType();

    /**
     * private constructor
     */
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
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.TINYINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreType.SMALLINT;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }



    @Override
    public Short convert(MappingEnv env, Object source) throws CriteriaException {
        return (short) IntegerType.toInt(this, map(env.serverMeta()), source, 0, 0xFF, PARAM_ERROR_HANDLER);
    }

    @Override
    public Short beforeBind(DataType dataType, MappingEnv env, Object source) {
        return (short) IntegerType.toInt(this, dataType, source, 0, 0xFF, PARAM_ERROR_HANDLER);
    }

    @Override
    public Short afterGet(DataType dataType, MappingEnv env, Object source) {
        return (short) IntegerType.toInt(this, dataType, source, 0, 0xFF, ACCESS_ERROR_HANDLER);
    }


}
