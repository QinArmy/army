package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

public final class PointType extends AbstractMappingType {

    public static final PointType INSTANCE = new PointType();

    private PointType() {
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
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
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
