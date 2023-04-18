package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public final class JsonType extends _ArmyInnerMapping implements MappingType.SqlJsonType {

    public static final JsonType TEXT_INSTANCE = new JsonType(String.class);

    private static final ConcurrentMap<Class<?>, JsonType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static JsonType from(final Class<?> javaType) {
        final JsonType instance;
        if (javaType == String.class) {
            instance = TEXT_INSTANCE;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, JsonType::new);
        }
        return instance;
    }

    private final Class<?> javaType;

    private JsonType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
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
