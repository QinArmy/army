package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.*;

public final class ByteArrayType extends AbstractMappingType {

    public static final ByteArrayType INSTANCE = new ByteArrayType();

    public static ByteArrayType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(ByteArrayType.class, fieldType);
        }
        return INSTANCE;
    }

    private ByteArrayType() {
    }

    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.VARBINARY;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BYTEA;
                break;
            case Oracle:
                sqlType = OracleDataType.BLOB;
                break;
            case H2:
                sqlType = H2DataType.VARBINARY;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }

    @Override
    public byte[] beforeBind(SqlType sqlType, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(SqlType sqlType, MappingEnv env, final Object nonNull) {
        final byte[] value;
        switch (sqlType.database()) {
            case MySQL:
            case H2: {
                if (!(nonNull instanceof byte[])) {
                    throw errorJavaTypeForSqlType(sqlType, nonNull);
                }
                value = (byte[]) nonNull;
            }
            break;
            case PostgreSQL://TODO
            case Oracle://TODO
            default:
                throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }
}
