package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
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
 *  to  (unsigned medium) int,if overflow,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class UnsignedShortType extends _NumericType._UnsignedIntegerType {


    public static UnsignedShortType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(UnsignedShortType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final UnsignedShortType INSTANCE = new UnsignedShortType();

    /**
     * private constructor
     */
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
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.SMALLINT_UNSIGNED;
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
    public Integer convert(MappingEnv env, Object source) throws CriteriaException {
        return IntegerType.toInt(this, map(env.serverMeta()), source, 0, 0xFFFF, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return IntegerType.toInt(this, dataType, source, 0, 0xFFFF, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer afterGet(DataType dataType, MappingEnv env, Object source) {
        return IntegerType.toInt(this, dataType, source, 0, 0xFFFF, ACCESS_ERROR_HANDLER);
    }


}
