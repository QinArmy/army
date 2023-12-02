package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
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
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType type, MappingEnv env, Object source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) {
        throw new UnsupportedOperationException();
    }


}
