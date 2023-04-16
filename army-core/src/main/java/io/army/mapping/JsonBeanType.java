package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

public final class JsonBeanType extends MappingType {

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
        throw new UnsupportedOperationException();
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
