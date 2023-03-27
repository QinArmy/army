package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;

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

    public static final BigIntegerType INSTANCE = new BigIntegerType();

    public static BigIntegerType from(Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(BigIntegerType.class, fieldType);
        }
        return INSTANCE;
    }


    private BigIntegerType() {
    }


    @Override
    public Class<?> javaType() {
        return BigInteger.class;
    }


    @Override
    public SqlType map(ServerMeta meta) {
        return BigDecimalType.INSTANCE.map(meta);
    }


    @Override
    public BigInteger convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToBigInteger(this, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType._convertToBigDecimal(this, nonNull, PARAM_ERROR_HANDLER)
                .stripTrailingZeros();
        if (value.scale() != 0) {
            throw PARAM_ERROR_HANDLER.apply(this, nonNull);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(final SqlType type, MappingEnv env, final Object nonNull) {
        return _convertToBigInteger(this, nonNull, DATA_ACCESS_ERROR_HANDLER);
    }

    public static BigInteger _convertToBigInteger(final MappingType type, final Object nonNull,
                                                  final BiFunction<MappingType, Object, ArmyException> errorHandler) {
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
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof String) {
            try {
                value = new BigInteger((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
