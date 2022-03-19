package io.army.mapping.optional;

import io.army.mapping.AbstractMappingType;
import io.army.mapping.ByteArrayType;
import io.army.mapping.MappingEnvironment;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;


public final class BinaryType extends AbstractMappingType {

    public static final BinaryType INSTANCE = new BinaryType();

    public static BinaryType from(Class<?> javaType) {
        if (javaType != byte[].class) {
            throw errorJavaType(BinaryType.class, javaType);
        }
        return INSTANCE;
    }

    private BinaryType() {
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
                sqlType = MySqlType.BINARY;
                break;
            case PostgreSQL:
                sqlType = PostgreType.BYTEA;
                break;
            case Oracle:
                sqlType = OracleDataType.BLOB;
                break;
            case H2:
                //TODO validate
                sqlType = H2DataType.VARBINARY;
                break;
            default:
                throw noMappingError(meta);

        }
        return sqlType;
    }


    @Override
    public byte[] beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return ByteArrayType.INSTANCE.beforeBind(sqlType, env, nonNull);
    }

    @Override
    public byte[] afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return ByteArrayType.INSTANCE.afterGet(sqlType, env, nonNull);
    }


}