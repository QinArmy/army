package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
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
 *  to  (unsigned) int,if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class UnsignedMediumIntType extends _NumericType._UnsignedIntegerType {

    public static final UnsignedMediumIntType INSTANCE = new UnsignedMediumIntType();

    public static final int MAX_VALUE = 0xFFFF_FF;


    public static UnsignedMediumIntType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(UnsignedMediumIntType.class, fieldType);
        }
        return INSTANCE;
    }

    private UnsignedMediumIntType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.MEDIUMINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreType.INTEGER;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public Integer convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return IntegerType.toInt(this, nonNull, 0, MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return IntegerType.toInt(this, nonNull, 0, MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return IntegerType.toInt(this, nonNull, 0, MAX_VALUE, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
