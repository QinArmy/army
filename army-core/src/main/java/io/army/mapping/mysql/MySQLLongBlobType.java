package io.army.mapping.mysql;

import io.army.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

import java.nio.file.Path;

public final class MySQLLongBlobType extends AbstractMappingType {

    public static final MySQLLongBlobType BYTE_ARRAY_INSTANCE = new MySQLLongBlobType(byte[].class);

    public static final MySQLLongBlobType PATH_INSTANCE = new MySQLLongBlobType(Path.class);

    public static final long MAX_LENGTH = 0xFFFF_FFFFL;

    public static MySQLLongBlobType create(final Class<?> javaType) {
        final MySQLLongBlobType instance;
        if (javaType == byte[].class) {
            instance = BYTE_ARRAY_INSTANCE;
        } else if (javaType == Path.class) {
            instance = PATH_INSTANCE;
        } else {
            throw errorJavaType(MySQLLongBlobType.class, javaType);
        }
        return instance;
    }

    private final Class<?> javaType;

    private MySQLLongBlobType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySqlType.LONGBLOB;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return null;
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return null;
    }


}
