package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;

public final class UnsignedBigDecimalType extends _NumericType._UnsignedNumericType
        implements _NumericType._DecimalNumeric {

    public static final UnsignedBigDecimalType INSTANCE = new UnsignedBigDecimalType();

    public static UnsignedBigDecimalType from(final Class<?> fieldType) {
        if (fieldType != BigDecimal.class) {
            throw errorJavaType(UnsignedBigDecimalType.class, fieldType);
        }
        return INSTANCE;
    }


    private UnsignedBigDecimalType() {
    }

    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.DECIMAL_UNSIGNED;
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
    public BigDecimal beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        return BigDecimalType.beforeBind(sqlType, nonNull);
    }

    @Override
    public BigDecimal afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final BigDecimal value = (BigDecimal) nonNull;
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw errorValueForSqlType(sqlType, nonNull, valueOutOfMapping(nonNull, UnsignedBigDecimalType.class));
        }
        return value;
    }


}
