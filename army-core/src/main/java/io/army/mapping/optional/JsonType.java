package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;

@Deprecated
public final class JsonType extends MappingType {


    public static final JsonType INSTANCE = new JsonType();

    public static JsonType from(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(JsonType.class, javaType);
        }
        return INSTANCE;
    }

    private JsonType() {
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
                sqlDataType = MySQLTypes.JSON;
                break;
            case PostgreSQL:
                sqlDataType = PostgreTypes.JSON;
                break;
            case Oracle:

            case H2:
            default:
                throw noMappingError(meta);

        }
        return sqlDataType;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return StringType.beforeBind(type, nonNull);
    }

    @Override
    public String afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (String) nonNull;
    }


}
