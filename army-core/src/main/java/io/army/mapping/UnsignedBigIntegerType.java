package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @see BigInteger
 */
public final class UnsignedBigIntegerType extends _NumericType._UnsignedIntegerType {

    public static final UnsignedBigIntegerType INSTANCE = new UnsignedBigIntegerType();

    public static UnsignedBigIntegerType from(final Class<?> fieldType) {
        if (fieldType != BigInteger.class) {
            throw errorJavaType(UnsignedBigIntegerType.class, fieldType);
        }
        return INSTANCE;
    }


    private UnsignedBigIntegerType() {
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
                sqlType = MySQLTypes.DECIMAL;
                break;
            case PostgreSQL:
                sqlType = PostgreType.DECIMAL;
                break;

            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }


    @Override
    public BigDecimal beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        final BigDecimal value;
        value = BigDecimalType.INSTANCE.beforeBind(type, env, nonNull);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw valueOutRange(type, nonNull, valueOutOfMapping(nonNull));
        }
        return value;
    }

    @Override
    public BigInteger afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        final BigDecimal v = ((BigDecimal) nonNull).stripTrailingZeros();
        if (v.compareTo(BigDecimal.ZERO) < 0) {
            throw valueOutRange(type, nonNull, valueOutOfMapping(nonNull));
        }
        return v.toBigInteger();
    }

    private static IllegalArgumentException valueOutOfMapping(final Object nonNull) {
        String m = String.format("value[%s] out of range of %s .", nonNull, UnsignedBigIntegerType.class.getName());
        return new IllegalArgumentException(m);
    }


}
