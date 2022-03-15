package io.army.mapping.mysql;

import io.army.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

public final class MySQLTinyBlobType extends AbstractMappingType {

    public static final MySQLTinyBlobType INSTANCE = new MySQLTinyBlobType();

    public static final int MAX_LENGTH = 0x7F;

    public static MySQLTinyBlobType from(Class<?> javaType) {
        if (javaType != byte[].class) {
            throw errorJavaType(MySQLTinyBlobType.class, javaType);
        }
        return INSTANCE;
    }

    private MySQLTinyBlobType() {
    }

    @Override
    public Class<?> javaType() {
        return byte[].class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySqlType.TINYBLOB;
    }

    @Override
    public byte[] beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        final byte[] value = (byte[]) nonNull;
        if (value.length > MAX_LENGTH) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public byte[] afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final byte[] value = (byte[]) nonNull;
        if (value.length > MAX_LENGTH) {
            throw errorValueForSqlType(sqlType, nonNull, valueOutOfMapping(nonNull, MySQLTinyBlobType.class));
        }
        return value;
    }


}
