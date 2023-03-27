package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
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
    public SqlType map(ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.FLOAT;
                break;
            case PostgreSQL:
                type = PostgreType.REAL;
                break;
            default:
                throw noMappingError(meta);
        }
        return type;
    }

    @Override
    public Float beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final float value;
        if (nonNull instanceof Float) {
            value = (Float) nonNull;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).floatValue();
        } else if (nonNull instanceof String) {
            try {
                value = Float.parseFloat((String) nonNull);
            } catch (NumberFormatException e) {
                throw valueOutRange(type, nonNull, null);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public Float afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Float)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (Float) nonNull;
    }


}
