package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

import java.sql.JDBCType;

public final class ByteArrayType extends AbstractMappingType {

    public static final ByteArrayType INSTANCE = new ByteArrayType();

    public static ByteArrayType build(Class<?> javaType) {
        if (javaType != byte[].class) {
            throw createNotSupportJavaTypeException(ByteArrayType.class, javaType);
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
    public JDBCType jdbcType() {
        return JDBCType.VARBINARY;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.VARBINARY;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.BYTEA;
                break;
            case Oracle:
                sqlDataType = OracleDataType.BLOB;
                break;
            case H2:
                sqlDataType = H2DataType.VARBINARY;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final byte[] value;
        if (nonNull instanceof byte[]) {
            value = (byte[]) nonNull;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, final Object nonNull) {
        final byte[] value;
        switch (sqlDataType.database()) {
            case MySQL:
            case H2: {
                if (!(nonNull instanceof byte[])) {
                    throw notSupportConvertAfterGet(nonNull);
                }
                value = (byte[]) nonNull;
            }
            break;
            case PostgreSQL://TODO
            case Oracle://TODO
            default:
                throw notSupportConvertAfterGet(nonNull);
        }
        return value;
    }
}
