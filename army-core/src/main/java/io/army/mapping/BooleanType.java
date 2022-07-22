package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BooleanType extends _ArmyNoInjectionMapping {


    public static final BooleanType INSTANCE = new BooleanType();

    public static BooleanType from(Class<?> fieldType) {
        if (fieldType != Boolean.class) {
            throw errorJavaType(BooleanType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final String TRUE = "TRUE";

    public static final String FALSE = "FALSE";


    private BooleanType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.BOOLEAN;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BOOLEAN;
                break;
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }


    @Override
    public Boolean beforeBind(SqlType sqlType, MappingEnv env, final Object nonNull) {
        final boolean value;
        if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull;
        } else if (nonNull instanceof Integer || nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue() != 0;
        } else if (nonNull instanceof Long) {
            value = ((Number) nonNull).longValue() != 0L;
        } else if (nonNull instanceof String) {
            if (TRUE.equalsIgnoreCase((String) nonNull)) {
                value = true;
            } else if (FALSE.equalsIgnoreCase((String) nonNull)) {
                value = false;
            } else {
                throw valueOutRange(sqlType, nonNull, null);
            }
        } else if (nonNull instanceof BigDecimal) {
            value = BigDecimal.ZERO.compareTo((BigDecimal) nonNull) != 0;
        } else if (nonNull instanceof BigInteger) {
            value = BigInteger.ZERO.compareTo((BigInteger) nonNull) != 0;
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            value = Double.compare(((Number) nonNull).doubleValue(), 0.0D) != 0;
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public Boolean afterGet(SqlType sqlType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof Boolean)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (Boolean) nonNull;
    }


}
