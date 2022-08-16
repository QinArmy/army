package io.army.mapping.optional;

import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

public final class JsonBeanType extends AbstractMappingType {

    public static JsonBeanType from(Class<?> javaType) {
        throw new UnsupportedOperationException();
    }

    private JsonBeanType() {
    }

    @Override
    public Class<?> javaType() {
        return null;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        return null;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        return null;
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        return null;
    }


}
