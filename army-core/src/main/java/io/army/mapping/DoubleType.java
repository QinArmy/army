package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
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
                type = PostgreType.DOUBLE;
                break;
            default:
                throw noMappingError(meta);
        }
        return type;
    }

    @Override
    public Double beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
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
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public Double afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Double)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (Double) nonNull;
    }


}
