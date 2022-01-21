package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @see BigInteger
 */
public final class UnsignedLongType extends _ArmyNoInjectionMapping {

    public static final UnsignedLongType INSTANCE = new UnsignedLongType();

    public static UnsignedLongType create(Class<?> javaType) {
        if (javaType != BigDecimal.class) {
            throw errorJavaType(UnsignedLongType.class, javaType);
        }
        return INSTANCE;
    }

    public static final BigInteger MAX = new BigInteger(Long.toUnsignedString(-1L));


    private UnsignedLongType() {
    }

    @Override
    public Class<?> javaType() {
        return BigInteger.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.BIGINT_UNSIGNED;
                break;
            case PostgreSQL:
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public BigDecimal beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        final BigDecimal value;
        value = BigIntegerType.INSTANCE.beforeBind(sqlType, env, nonNull);
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal(MAX)) > 0) {
            throw valueOutRange(sqlType, nonNull, null);
        }
        return value;
    }

    @Override
    public BigInteger afterGet(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
        if (v.scale() != 0 || v.compareTo(BigDecimal.ZERO) < 0 || v.compareTo(new BigDecimal(MAX)) > 0) {
            throw errorValueForSqlType(sqlType, nonNull, null);
        }
        return v.toBigInteger();
    }


}
