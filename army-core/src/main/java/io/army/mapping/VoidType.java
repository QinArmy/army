package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

public final class VoidType extends _ArmyInnerMapping {

    public static final VoidType INSTANCE = new VoidType();


    private VoidType() {
    }


    @Override
    public Class<?> javaType() {
        return Void.class;
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
