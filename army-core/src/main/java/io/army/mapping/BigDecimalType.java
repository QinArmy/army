package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BigDecimalType extends _NumericType implements _NumericType._DecimalNumeric {

    public static final BigDecimalType INSTANCE = new BigDecimalType();

    public static BigDecimalType from(Class<?> fieldType) {
        if (fieldType != BigDecimal.class) {
            throw errorJavaType(BigDecimalType.class, fieldType);
        }
        return INSTANCE;
    }


    private BigDecimalType() {
    }


    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }


    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.DECIMAL;
                break;
            case PostgreSQL:
                type = PostgreType.DECIMAL;
                break;
            case H2:
                type = H2DataType.DECIMAL;
                break;
            case Oracle:
                type = OracleDataType.NUMBER;
                break;
            default:
                throw noMappingError(meta);

        }
        return type;
    }

    @Override
    public BigDecimal beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToBigDecimal(type, nonNull);
    }

    @Override
    public BigDecimal afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (BigDecimal) nonNull;
    }


    public static BigDecimal convertToBigDecimal(final SqlType type, final Object nonNull) {
        final BigDecimal value;
        if (nonNull instanceof BigDecimal) {
            value = (BigDecimal) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Long
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigDecimal.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof BigInteger) {
            value = new BigDecimal((BigInteger) nonNull);
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? BigDecimal.ONE : BigDecimal.ZERO;
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            value = new BigDecimal(nonNull.toString());
        } else if (nonNull instanceof String) {
            try {
                value = new BigDecimal((String) nonNull);
            } catch (NumberFormatException e) {
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }


}
