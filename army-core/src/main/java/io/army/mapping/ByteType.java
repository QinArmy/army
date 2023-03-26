package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class ByteType extends _NumericType._IntegerType {

    public static final ByteType INSTANCE = new ByteType();

    public static ByteType from(final Class<?> javaType) {
        if (javaType != Byte.class) {
            throw errorJavaType(ByteType.class, javaType);
        }
        return INSTANCE;
    }

    private ByteType() {
    }

    @Override
    public Class<?> javaType() {
        return Byte.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.TINYINT;
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
    public Byte beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return (byte) IntegerType.beforeBind(type, nonNull, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }


    @Override
    public Byte afterGet(SqlType type, MappingEnv env, Object nonNull) {
        final byte value;
        switch (type.database()) {
            case MySQL: {
                if (!(nonNull instanceof Byte)) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
                value = (Byte) nonNull;
            }
            break;
            case PostgreSQL: {
                if (!(nonNull instanceof Short)) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
                value = ((Short) nonNull).byteValue();
            }
            break;
            default:
                throw errorJavaTypeForSqlType(type, nonNull);
        }
        return value;
    }


}
