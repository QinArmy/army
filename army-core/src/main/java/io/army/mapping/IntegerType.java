package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;

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


    public static final IntegerType INSTANCE = new IntegerType();


    public static IntegerType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(IntegerType.class, fieldType);
        }
        return INSTANCE;
    }


    private IntegerType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.INT;
                break;
            case PostgreSQL:
                type = PostgreType.INTEGER;
                break;
            default:
                throw noMappingError(meta);

        }
        return type;
    }

    @Override
    public Integer convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToInt(this, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer beforeBind(SqlType type, final MappingEnv env, final Object nonNull) {
        return _convertToInt(this, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer afterGet(SqlType type, final MappingEnv env, Object nonNull) {
        return _convertToInt(this, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE, DATA_ACCESS_ERROR_HANDLER);
    }


    @Deprecated
    public static int _beforeBind(final SqlType sqlType, final Object nonNull, final int min, final int max) {
        final int value;
        if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            final int v = ((Number) nonNull).intValue();
            if (v < min || v > max) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = (byte) v;
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v < min || v > max) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = (byte) v;
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.compareTo(BigDecimal.valueOf(min)) < 0
                    || v.compareTo(BigDecimal.valueOf(max)) > 0) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = v.byteValue();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(min)) < 0
                    || v.compareTo(BigInteger.valueOf(max)) > 0) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = v.byteValue();
        } else if (nonNull instanceof String) {
            final int v;
            try {
                v = Integer.parseInt((String) nonNull);
                if (v < min || v > max) {
                    throw valueOutRange(sqlType, nonNull, null);
                }
            } catch (NumberFormatException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
            value = v;
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    public static int _convertToInt(final MappingType type, final Object nonNull, final int min, final int max,
                                    final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final int value;
        if (nonNull instanceof Integer) {
            value = (Integer) nonNull;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue();
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v < min || v > max) {
                throw errorHandler.apply(type, nonNull);
            }
            value = (byte) v;
        } else if (nonNull instanceof BigDecimal) {
            try {
                value = ((BigDecimal) nonNull).intValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof BigInteger) {
            try {
                value = ((BigInteger) nonNull).intValueExact();
            } catch (ArithmeticException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof String) {
            try {
                value = Integer.parseInt((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1 : 0;
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        if (value < min || value > max) {
            throw errorHandler.apply(type, nonNull);
        }
        return value;

    }


}
