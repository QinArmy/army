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
        return toUnsignedBigInteger(this, map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, Object nonNull) {
        return toUnsignedBigDecimal(this, dataType, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigInteger afterGet(DataType dataType, MappingEnv env, Object nonNull) {
        return toUnsignedBigInteger(this, dataType, nonNull, ACCESS_ERROR_HANDLER);
    }


    public static BigInteger toUnsignedBigInteger(MappingType type, DataType dataType, final Object nonNull,
                                                  ErrorHandler errorHandler) {
        final BigInteger value;
        value = BigIntegerType.toBigInteger(type, dataType, nonNull, errorHandler);
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }

    public static BigDecimal toUnsignedBigDecimal(MappingType type, DataType dataType, final Object nonNull,
                                                  ErrorHandler errorHandler) {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(type, dataType, nonNull, PARAM_ERROR_HANDLER)
                .stripTrailingZeros();
        if (value.scale() != 0 || value.compareTo(BigDecimal.ZERO) < 0) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
