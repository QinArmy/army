package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

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

    public static final UnsignedBigIntegerType INSTANCE = new UnsignedBigIntegerType();

    public static UnsignedBigIntegerType from(final Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(UnsignedBigIntegerType.class, fieldType);
        }
        return INSTANCE;
    }


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
    public SqlType map(final ServerMeta meta) {
        return UnsignedBigDecimalType.mapToSqlType(this, meta);
    }

    @Override
    public BigInteger convert(MappingEnv env, Object nonNull) throws CriteriaException {
        final BigInteger value;
        value = BigIntegerType._convertToBigInteger(this, nonNull, PARAM_ERROR_HANDLER_0);
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }

    @Override
    public BigDecimal beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType._convertToBigDecimal(this, nonNull, PARAM_ERROR_HANDLER_0)
                .stripTrailingZeros();
        if (value.scale() != 0 || value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(SqlType type, MappingEnv env, Object nonNull) {
        final BigInteger value;
        value = BigIntegerType._convertToBigInteger(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }



}
