package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.util.Map;

@Deprecated
public final class JsonMapType extends MappingType {


    public static JsonMapType from(MappingType keyType, MappingType valueType) {
        throw new UnsupportedOperationException();
    }

    /**
     * private constructor
     */
    private JsonMapType() {
    }

    @Override
    public Class<?> javaType() {
        return Map.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }


}
