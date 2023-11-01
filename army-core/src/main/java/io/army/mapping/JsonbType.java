package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonbType extends _ArmyBuildInMapping implements MappingType.SqlJsonbType {


    public static final JsonbType TEXT = new JsonbType(String.class);

    private static final ConcurrentMap<Class<?>, JsonbType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static JsonbType from(final Class<?> javaType) {
        final JsonbType instance;
        if (javaType == String.class) {
            instance = TEXT;
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
    public SQLType map(final ServerMeta meta) {
        final SQLType sqlDataType;
        switch (meta.dialectDatabase()) {
            case PostgreSQL:
                sqlDataType = PostgreSqlType.JSONB;
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return sqlDataType;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(SQLType type, MappingEnv env, Object nonNull) {
        if (nonNull instanceof String) {
            return (String) nonNull;
        }
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String afterGet(SQLType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (String) nonNull;
    }
}
