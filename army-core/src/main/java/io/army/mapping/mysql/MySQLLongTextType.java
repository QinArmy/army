package io.army.mapping.mysql;

import io.army.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.mapping.StringType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

public final class MySQLLongTextType extends AbstractMappingType {

    public static final MySQLLongTextType STRING_INSTANCE = new MySQLLongTextType(String.class);

    public static final long MAX_LENGTH = 0xFFFF_FFFFL;

    public static MySQLLongTextType create(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(MySQLLongTextType.class, javaType);
        }
        return STRING_INSTANCE;
    }


    private final Class<?> javaType;

//    private final Class<?> elementType;
//
//    private final Charset charset;

    private MySQLLongTextType(Class<?> javaType) {
        this.javaType = javaType;
//        this.elementType = elementType;
//        this.charset = charset;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySqlType.LONGTEXT;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return StringType.beforeBind(sqlType, nonNull);
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return StringType.INSTANCE.afterGet(sqlType, env, nonNull);
    }
//
//    private static final class PathUTF8Holder {
//
//        private static final MySQLLongTextType INSTANCE = new MySQLLongTextType(Path.class, void.class, StandardCharsets.UTF_8);
//
//    }
//
//    private static final class PathInstanceHolder {
//
//        private static final ConcurrentMap<Charset, MySQLLongTextType> INSTANCE_MAP = new ConcurrentHashMap<>();
//
//    }


}
