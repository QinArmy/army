package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * @see Short
 */
public final class ShortType extends _NumericType._IntegerType {

    public static final ShortType INSTANCE = new ShortType();

    public static ShortType from(final Class<?> fieldType) {
        if (fieldType != Short.class) {
            throw errorJavaType(ShortType.class, fieldType);
        }
        return INSTANCE;
    }

    private ShortType() {
    }

    @Override
    public Class<?> javaType() {
        return Short.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.SMALLINT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.SMALLINT;
                break;

            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Short beforeBind(SqlType sqlType, MappingEnv env, final Object nonNull) {
        return (short) IntegerType.beforeBind(sqlType, nonNull, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Override
    public Short afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        final short value;
        switch (sqlType.database()) {
            case MySQL:
            case PostgreSQL: {
                if (!(nonNull instanceof Short)) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
                value = (Short) nonNull;
            }
            break;
            default:
                throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }


}
