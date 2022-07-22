package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class DoubleType extends _ArmyNoInjectionMapping {


    public static final DoubleType INSTANCE = new DoubleType();

    public static DoubleType from(final Class<?> fieldType) {
        if (fieldType != Double.class) {
            throw errorJavaType(DoubleType.class, fieldType);
        }
        return INSTANCE;
    }

    private DoubleType() {
    }

    @Override
    public Class<?> javaType() {
        return Double.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.DOUBLE;
                break;
            case PostgreSQL:
                sqlType = PostgreType.DOUBLE;
                break;
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Double beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        final double value;
        if (nonNull instanceof Double) {
            value = (Double) nonNull;
        } else if (nonNull instanceof Float) {
            value = ((Float) nonNull).doubleValue();
        } else if (nonNull instanceof String) {
            try {
                value = Double.parseDouble((String) nonNull);
            } catch (NumberFormatException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public Double afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Double)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (Double) nonNull;
    }


}
