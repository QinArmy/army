package io.army.mapping.optional;

import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.util.Map;

public final class JsonMapType extends AbstractMappingType {


    public static JsonMapType from(MappingType keyType, MappingType valueType) {
        throw new UnsupportedOperationException();
    }

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
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }


}
