package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class MediumIntType extends _ArmyNoInjectionMapping {

    public static final MediumIntType INSTANCE = new MediumIntType();

    public static final int MAX = 0x7FFF_FF;

    public static final int MIN = -MAX - 1;


    public static MediumIntType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(MediumIntType.class, fieldType);
        }
        return INSTANCE;
    }

    private MediumIntType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.MEDIUMINT;
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
        return IntegerType.beforeBind(sqlType, nonNull, MIN, MAX);
    }

    @Override
    public Integer afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final int value = (Integer) nonNull;
        if (value < MIN || value > MAX) {
            throw errorValueForSqlType(sqlType, nonNull, valueOutOfMapping(nonNull, MediumIntType.class));
        }
        return value;
    }


}
