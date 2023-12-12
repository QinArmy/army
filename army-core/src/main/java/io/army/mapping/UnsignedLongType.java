package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigInteger;

/**
 * <p>
 * This class representing the mapping from {@link BigInteger} to (unsigned) bigint.
 * * @see BigInteger
 */
public final class UnsignedLongType extends _NumericType._UnsignedIntegerType {

    public static UnsignedLongType from(final Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(UnsignedLongType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BigInteger MAX_VALUE = new BigInteger(Long.toUnsignedString(-1L));

    public static final UnsignedLongType INSTANCE = new UnsignedLongType();

    /**
     * private constructor
     */
    private UnsignedLongType() {
    }

    @Override
    public Class<?> javaType() {
        return BigInteger.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.BIG_LONG;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.BIGINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreType.DECIMAL;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public BigInteger convert(MappingEnv env, Object source) throws CriteriaException {
        return UnsignedBigIntegerType.toUnsignedBigInteger(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Number beforeBind(final DataType dataType, MappingEnv env, final Object source) {
        final Number value;
        switch (((SqlType) dataType).database()) {
            case MySQL:
                value = UnsignedBigIntegerType.toUnsignedBigInteger(this, dataType, source, PARAM_ERROR_HANDLER);
                break;
            case PostgreSQL:
                value = UnsignedBigIntegerType.toUnsignedBigDecimal(this, dataType, source, PARAM_ERROR_HANDLER);
                break;
            default:
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(DataType dataType, MappingEnv env, final Object source) {
        return UnsignedBigIntegerType.toUnsignedBigInteger(this, dataType, source, ACCESS_ERROR_HANDLER);
    }



}
