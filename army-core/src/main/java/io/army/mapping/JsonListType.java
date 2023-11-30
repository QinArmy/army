package io.army.mapping;


import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.util.List;

@Deprecated
public final class JsonListType extends MappingType {

    public static JsonListType from(MappingType elementType) {
        throw new UnsupportedOperationException();
    }

    /**
     * private constructor
     */
    private JsonListType() {
    }

    @Override
    public Class<?> javaType() {
        return List.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
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
    public Object beforeBind(DataType dataType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
