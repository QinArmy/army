package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;

/**
 * <p>
 * This class representing the mapping from {@link BigDecimal} to unsigned decimal.
 * </p>
 *
 * @see BigDecimal
 */
public final class UnsignedBigDecimalType extends _NumericType._UnsignedNumericType
        implements MappingType.SqlDecimalType {


    public static UnsignedBigDecimalType from(final Class<?> fieldType) {
        if (fieldType != BigDecimal.class) {
            throw errorJavaType(UnsignedBigDecimalType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final UnsignedBigDecimalType INSTANCE = new UnsignedBigDecimalType();


    /**
     * private constructor
     */
    private UnsignedBigDecimalType() {
    }

    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }

    @Override
    public BigDecimal convert(MappingEnv env, Object nonNull) throws CriteriaException {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull, null);
        }
        return value;
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, dataType, nonNull, PARAM_ERROR_HANDLER);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        return value;
    }

    @Override
    public BigDecimal afterGet(DataType dataType, MappingEnv env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.toBigDecimal(this, dataType, nonNull, ACCESS_ERROR_HANDLER);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, nonNull, null);
        }
        return value;
    }


    public static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.DECIMAL_UNSIGNED;
                break;
            case PostgreSQL:
                sqlType = PostgreType.DECIMAL;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return sqlType;
    }


}
