package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.BigIntegerArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * This class is mapping class of {@link BigInteger}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link java.math.BigDecimal},it has a zero fractional part</li>
 *     <li>{@link Boolean} true : {@link BigInteger#ONE} , false: {@link BigInteger#ZERO}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link BigInteger},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class BigIntegerType extends _NumericType._IntegerType {


    public static BigIntegerType from(final Class<?> javaType) {
        if (javaType != BigInteger.class) {
            throw errorJavaType(BigIntegerType.class, javaType);
        }
        return INSTANCE;
    }

    public static final BigIntegerType INSTANCE = new BigIntegerType();

    /**
     * private constructor
     */
    private BigIntegerType() {
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
    public DataType map(ServerMeta meta) {
        return BigDecimalType.mapToSqlType(this, meta);
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BigIntegerArrayType.LINEAR;
    }

    @Override
    public BigInteger convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return toBigInteger(this, map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, final Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, dataType, nonNull, PARAM_ERROR_HANDLER)
                .stripTrailingZeros();
        if (value.scale() != 0) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(final DataType dataType, MappingEnv env, final Object nonNull) {
        return toBigInteger(this, dataType, nonNull, ACCESS_ERROR_HANDLER);
    }


    public static BigInteger toBigInteger(final MappingType type, final DataType dataType, final Object nonNull,
                                          final ErrorHandler errorHandler) {
        final BigInteger value;
        if (nonNull instanceof BigInteger) {
            value = (BigInteger) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Long
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigInteger.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? BigInteger.ONE : BigInteger.ZERO;
        } else if (nonNull instanceof BigDecimal) {
            try {
                value = ((BigDecimal) nonNull).toBigIntegerExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
        } else if (nonNull instanceof String) {
            try {
                value = new BigInteger((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
