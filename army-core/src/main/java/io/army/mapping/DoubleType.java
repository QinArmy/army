package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.DoubleArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

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
 *
 * @since 0.6.0
 */
public final class DoubleType extends _NumericType._FloatNumericType {


    public static DoubleType from(final Class<?> javaType) {
        if (javaType != Double.class) {
            throw errorJavaType(DoubleType.class, javaType);
        }
        return INSTANCE;
    }

    public static final DoubleType INSTANCE = new DoubleType();

    /**
     * private constructor
     */
    private DoubleType() {
    }

    @Override
    public Class<?> javaType() {
        return Double.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return DoubleArrayType.LINEAR;
    }

    @Override
    public DataType map(ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.DOUBLE;
                break;
            case PostgreSQL:
                type = PostgreType.FLOAT8;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public Object convert(MappingEnv env, final Object source) throws CriteriaException {
        return toDouble(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }


    @Override
    public Double beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toDouble(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Double afterGet(DataType dataType, MappingEnv env, Object source) {
        return toDouble(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    private static double toDouble(final MappingType type, final DataType dataType, final Object nonNull,
                                   final ErrorHandler errorHandler) {
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
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1d : 0d;
        } else {//TODO consider Long
            throw errorHandler.apply(type, dataType, nonNull, null);
        }

        return value;
    }


}
