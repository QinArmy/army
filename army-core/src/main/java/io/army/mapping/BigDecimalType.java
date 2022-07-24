package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BigDecimalType extends _ArmyNoInjectionMapping {

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
        final SqlType sqlDataType;
        switch (meta.database()) {
            case MySQL:
                sqlDataType = MySqlType.DECIMAL;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.DECIMAL;
                break;
            case H2:
                sqlDataType = H2DataType.DECIMAL;
                break;
            case Oracle:
                sqlDataType = OracleDataType.NUMBER;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlDataType;
    }

    @Override
    public BigDecimal beforeBind(SqlType sqlType, MappingEnv env, final Object nonNull) {
        return beforeBind(sqlType, nonNull);
    }

    @Override
    public BigDecimal afterGet(SqlType sqlType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (BigDecimal) nonNull;
    }


    public static BigDecimal beforeBind(final SqlType sqlType, final Object nonNull) {
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
        } else if (nonNull instanceof String) {
            try {
                value = new BigDecimal((String) nonNull);
            } catch (NumberFormatException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }


}
