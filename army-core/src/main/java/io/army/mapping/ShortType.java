package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.ShortArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
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
*
 * @since 1.0
 */
public final class ShortType extends _NumericType._IntegerType {

    public static ShortType from(final Class<?> fieldType) {
        if (fieldType != Short.class) {
            throw errorJavaType(ShortType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final ShortType INSTANCE = new ShortType();

    /**
     * private constructor
     */
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
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return ShortArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.SMALLINT;
                break;
            case PostgreSQL:
                type = PostgreType.SMALLINT;
                break;

            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }


    @Override
    public Short convert(MappingEnv env, Object source) throws CriteriaException {
        return (short) IntegerType.toInt(this, map(env.serverMeta()), source, Short.MIN_VALUE, Short.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Short beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return (short) IntegerType.toInt(this, dataType, source, Short.MIN_VALUE, Short.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Short afterGet(DataType dataType, MappingEnv env, Object source) {
        return (short) IntegerType.toInt(this, dataType, source, Short.MIN_VALUE, Short.MAX_VALUE, ACCESS_ERROR_HANDLER);
    }


}
