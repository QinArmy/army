package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;


/**
 * <p>
 * This class is mapping class of {@link Long}.
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
 *  to (unsigned) int,if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class UnsignedIntegerType extends _NumericType._UnsignedIntegerType {

    public static UnsignedIntegerType from(final Class<?> fieldType) {
        if (fieldType != Long.class) {
            throw errorJavaType(UnsignedIntegerType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final UnsignedIntegerType INSTANCE = new UnsignedIntegerType();

    /**
     * private constructor
     */
    private UnsignedIntegerType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }


    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.INT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreType.BIGINT;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public Long convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return LongType.toLong(this, map(env.serverMeta()), nonNull, 0L, 0xFFFF_FFFFL, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, Object nonNull) {
        return LongType.toLong(this, dataType, nonNull, 0L, 0xFFFF_FFFFL, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long afterGet(DataType dataType, MappingEnv env, Object nonNull) {
        return LongType.toLong(this, dataType, nonNull, 0L, 0xFFFF_FFFFL, ACCESS_ERROR_HANDLER);
    }


}
