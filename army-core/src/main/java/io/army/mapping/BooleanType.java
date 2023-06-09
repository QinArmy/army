package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;

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


    public static final BooleanType INSTANCE = new BooleanType();

    public static BooleanType from(Class<?> fieldType) {
        if (fieldType != Boolean.class) {
            throw errorJavaType(BooleanType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final String TRUE = "TRUE";

    public static final String FALSE = "FALSE";


    private BooleanType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        final SqlType sqlType;
        switch (meta.dialectDatabase()) {
            case MySQL:
                sqlType = MySQLType.BOOLEAN;
                break;
            case Postgre:
                sqlType = PostgreSqlType.BOOLEAN;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return sqlType;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Boolean convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToBoolean(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Boolean beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToBoolean(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Boolean afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToBoolean(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static boolean convertToBoolean(final MappingType type, final Object nonNull,
                                            final BiFunction<MappingType, Object, ArmyException> errorHandler) {
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
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof BigDecimal) {
            value = BigDecimal.ZERO.compareTo((BigDecimal) nonNull) != 0;
        } else if (nonNull instanceof BigInteger) {
            value = BigInteger.ZERO.compareTo((BigInteger) nonNull) != 0;
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            value = Double.compare(((Number) nonNull).doubleValue(), 0.0D) != 0;
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
