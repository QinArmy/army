package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * @see Integer
 */
public final class UnsignedShortType extends _ArmyNoInjectionMapping {

    public static final UnsignedShortType INSTANCE = new UnsignedShortType();

    public static UnsignedShortType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(UnsignedShortType.class, fieldType);
        }
        return INSTANCE;
    }


    private UnsignedShortType() {
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
                sqlType = MySqlType.SMALLINT_UNSIGNED;
                break;
            case PostgreSQL:
                sqlType = PostgreType.INTEGER;
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
    public Integer beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        final int value;
        value = IntegerType.beforeBind(sqlType, nonNull, 0, 0xFFFF);
        if (value < 0 || value > 0xFFFF) {
            throw valueOutRange(sqlType, nonNull, valueOutOfMapping(nonNull));
        }
        return value;
    }

    @Override
    public Integer afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final int value = (Integer) nonNull;
        if (value < 0 || value > 0xFFFF) {
            throw errorValueForSqlType(sqlType, nonNull, valueOutOfMapping(nonNull));
        }
        return value;
    }

    private static IllegalArgumentException valueOutOfMapping(final Object nonNull) {
        String m = String.format("value[%s] out of range of %s .", nonNull, UnsignedShortType.class.getName());
        return new IllegalArgumentException(m);
    }


}
