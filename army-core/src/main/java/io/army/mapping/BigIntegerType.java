package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlDataType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;

public final class BigIntegerType extends AbstractMappingType {

    public static final BigIntegerType INSTANCE = new BigIntegerType();

    public static BigIntegerType build(Class<?> typeClass) {
        if (typeClass != BigDecimal.class) {
            throw createNotSupportJavaTypeException(BigIntegerType.class, typeClass);
        }
        return INSTANCE;
    }


    private BigIntegerType() {
    }


    @Override
    public Class<?> javaType() {
        return BigInteger.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DECIMAL;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        return BigDecimalType.INSTANCE.sqlDataType(serverMeta);
    }

    @Override
    public BigDecimal convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.INSTANCE.convertBeforeBind(sqlDataType, nonNull).stripTrailingZeros();
        if (value.scale() != 0) {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public BigInteger convertAfterGet(final SqlDataType sqlDataType, final Object nonNull) {
        final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
        if (v.scale() != 0) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return v.toBigInteger();
    }


}
