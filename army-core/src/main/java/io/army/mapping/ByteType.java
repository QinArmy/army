package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;

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
 *     <li>{@link Boolean} true : 1 , false: 0</li>
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
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public SQLType map(final ServerMeta meta) {
        final SQLType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.TINYINT;
                break;
            case PostgreSQL:
                type = PostgreSqlType.SMALLINT;
                break;

            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public Byte convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return (byte) IntegerType._convertToInt(this, nonNull, Byte.MIN_VALUE, Byte.MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Number beforeBind(final SQLType type, MappingEnv env, final Object nonNull) {
        final int intValue;
        intValue = IntegerType._convertToInt(this, nonNull, Byte.MIN_VALUE, Byte.MAX_VALUE, PARAM_ERROR_HANDLER_0);
        final Number value;
        switch (type.database()) {
            case MySQL:
                value = (byte) intValue;
                break;
            case PostgreSQL:
                value = (short) intValue;
                break;
            default:
                throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }


    @Override
    public Byte afterGet(SQLType type, MappingEnv env, Object nonNull) {
        return (byte) IntegerType._convertToInt(this, nonNull, Byte.MIN_VALUE, Byte.MAX_VALUE, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
