package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;

public final class BigDecimalType extends AbstractMappingType {

    private static final BigDecimalType INSTANCE = new BigDecimalType();

    public static BigDecimalType build(Class<?> typeClass) {
        if (typeClass != BigDecimal.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(BigDecimalType.class, typeClass);
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
    public JDBCType jdbcType() {
        return JDBCType.DECIMAL;
    }

    @Override
    public SqlType sqlDataType(final ServerMeta serverMeta) throws NoMappingException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.DECIMAL;
                break;
            case Postgre:
                sqlDataType = PostgreDataType.DECIMAL;
                break;
            default:
                throw noMappingError(javaType(), serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(final ServerMeta serverMeta, final Object nonNull) {
        final BigDecimal value;
        if (nonNull instanceof BigDecimal) {
            value = (BigDecimal) nonNull;
        } else if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigDecimal.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof BigInteger) {
            value = new BigDecimal((BigInteger) nonNull);
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? BigDecimal.ONE : BigDecimal.ZERO;
        } else if (nonNull instanceof String) {
            value = new BigDecimal((String) nonNull);
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(final ServerMeta serverMeta, final Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
