package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;

public final class LongType extends _ArmyNoInjectionMapping {

    public static final LongType INSTANCE = new LongType();


    public static LongType build(Class<?> javaType) {
        if (javaType != Long.class) {
            throw createNotSupportJavaTypeException(LongType.class, javaType);
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
    public JDBCType jdbcType() {
        return JDBCType.BIGINT;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySqlType.BIGINT;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.BIGINT;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        final long value;
        if (nonNull instanceof Long) {
            value = (Long) nonNull;
        } else if (nonNull instanceof Integer || nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).longValue();
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0
                    || v.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0
                    || v.compareTo(BigDecimal.valueOf(Long.MIN_VALUE)) < 0) {
                throw outRangeOfType(nonNull, null);
            }
            value = v.longValue();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = ((BigInteger) nonNull);
            if (v.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                    || v.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                throw outRangeOfType(nonNull, null);
            }
            value = v.longValue();
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseLong((String) nonNull);
            } catch (NumberFormatException e) {
                throw outRangeOfType(nonNull, e);
            }
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
