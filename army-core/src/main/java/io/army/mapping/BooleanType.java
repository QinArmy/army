package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * This class is mapping class of {@link Boolean}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte},non-zero is true</li>
 *     <li>{@link Short},non-zero is true</li>
 *     <li>{@link Integer},non-zero is true</li>
 *     <li>{@link Long},non-zero is true</li>
 *     <li>{@link java.math.BigInteger},non-zero is true</li>
 *     <li>{@link java.math.BigDecimal},non-zero is true</li>
 *     <li>{@link String} , true or false ,case insensitive</li>
 * </ul>
 *  to {@link Boolean},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class BooleanType extends _ArmyNoInjectionMapping {


    public static BooleanType from(Class<?> fieldType) {
        if (fieldType != Boolean.class) {
            throw errorJavaType(BooleanType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final BooleanType INSTANCE = new BooleanType();


    public static final String TRUE = "TRUE";

    public static final String FALSE = "FALSE";

    /**
     * private constructor
     */
    private BooleanType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.BOOLEAN;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BOOLEAN;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return sqlType;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Boolean convert(MappingEnv env, Object source) throws CriteriaException {
        return convertToBoolean(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Boolean beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return convertToBoolean(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Boolean afterGet(DataType dataType, MappingEnv env, final Object source) {
        return convertToBoolean(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    private static boolean convertToBoolean(final MappingType type, final DataType dataType, final Object nonNull,
                                            final ErrorHandler errorHandler) {
        final boolean value;
        if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull;
        } else if (nonNull instanceof Integer) {
            value = ((Integer) nonNull) != 0;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue() != 0;
        } else if (nonNull instanceof Long) {
            value = ((Long) nonNull) != 0L;
        } else if (nonNull instanceof String) {
            if (TRUE.equalsIgnoreCase((String) nonNull)) {
                value = true;
            } else if (FALSE.equalsIgnoreCase((String) nonNull)) {
                value = false;
            } else {
                throw errorHandler.apply(type, dataType, nonNull, null);
            }
        } else if (nonNull instanceof BigDecimal) {
            value = BigDecimal.ZERO.compareTo((BigDecimal) nonNull) != 0;
        } else if (nonNull instanceof BigInteger) {
            value = BigInteger.ZERO.compareTo((BigInteger) nonNull) != 0;
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            value = Double.compare(((Number) nonNull).doubleValue(), 0.0D) != 0;
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
