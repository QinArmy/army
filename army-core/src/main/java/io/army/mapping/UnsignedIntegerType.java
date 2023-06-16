package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
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

    public static final UnsignedIntegerType INSTANCE = new UnsignedIntegerType();

    public static UnsignedIntegerType from(final Class<?> fieldType) {
        if (fieldType != Long.class) {
            throw errorJavaType(UnsignedIntegerType.class, fieldType);
        }
        return INSTANCE;
    }

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
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.INT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreSqlType.BIGINT;
                break;
            case Oracle:
            case H2:
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
    public Long convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return LongType._convertToLong(this, nonNull, 0L, 0xFFFF_FFFFL, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Long beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return LongType._convertToLong(this, nonNull, 0L, 0xFFFF_FFFFL, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Long afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return LongType._convertToLong(this, nonNull, 0L, 0xFFFF_FFFFL, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
