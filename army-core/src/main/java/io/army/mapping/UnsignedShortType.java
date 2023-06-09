package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class is mapping class of {@link Integer}.
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
 *  to  (unsigned medium) int,if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class UnsignedShortType extends _NumericType._UnsignedIntegerType {

    public static final UnsignedShortType INSTANCE = new UnsignedShortType();

    public static UnsignedShortType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(UnsignedShortType.class, fieldType);
        }
        return INSTANCE;
    }


    private UnsignedShortType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.MEDIUM;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.SMALLINT_UNSIGNED;
                break;
            case Postgre:
                type = PostgreDataType.INTEGER;
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
    public Integer convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return IntegerType._convertToInt(this, nonNull, 0, 0xFFFF, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, 0, 0xFFFF, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, 0, 0xFFFF, DATA_ACCESS_ERROR_HANDLER_0);
    }



}
