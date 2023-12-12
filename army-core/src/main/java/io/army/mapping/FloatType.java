package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.FloatArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class is mapping class of {@link Float}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Float}</li>
 *     <li>{@link Boolean},true:1f,false:0f</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link Float},if overflow,throw {@link io.army.ArmyException}
 ** @since 1.0
 */
public final class FloatType extends _NumericType._FloatNumericType {


    public static FloatType from(final Class<?> fieldType) {
        if (fieldType != Float.class) {
            throw errorJavaType(FloatType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final FloatType INSTANCE = new FloatType();

    /**
     * private constructor
     */
    private FloatType() {
    }

    @Override
    public Class<?> javaType() {
        return Float.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return FloatArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.FLOAT;
                break;
            case PostgreSQL:
                type = PostgreType.REAL;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public Float convert(MappingEnv env, Object source) throws CriteriaException {
        return convertToFloat(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Float beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return convertToFloat(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Float afterGet(DataType dataType, MappingEnv env, Object source) {
        return convertToFloat(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    private static float convertToFloat(final MappingType type, final DataType dataType, final Object nonNull,
                                        final ErrorHandler errorHandler) {
        final float value;
        if (nonNull instanceof Float) {
            value = (Float) nonNull;
        } else if (nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).floatValue();
        } else if (nonNull instanceof String) {
            try {
                value = Float.parseFloat((String) nonNull);
            } catch (NumberFormatException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1f : 0f;
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
