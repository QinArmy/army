package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link BigDecimal}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link Double}</li>
 *     <li>{@link Float}</li>
 *     <li>{@link Boolean} true : {@link BigDecimal#ONE} , false: {@link BigDecimal#ZERO}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link BigDecimal},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class BigDecimalType extends _NumericType implements MappingType.SqlDecimalType {

    public static final BigDecimalType INSTANCE = new BigDecimalType();

    public static BigDecimalType from(Class<?> fieldType) {
        if (fieldType != BigDecimal.class) {
            throw errorJavaType(BigDecimalType.class, fieldType);
        }
        return INSTANCE;
    }


    private BigDecimalType() {
    }


    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }


    @Override
    public SqlType map(final ServerMeta meta) {
        return mapToDecimal(this, meta);
    }


    @Override
    public BigDecimal convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToBigDecimal(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public BigDecimal beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return _convertToBigDecimal(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public BigDecimal afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        return _convertToBigDecimal(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    @Deprecated
    public static BigDecimal convertToBigDecimal(final SqlType type, final Object nonNull) {
        final BigDecimal value;
        if (nonNull instanceof BigDecimal) {
            value = (BigDecimal) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Long
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigDecimal.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof BigInteger) {
            value = new BigDecimal((BigInteger) nonNull);
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? BigDecimal.ONE : BigDecimal.ZERO;
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            value = new BigDecimal(nonNull.toString());
        } else if (nonNull instanceof String) {
            try {
                value = new BigDecimal((String) nonNull);
            } catch (NumberFormatException e) {
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    static BigDecimal _convertToBigDecimal(final MappingType type, final Object nonNull,
                                           final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final BigDecimal value;
        if (nonNull instanceof BigDecimal) {
            value = (BigDecimal) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Long
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigDecimal.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof BigInteger) {
            value = new BigDecimal((BigInteger) nonNull);
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? BigDecimal.ONE : BigDecimal.ZERO;
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            value = new BigDecimal(nonNull.toString()); // must use double string.
        } else if (nonNull instanceof String) {
            try {
                value = new BigDecimal((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }

    static SqlType mapToDecimal(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.dialectDatabase()) {
            case MySQL:
                sqlType = MySQLType.DECIMAL;
                break;
            case PostgreSQL:
                sqlType = PostgreDataType.DECIMAL;
                break;
            case H2:
                sqlType = H2DataType.DECIMAL;
                break;
            case Oracle:
                sqlType = OracleDataType.NUMBER;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return sqlType;
    }


}
