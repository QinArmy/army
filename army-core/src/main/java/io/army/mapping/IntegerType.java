package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.IntegerArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * This class is mapping class of {@link Integer}.
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
 *  to {@link Integer},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class IntegerType extends _NumericType._IntegerType {


    public static IntegerType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(IntegerType.class, fieldType);
        }
        return INTEGER;
    }

    public static final IntegerType INTEGER = new IntegerType();

    /**
     * private constructor
     */
    private IntegerType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        return IntegerArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToInteger(this, meta);
    }


    @Override
    public Integer convert(MappingEnv env, Object source) throws CriteriaException {
        return toInt(this, map(env.serverMeta()), source, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        return toInt(this, dataType, source, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer afterGet(DataType dataType, final MappingEnv env, Object source) {
        return toInt(this, dataType, source, Integer.MIN_VALUE, Integer.MAX_VALUE, ACCESS_ERROR_HANDLER);
    }


    public static int toInt(final MappingType type, DataType dataType, final Object nonNull,
                            final int min, final int max,
                            final ErrorHandler errorHandler) {
        final int value;
        if (nonNull instanceof Integer) {
            value = (Integer) nonNull;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue();
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v < min || v > max) {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
            value = (byte) v;
        } else if (nonNull instanceof BigDecimal) {
            try {
                value = ((BigDecimal) nonNull).intValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof BigInteger) {
            try {
                value = ((BigInteger) nonNull).intValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof String) {
            try {
                value = Integer.parseInt((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            try {
                value = new BigDecimal(nonNull.toString()).intValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1 : 0;
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        if (value < min || value > max) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;

    }

    public static SqlType mapToInteger(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.INT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.INTEGER;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return sqlType;
    }


}
