package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class UnsignedMediumIntType extends _NumericType._UnsignedIntegerType {

    public static final UnsignedMediumIntType INSTANCE = new UnsignedMediumIntType();

    public static final int MAX = 0xFFFF_FF;


    public static UnsignedMediumIntType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(UnsignedMediumIntType.class, fieldType);
        }
        return INSTANCE;
    }

    private UnsignedMediumIntType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.MEDIUMINT_UNSIGNED;
                break;
            case PostgreSQL:
                sqlType = PostgreType.INTEGER;
                break;

            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Integer beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        return IntegerType.beforeBind(sqlType, nonNull, 0, MAX);
    }

    @Override
    public Integer afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final int value = (Integer) nonNull;
        if (value < 0 || value > MAX) {
            throw errorValueForSqlType(sqlType, nonNull, valueOutOfMapping(nonNull, UnsignedMediumIntType.class));
        }
        return value;
    }


}
