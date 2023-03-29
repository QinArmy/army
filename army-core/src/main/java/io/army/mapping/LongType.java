package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link Long}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link java.math.BigDecimal},it has a zero fractional part</li>
 *     <li>{@link Boolean} true : 1 , false: 0</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link Long},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class LongType extends _NumericType._IntegerType {

    public static final LongType INSTANCE = new LongType();


    public static LongType from(final Class<?> fieldType) {
        if (fieldType != Long.class) {
            throw errorJavaType(LongType.class, fieldType);
        }
        return INSTANCE;
    }

    private LongType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.BIGINT;
                break;
            case PostgreSQL:
                type = PostgreTypes.BIGINT;
                break;
            default:
                throw noMappingError(meta);

        }
        return type;
    }


    @Override
    public Long convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToLong(this, nonNull, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToLong(this, nonNull, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToLong(this, nonNull, Long.MIN_VALUE, Long.MAX_VALUE, DATA_ACCESS_ERROR_HANDLER);
    }


    static long _convertToLong(final MappingType type, final Object nonNull, final long min, final long max,
                               final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).longValue();
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseLong((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof BigDecimal) {
            try {
                value = ((BigDecimal) nonNull).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof BigInteger) {
            try {
                value = ((BigInteger) nonNull).longValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1L : 0L;
        } else {
            throw errorHandler.apply(type, nonNull);
        }

        if (value < min || value > max) {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
