package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PgSqlType;
import io.army.sqltype.SqlType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonbType extends _ArmyInnerMapping implements MappingType.SqlJsonbType {


    public static final JsonbType TEXT_INSTANCE = new JsonbType(String.class);

    private static final ConcurrentMap<Class<?>, JsonbType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static JsonbType from(final Class<?> javaType) {
        final JsonbType instance;
        if (javaType == String.class) {
            instance = TEXT_INSTANCE;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, JsonbType::new);
        }
        return instance;
    }

    private final Class<?> javaType;

    private JsonbType(Class<?> javaType) {
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
                sqlDataType = MySQLType.JSON;
                break;
            case PostgreSQL:
                sqlDataType = PgSqlType.JSON;
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
