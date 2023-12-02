package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;


public final class JsonType extends _ArmyBuildInMapping implements MappingType.SqlJsonType {

    public static JsonType from(final Class<?> javaType) {
        final JsonType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, JsonType::new);
        }
        return instance;
    }

    public static final JsonType TEXT = new JsonType(String.class);

    private static final ConcurrentMap<Class<?>, JsonType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private JsonType(Class<?> javaType) {
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
            case MySQL:
                sqlDataType = MySQLType.JSON;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.JSON;
                break;
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
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        if (source instanceof String) {
            return (String) source;
        }
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) {

        return (String) source;
    }


}
