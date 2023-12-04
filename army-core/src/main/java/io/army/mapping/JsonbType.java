package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public final class JsonbType extends _ArmyBuildInMapping implements MappingType.SqlJsonbType {

    public static JsonbType from(final Class<?> javaType) {
        final JsonbType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, CONSTRUCTOR);
        }
        return instance;
    }

    public static final JsonbType TEXT = new JsonbType(String.class);

    private static final ConcurrentMap<Class<?>, JsonbType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private static final Function<Class<?>, JsonbType> CONSTRUCTOR = JsonbType::new;

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
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.JSONB;
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return decodeJson(map(env.serverMeta()), env, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        final String value;
        if (source instanceof String) {
            value = (String) source;
        } else if (this.javaType.isInstance(source)) {
            try {
                value = env.jsonCodec().encode(source);
            } catch (Exception e) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, e);
            }
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) {
        return decodeJson(dataType, env, source, ACCESS_ERROR_HANDLER);
    }


    private Object decodeJson(DataType dataType, MappingEnv env, final Object source, ErrorHandler errorHandler) {
        if (!(source instanceof String)) {
            throw errorHandler.apply(this, dataType, source, null);
        }
        return env.jsonCodec().decode((String) source, this.javaType);
    }


}
