package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonbType extends _ArmyBuildInMapping implements MappingType.SqlJsonbType {

    public static JsonbType from(final Class<?> javaType) {
        final JsonbType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, JsonbType::new);
        }
        return instance;
    }

    public static final JsonbType TEXT = new JsonbType(String.class);

    private static final ConcurrentMap<Class<?>, JsonbType> INSTANCE_MAP = new ConcurrentHashMap<>();

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private JsonbType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                sqlDataType = PostgreType.JSONB;
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
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(DataType type, MappingEnv env, Object source) {
        if (source instanceof String) {
            return (String) source;
        }
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String afterGet(DataType type, MappingEnv env, Object source) {
        if (!(source instanceof String)) {
            throw errorJavaTypeForSqlType(type, source);
        }
        return (String) source;
    }


}
