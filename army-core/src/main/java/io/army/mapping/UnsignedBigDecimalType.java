package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;

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

    public static final UnsignedBigDecimalType INSTANCE = new UnsignedBigDecimalType();

    public static UnsignedBigDecimalType from(final Class<?> fieldType) {
        if (fieldType != BigDecimal.class) {
            throw errorJavaType(UnsignedBigDecimalType.class, fieldType);
        }
        return INSTANCE;
    }


    private UnsignedBigDecimalType() {
    }

    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }

    @Override
    public SQLType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }

    @Override
    public BigDecimal convert(MappingEnv env, Object nonNull) throws CriteriaException {
        final BigDecimal value;
        value = BigDecimalType._convertToBigDecimal(this, nonNull, PARAM_ERROR_HANDLER_0);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }

    @Override
    public BigDecimal beforeBind(SQLType type, MappingEnv env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType._convertToBigDecimal(this, nonNull, PARAM_ERROR_HANDLER_0);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }

    @Override
    public BigDecimal afterGet(SQLType type, MappingEnv env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType._convertToBigDecimal(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }


    static SQLType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SQLType sqlType;
        switch (meta.dialectDatabase()) {
            case MySQL:
                sqlType = MySQLType.DECIMAL_UNSIGNED;
                break;
            case PostgreSQL:
                sqlType = PostgreSqlType.DECIMAL;
                break;
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return sqlType;
    }


}
