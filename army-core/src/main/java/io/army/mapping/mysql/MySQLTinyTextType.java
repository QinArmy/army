package io.army.mapping.mysql;

import io.army.dialect.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.mapping.StringType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-type-syntax.html">String Data Type Syntax::TINYTEXT</a>
 */
public final class MySQLTinyTextType extends AbstractMappingType {

    public static final MySQLTinyTextType INSTANCE = new MySQLTinyTextType();

    public static final int MAX_LENGTH = 0x7F;

    public static MySQLTinyTextType from(Class<?> fieldType) {
        if (fieldType != String.class) {
            throw errorJavaType(MySQLTinyTextType.class, fieldType);
        }
        return INSTANCE;
    }

    private MySQLTinyTextType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySQLTypes.TINYTEXT;
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        final String value;
        value = StringType.beforeBind(type, nonNull);
        if (value.length() > MAX_LENGTH) {
            throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public String afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        final String value = (String) nonNull;
        if (value.length() > MAX_LENGTH) {
            throw errorValueForSqlType(type, nonNull, valueOutOfMapping(nonNull, MySQLTinyTextType.class));
        }
        return value;
    }


}
