package io.army.mapping.mysql;

import io.army.dialect.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;

public final class MySQLMediumBlobType extends AbstractMappingType {

    public static final MySQLMediumBlobType INSTANCE = new MySQLMediumBlobType();

    public static final int MAX_LENGTH = 0x7FFF_FF;

    public static MySQLMediumBlobType from(Class<?> fieldType) {
        if (fieldType != byte[].class) {
            throw errorJavaType(MySQLMediumBlobType.class, fieldType);
        }
        return INSTANCE;
    }

    private MySQLMediumBlobType() {
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
        return MySQLTypes.MEDIUMBLOB;
    }

    @Override
    public byte[] beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
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
    public byte[] afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof byte[])) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final byte[] value = (byte[]) nonNull;
        if (value.length > MAX_LENGTH) {
            throw errorValueForSqlType(sqlType, nonNull, valueOutOfMapping(nonNull, MySQLMediumBlobType.class));
        }
        return value;
    }


}
