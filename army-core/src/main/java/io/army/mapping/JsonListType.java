package io.army.mapping;


import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.util.List;

public final class JsonListType extends MappingType {

    public static JsonListType from(MappingType elementType) {
        throw new UnsupportedOperationException();
    }

    private JsonListType() {
    }

    @Override
    public Class<?> javaType() {
        return List.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }


    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
