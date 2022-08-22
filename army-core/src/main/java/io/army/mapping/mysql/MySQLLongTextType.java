package io.army.mapping.mysql;

import io.army.dialect.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;

import java.io.Reader;

public final class MySQLLongTextType extends AbstractMappingType {

    public static final MySQLLongTextType STRING_INSTANCE = new MySQLLongTextType(String.class);

    public static final MySQLLongTextType READER_INSTANCE = new MySQLLongTextType(Reader.class);

    public static final long MAX_LENGTH = 0xFFFF_FFFFL;

    public static MySQLLongTextType from(final Class<?> fieldType) {
        final MySQLLongTextType instance;
        if (fieldType == String.class) {
            instance = STRING_INSTANCE;
        } else if (fieldType == Reader.class) {
            instance = READER_INSTANCE;
        } else {
            throw errorJavaType(MySQLLongTextType.class, fieldType);
        }
        return instance;
    }


    private final Class<?> javaType;

    private MySQLLongTextType(Class<?> javaType) {
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
        return MySQLTypes.LONGTEXT;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String || nonNull instanceof Reader)) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return nonNull;
    }

    @Override
    public String afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (String) nonNull;
    }


}
