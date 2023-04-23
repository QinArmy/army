package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PgSqlType;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class is mapping class of medium {@link Integer}.
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
 *  to medium {@link Integer},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class MediumIntType extends _NumericType {

    public static final MediumIntType INSTANCE = new MediumIntType();

    public static final int MAX_VALUE = 0x7FFF_FF;

    public static final int MIN_VALUE = -MAX_VALUE - 1;


    public static MediumIntType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(MediumIntType.class, fieldType);
        }
        return INSTANCE;
    }

    private MediumIntType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.dialectDatabase()) {
            case MySQL:
                sqlType = MySQLType.MEDIUMINT;
                break;
            case PostgreSQL:
                sqlType = PgSqlType.INTEGER;
                break;

            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return sqlType;
    }


    @Override
    public Integer convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return IntegerType._convertToInt(this, nonNull, MIN_VALUE, MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, MIN_VALUE, MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, MIN_VALUE, MAX_VALUE, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
