package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BigIntegerType extends _NumericType._IntegerType {

    public static final BigIntegerType INSTANCE = new BigIntegerType();

    public static BigIntegerType from(Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(BigIntegerType.class, fieldType);
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
    public SqlType map(ServerMeta meta) {
        return BigDecimalType.INSTANCE.map(meta);
    }

    @Override
    public BigDecimal beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.INSTANCE.beforeBind(type, env, nonNull);
        if (value.scale() != 0) {
            throw valueOutRange(type, nonNull, null);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(final SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        final BigDecimal value = ((BigDecimal) nonNull).stripTrailingZeros();
        if (value.scale() != 0) {
            throw errorValueForSqlType(type, nonNull, null);
        }
        return value.toBigInteger();
    }


}
