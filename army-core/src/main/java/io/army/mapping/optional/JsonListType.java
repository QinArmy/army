package io.army.mapping.optional;


import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.util.List;

public final class JsonListType extends AbstractMappingType {

    public static JsonListType from(MappingType elementType) {
        throw new UnsupportedOperationException();
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
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
