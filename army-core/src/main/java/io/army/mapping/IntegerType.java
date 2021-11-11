package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;

public final class IntegerType extends AbstractMappingType {


    public static final IntegerType INSTANCE = new IntegerType();


    public static IntegerType build(Class<?> typeClass) {
        if (typeClass != Integer.class) {
            throw AbstractMappingType.createNotSupportJavaTypeException(IntegerType.class, typeClass);
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
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.INT;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.INTEGER;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final int value;
        if (nonNull instanceof Integer) {
            value = (Integer) nonNull;
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE) {
                throw outRangeOfType(nonNull, null);
            }
            value = (int) v;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue();
        } else if (nonNull instanceof BigDecimal) {
            final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
            if (v.scale() != 0
                    || v.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0
                    || v.compareTo(BigDecimal.valueOf(Integer.MIN_VALUE)) < 0) {
                throw outRangeOfType(nonNull, null);
            }
            value = v.intValue();
        } else if (nonNull instanceof BigInteger) {
            final BigInteger v = ((BigInteger) nonNull);
            if (v.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0
                    || v.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
                throw outRangeOfType(nonNull, null);
            }
            value = v.intValue();
        } else if (nonNull instanceof String) {
            try {
                value = Integer.parseInt((String) nonNull);
            } catch (NumberFormatException e) {
                throw outRangeOfType(nonNull, e);
            }
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
