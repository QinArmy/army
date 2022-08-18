package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class IntegerType extends _NumericType {


    public static final IntegerType INSTANCE = new IntegerType();


    public static IntegerType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(IntegerType.class, fieldType);
        }
        return INSTANCE;
    }


    private IntegerType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.INT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.INTEGER;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public Integer beforeBind(SqlType sqlType, final MappingEnv env, final Object nonNull) {
        return beforeBind(sqlType, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Integer afterGet(SqlType sqlType, final MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (Integer) nonNull;
    }


    public static int beforeBind(final SqlType sqlType, final Object nonNull, final int min, final int max) {
        final int value;
        if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            final int v = ((Number) nonNull).intValue();
            if (v < min || v > max) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = (byte) v;
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v < min || v > max) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = (byte) v;
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.compareTo(BigDecimal.valueOf(min)) < 0
                    || v.compareTo(BigDecimal.valueOf(max)) > 0) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = v.byteValue();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = (BigInteger) nonNull;
            if (v.compareTo(BigInteger.valueOf(min)) < 0
                    || v.compareTo(BigInteger.valueOf(max)) > 0) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = v.byteValue();
        } else if (nonNull instanceof String) {
            final int v;
            try {
                v = Integer.parseInt((String) nonNull);
                if (v < min || v > max) {
                    throw valueOutRange(sqlType, nonNull, null);
                }
            } catch (NumberFormatException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
            value = v;
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }


}
