package io.army.mapping.mysql;

import io.army.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.mapping.StringType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-type-syntax.html">String Data Type Syntax::MEDIUMTEXT</a>
 */
public final class MySQLMediumTextType extends AbstractMappingType {

    public static final MySQLMediumTextType INSTANCE = new MySQLMediumTextType();

    public static final int MAX_LENGTH = 0x7FFF_FF;

    public static MySQLMediumTextType from(Class<?> fieldType) {
        if (fieldType != String.class) {
            throw errorJavaType(MySQLMediumTextType.class, fieldType);
        }
        return INSTANCE;
    }

    private MySQLMediumTextType() {
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
        return MySqlType.MEDIUMTEXT;
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        final String value;
        value = StringType.beforeBind(sqlType, nonNull);
        if (value.length() > MAX_LENGTH) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }

    @Override
    public String afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final String value = (String) nonNull;
        if (value.length() > MAX_LENGTH) {
            throw errorValueForSqlType(sqlType, nonNull, valueOutOfMapping(nonNull, MySQLMediumTextType.class));
        }
        return value;
    }


}
