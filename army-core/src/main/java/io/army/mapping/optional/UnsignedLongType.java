package io.army.mapping.optional;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.AbstractMappingType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.SqlDataType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;

public final class UnsignedLongType extends AbstractMappingType {

    public static final UnsignedLongType INSTANCE = new UnsignedLongType();

    public static UnsignedLongType build(Class<?> typeClass) {
        if (typeClass != BigDecimal.class) {
            throw createNotSupportJavaTypeException(UnsignedLongType.class, typeClass);
        }
        return INSTANCE;
    }

    public static final BigInteger MAX_UNSIGNED_LONG = new BigInteger(Long.toUnsignedString(-1L));


    private UnsignedLongType() {
    }

    @Override
    public Class<?> javaType() {
        return BigInteger.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.BIGINT;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        if (serverMeta.database() == Database.MySQL) {
            sqlDataType = MySQLDataType.BIGINT_UNSIGNED;
        } else {
            throw noMappingError(serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final BigInteger value;
        if (nonNull instanceof BigInteger) {
            value = (BigInteger) nonNull;
        } else if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigInteger.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? BigInteger.ONE : BigInteger.ZERO;
        } else if (nonNull instanceof String) {
            try {
                value = new BigInteger((String) nonNull);
            } catch (NumberFormatException e) {
                throw outRangeOfType(nonNull, e);
            }
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(MAX_UNSIGNED_LONG) > 0) {
            throw outRangeOfType(nonNull, null);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, final Object nonNull) {
        if (!(nonNull instanceof BigInteger) || ((BigInteger) nonNull).compareTo(BigInteger.ZERO) < 0) {
            throw notSupportConvertAfterGet(nonNull);
        }
        final BigInteger value = (BigInteger) nonNull;
        if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(MAX_UNSIGNED_LONG) > 0) {
            throw outRangeOfType(nonNull, null);
        }
        return nonNull;
    }


}
