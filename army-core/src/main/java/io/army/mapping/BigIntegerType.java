package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BigIntegerType extends _ArmyNoInjectionMapping {

    public static final BigIntegerType INSTANCE = new BigIntegerType();

    public static BigIntegerType from(Class<?> fieldType) {
        if (fieldType != BigDecimal.class) {
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
    public BigDecimal beforeBind(SqlType sqlType, MappingEnv env, final Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.INSTANCE.beforeBind(sqlType, env, nonNull);
        if (value.scale() != 0) {
            throw valueOutRange(sqlType, nonNull, null);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(final SqlType sqlType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final BigDecimal value = ((BigDecimal) nonNull).stripTrailingZeros();
        if (value.scale() != 0) {
            throw errorValueForSqlType(sqlType, nonNull, null);
        }
        return value.toBigInteger();
    }


}
