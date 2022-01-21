package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @see BigInteger
 */
public final class UnsignedBigInteger extends _ArmyNoInjectionMapping {

    public static final UnsignedBigInteger INSTANCE = new UnsignedBigInteger();

    public static UnsignedBigInteger create(Class<?> javaType) {
        if (javaType != BigInteger.class) {
            throw errorJavaType(UnsignedBigInteger.class, javaType);
        }
        return INSTANCE;
    }


    private UnsignedBigInteger() {
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
                sqlType = MySqlType.DECIMAL;
                break;
            case PostgreSQL:
                sqlType = PostgreType.DECIMAL;
                break;
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }


    @Override
    public BigDecimal beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.INSTANCE.beforeBind(sqlType, env, nonNull);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw valueOutRange(sqlType, nonNull, valueOutOfMapping(nonNull));
        }
        return value;
    }

    @Override
    public BigInteger afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
        if (v.compareTo(BigDecimal.ZERO) < 0) {
            throw valueOutRange(sqlType, nonNull, valueOutOfMapping(nonNull));
        }
        return v.toBigInteger();
    }

    private static IllegalArgumentException valueOutOfMapping(final Object nonNull) {
        String m = String.format("value[%s] out of range of %s .", nonNull, UnsignedBigInteger.class.getName());
        return new IllegalArgumentException(m);
    }


}
