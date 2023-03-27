package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class is mapping class of {@link Byte}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link java.math.BigDecimal},it has a zero fractional part</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link Byte},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class ByteType extends _NumericType._IntegerType {

    public static final ByteType INSTANCE = new ByteType();

    public static ByteType from(final Class<?> javaType) {
        if (javaType != Byte.class) {
            throw errorJavaType(ByteType.class, javaType);
        }
        return INSTANCE;
    }

    private ByteType() {
    }

    @Override
    public Class<?> javaType() {
        return Byte.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.TINYINT;
                break;
            case PostgreSQL:
                type = PostgreType.SMALLINT;
                break;

            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return type;
    }

    @Override
    public Byte beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return (byte) IntegerType._beforeBind(type, nonNull, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }


    @Override
    public Byte afterGet(SqlType type, MappingEnv env, Object nonNull) {
        final byte value;
        switch (type.database()) {
            case MySQL: {
                if (!(nonNull instanceof Byte)) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
                value = (Byte) nonNull;
            }
            break;
            case PostgreSQL: {
                if (!(nonNull instanceof Short)) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
                value = ((Short) nonNull).byteValue();
            }
            break;
            default:
                throw errorJavaTypeForSqlType(type, nonNull);
        }
        return value;
    }


}
