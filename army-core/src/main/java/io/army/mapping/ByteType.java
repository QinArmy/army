package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class ByteType extends _ArmyNoInjectionMapping {

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
                sqlType = MySqlType.TINYINT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.SMALLINT;
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
    public Byte beforeBind(SqlType sqlType, MappingEnv env, final Object nonNull) {
        return (byte) IntegerType.beforeBind(sqlType, nonNull, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }


    @Override
    public Byte afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        final byte value;
        switch (sqlType.database()) {
            case MySQL: {
                if (!(nonNull instanceof Byte)) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
                value = (Byte) nonNull;
            }
            break;
            case PostgreSQL: {
                if (!(nonNull instanceof Short)) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
                value = ((Short) nonNull).byteValue();
            }
            break;
            default:
                throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }


}
