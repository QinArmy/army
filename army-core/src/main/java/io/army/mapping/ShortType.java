package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class is mapping class of {@link Short}.
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
 *  to {@link Short},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class ShortType extends _NumericType._IntegerType {

    public static final ShortType INSTANCE = new ShortType();

    public static ShortType from(final Class<?> fieldType) {
        if (fieldType != Short.class) {
            throw errorJavaType(ShortType.class, fieldType);
        }
        return INSTANCE;
    }

    private ShortType() {
    }

    @Override
    public Class<?> javaType() {
        return Short.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.SMALL;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.SMALLINT;
                break;
            case Postgre:
                type = PostgreDataType.SMALLINT;
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
    public Short convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return (short) IntegerType._convertToInt(this, nonNull, Short.MIN_VALUE, Short.MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Short beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return (short) IntegerType._convertToInt(this, nonNull, Short.MIN_VALUE, Short.MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Short afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return (short) IntegerType._convertToInt(this, nonNull, Short.MIN_VALUE, Short.MAX_VALUE, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
