package io.army.mapping.optional;

import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnvironment;
import io.army.mapping.StringType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class JsonStringType extends AbstractMappingType {


    public static final JsonStringType INSTANCE = new JsonStringType();

    public static JsonStringType from(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(JsonStringType.class, javaType);
        }
        return INSTANCE;
    }


    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.database()) {
            case MySQL:
                sqlDataType = MySqlType.JSON;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.JSON;
                break;
            case Oracle:
            case Firebird:
            case H2:
            default:
                throw noMappingError(meta);

        }
        return sqlDataType;
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        return StringType.beforeBind(sqlType, nonNull);
    }

    @Override
    public String afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return (String) nonNull;
    }


}
