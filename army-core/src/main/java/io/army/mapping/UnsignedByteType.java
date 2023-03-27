package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * @see Short
 */
public final class UnsignedByteType extends _NumericType._UnsignedIntegerType {

    public static final UnsignedByteType INSTANCE = new UnsignedByteType();

    public static UnsignedByteType from(final Class<?> fieldType) {
        if (fieldType != Short.class) {
            throw errorJavaType(UnsignedByteType.class, fieldType);
        }
        return INSTANCE;
    }


    private UnsignedByteType() {
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
                sqlType = MySQLTypes.TINYINT_UNSIGNED;
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
    public Short beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return (short) IntegerType._beforeBind(type, nonNull, 0, 0xFF);
    }

    @Override
    public Short afterGet(SqlType type, MappingEnv env, Object nonNull) {
        switch (type.database()) {
            case MySQL:
            case PostgreSQL: {
                if (!(nonNull instanceof Short)) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
            }
            break;

            case Oracle:
            case H2:
            default:
                throw errorJavaTypeForSqlType(type, nonNull);
        }
        final Short value = (Short) nonNull;
        if (value < 0 || value > 0xFF) {
            throw errorValueForSqlType(type, nonNull, null);
        }
        return value;
    }


}
