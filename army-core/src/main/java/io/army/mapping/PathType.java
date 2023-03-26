package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.nio.file.Path;

public final class PathType extends AbstractMappingType {

    public static final PathType INSTANCE = new PathType();

    private PathType() {
    }

    @Override
    public Class<?> javaType() {
        return Path.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
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
