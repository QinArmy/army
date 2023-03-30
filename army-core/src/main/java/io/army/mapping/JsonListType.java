package io.army.mapping;


import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.util.List;

public final class JsonListType extends AbstractMappingType {

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
