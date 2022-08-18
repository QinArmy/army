package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class LongType extends _NumericType {

    public static final LongType INSTANCE = new LongType();


    public static LongType from(final Class<?> fieldType) {
        if (fieldType != Long.class) {
            throw errorJavaType(LongType.class, fieldType);
        }
        return INSTANCE;
    }

    private LongType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.BIGINT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BIGINT;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public Long beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        return beforeBind(sqlType, nonNull, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    public Long afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Long)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (Long) nonNull;
    }

    public static long beforeBind(SqlType sqlType, final Object nonNull, final long min, final long max) {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).longValue();
            if (value < min || value > max) {
                throw valueOutRange(sqlType, nonNull, null);
            }
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0
                    || v.compareTo(BigDecimal.valueOf(max)) > 0
                    || v.compareTo(BigDecimal.valueOf(min)) < 0) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = v.longValue();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = ((BigInteger) nonNull);
            if (v.compareTo(BigInteger.valueOf(max)) > 0 || v.compareTo(BigInteger.valueOf(min)) < 0) {
                throw valueOutRange(sqlType, nonNull, null);
            }
            value = v.longValue();
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseLong((String) nonNull);
            } catch (NumberFormatException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
            if (value < min || value > max) {
                throw valueOutRange(sqlType, nonNull, null);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }


}
