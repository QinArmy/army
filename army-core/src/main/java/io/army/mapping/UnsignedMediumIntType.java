package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
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
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.MEDIUMINT_UNSIGNED;
                break;
            case PostgreSQL:
                type = PostgreTypes.INTEGER;
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
        return IntegerType._convertToInt(this, nonNull, 0, MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, 0, MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, 0, MAX_VALUE, DATA_ACCESS_ERROR_HANDLER);
    }


}
