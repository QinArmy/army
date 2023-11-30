package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * This class representing the mapping from {@link BigInteger} to (unsigned) decimal.
 * </p>
 *
 * @see BigInteger
 */
public final class UnsignedBigIntegerType extends _NumericType._UnsignedIntegerType {


    public static UnsignedBigIntegerType from(final Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(UnsignedBigIntegerType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final UnsignedBigIntegerType INSTANCE = new UnsignedBigIntegerType();

    /**
     * private constructor
     */
    private UnsignedBigIntegerType() {
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
        return UnsignedBigDecimalType.mapToSqlType(this, meta);
    }


    @Override
    public BigInteger convert(MappingEnv env, Object nonNull) throws CriteriaException {
        final BigInteger value;
        value = BigIntegerType.toBigInteger(this, map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER);
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull, null);
        }
        return value;
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, dataType, nonNull, PARAM_ERROR_HANDLER)
                .stripTrailingZeros();
        if (value.scale() != 0 || value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(DataType dataType, MappingEnv env, Object nonNull) {
        final BigInteger value;
        value = BigIntegerType.toBigInteger(this, dataType, nonNull, ACCESS_ERROR_HANDLER);
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        return value;
    }


}
