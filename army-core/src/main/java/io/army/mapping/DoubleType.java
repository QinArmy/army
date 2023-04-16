package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;

import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link Double}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Float}</li>
 *     <li>{@link Boolean},true:1d,false:0d</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link Double},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class DoubleType extends _NumericType._FloatNumericType {


    public static final DoubleType INSTANCE = new DoubleType();

    public static DoubleType from(final Class<?> fieldType) {
        if (fieldType != Double.class) {
            throw errorJavaType(DoubleType.class, fieldType);
        }
        return INSTANCE;
    }

    private DoubleType() {
    }

    @Override
    public Class<?> javaType() {
        return Double.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.DOUBLE;
                break;
            case PostgreSQL:
                type = PostgreTypes.DOUBLE;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public Object convert(MappingEnv env, final Object nonNull) throws CriteriaException {
        return convertToDouble(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Double beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToDouble(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Double afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return convertToDouble(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static double convertToDouble(final MappingType type, final Object nonNull,
                                          final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final double value;
        if (nonNull instanceof Double) {
            value = (Double) nonNull;
        } else if (nonNull instanceof Float
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).doubleValue();
        } else if (nonNull instanceof String) {
            try {
                value = Double.parseDouble((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1d : 0d;
        } else {//TODO consider Long
            throw errorHandler.apply(type, nonNull);
        }

        return value;
    }


}
