package io.army.mapping.optional;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.AbstractMappingType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.SqlDataType;

import java.math.BigDecimal;
import java.sql.JDBCType;

public final class UnsignedIntegerType extends AbstractMappingType {

    public static final UnsignedIntegerType INSTANCE = new UnsignedIntegerType();

    public static UnsignedIntegerType build(Class<?> typeClass) {
        if (typeClass != BigDecimal.class) {
            throw createNotSupportJavaTypeException(UnsignedIntegerType.class, typeClass);
        }
        return INSTANCE;
    }

    private UnsignedIntegerType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        if (serverMeta.database() == Database.MySQL) {
            sqlDataType = MySQLDataType.INT_UNSIGNED;
        } else {
            throw noMappingError(serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Long convertBeforeBind(SqlDataType sqlDataType, Object nonNull) {
        final long value;
        if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).longValue();
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? 1L : 0L;
        } else if (nonNull instanceof String) {
            try {
                value = Long.parseLong((String) nonNull);
            } catch (NumberFormatException e) {
                throw outRangeOfType(nonNull, e);
            }
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        if (value < 0L || value > 0xFFFF_FFFFL) {
            throw outRangeOfType(nonNull, null);
        }
        return value;
    }

    @Override
    public Long convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Long)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        final Long value = (Long) nonNull;
        if (value < 0L || value > 0xFFFF_FFFFL) {
            throw outRangeOfType(nonNull, null);
        }
        return value;
    }


}
