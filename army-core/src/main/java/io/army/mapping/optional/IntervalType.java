package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

public final class IntervalType extends _ArmyNoInjectionMapping implements MappingType.SqlIntervalType {


    public static IntervalType from(Class<?> javaType) {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static final IntervalType TEXT = new IntervalType();

    private IntervalType() {
    }

    @Override
    public Class<?> javaType() {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType type, MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
