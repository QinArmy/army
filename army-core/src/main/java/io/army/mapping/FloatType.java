package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.util.function.BiFunction;

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
 * </p>
 *
 * @since 1.0
 */
public final class FloatType extends _NumericType._FloatNumericType {

    public static final FloatType INSTANCE = new FloatType();

    public static FloatType from(final Class<?> fieldType) {
        if (fieldType != Float.class) {
            throw errorJavaType(FloatType.class, fieldType);
        }
        return INSTANCE;
    }

    private FloatType() {
    }

    @Override
    public Class<?> javaType() {
        return Float.class;
    }


    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.FLOAT;
                break;
            case PostgreSQL:
                type = PostgreDataType.REAL;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Float convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToFloat(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Float beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToFloat(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Float afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return convertToFloat(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }

    private static float convertToFloat(final MappingType type, final Object nonNull,
                                        final BiFunction<MappingType, Object, ArmyException> errorHandler) {
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
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1f : 0f;
        } else {//TODO consider int,long,double
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
