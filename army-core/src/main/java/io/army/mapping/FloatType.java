package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;


public final class FloatType extends _ArmyNoInjectionMapping {

    public static final FloatType INSTANCE = new FloatType();

    public static FloatType create(Class<?> javaType) {
        if (javaType != Float.class) {
            throw errorJavaType(FloatType.class, javaType);
        }
        return INSTANCE;
    }

    private FloatType() {
    }

    @Override
    public Class<?> javaType() {
        return Float.class;
    }


    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.FLOAT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.REAL;
                break;
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Float beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        final float value;
        if (nonNull instanceof Float) {
            value = (Float) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = Float.parseFloat((String) nonNull);
            } catch (NumberFormatException e) {
                throw valueOutRange(sqlType, nonNull, null);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public Float afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof Float)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (Float) nonNull;
    }


}
