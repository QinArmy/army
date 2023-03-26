package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.*;

public final class PrimitiveByteArrayType extends AbstractMappingType {

    public static final PrimitiveByteArrayType INSTANCE = new PrimitiveByteArrayType();

    public static PrimitiveByteArrayType from(final Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(PrimitiveByteArrayType.class, fieldType);
        }
        return INSTANCE;
    }

    private PrimitiveByteArrayType() {
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
                sqlType = MySQLTypes.VARBINARY;
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
    public byte[] beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw outRangeOfSqlType(type, nonNull);
        }
        return (byte[]) nonNull;
    }

    @Override
    public byte[] afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        final byte[] value;
        switch (type.database()) {
            case MySQL:
            case H2: {
                if (!(nonNull instanceof byte[])) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
                value = (byte[]) nonNull;
            }
            break;
            case PostgreSQL://TODO
            case Oracle://TODO
            default:
                throw errorJavaTypeForSqlType(type, nonNull);
        }
        return value;
    }
}
