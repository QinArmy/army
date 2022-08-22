package io.army.mapping.optional;

import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

public final class MultiPointType extends AbstractMappingType {


    private MultiPointType() {
    }

    @Override
    public Class<?> javaType() {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public SqlType map(ServerMeta meta) {
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
