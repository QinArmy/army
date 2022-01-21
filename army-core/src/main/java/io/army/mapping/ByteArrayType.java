package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.*;

public final class ByteArrayType extends AbstractMappingType {

    public static final ByteArrayType INSTANCE = new ByteArrayType();

    public static ByteArrayType create(Class<?> javaType) {
        if (javaType != byte[].class) {
            throw errorJavaType(ByteArrayType.class, javaType);
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
    public SqlType map(ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.database()) {
            case MySQL:
                sqlDataType = MySqlType.VARBINARY;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.BYTEA;
                break;
            case Oracle:
                sqlDataType = OracleDataType.BLOB;
                break;
            case H2:
                sqlDataType = H2DataType.VARBINARY;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlDataType;
    }

    @Override
    public byte[] beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
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
