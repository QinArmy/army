package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.BigDecimalArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

import java.math.BigDecimal;
import java.math.BigInteger;

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


    public static BigDecimalType from(Class<?> javaType) {
        if (javaType != BigDecimal.class) {
            throw errorJavaType(BigDecimalType.class, javaType);
        }
        return INSTANCE;
    }


    public static final BigDecimalType INSTANCE = new BigDecimalType();

    /**
     * private constructor
     */
    private BigDecimalType() {
    }


    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }


    @Override
    public DataType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BigDecimalArrayType.LINEAR;
    }

    @Override
    public BigDecimal convert(MappingEnv env, Object source) throws CriteriaException {
        return toBigDecimal(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toBigDecimal(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal afterGet(DataType dataType, MappingEnv env, final Object source) {
        return toBigDecimal(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    /*-------------------below static methods -------------------*/

    public static BigDecimal toBigDecimal(final MappingType type, final DataType dataType, final Object nonNull,
                                          final ErrorHandler errorHandler) {
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
            // TODO handle postgre money
            try {
                value = new BigDecimal((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }

    public static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.DECIMAL;
                break;
            case PostgreSQL:
                sqlType = PostgreType.DECIMAL;
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
