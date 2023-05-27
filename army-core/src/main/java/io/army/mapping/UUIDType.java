package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;

import java.util.UUID;

public final class UUIDType extends _ArmyNoInjectionMapping {

    public static final UUIDType INSTANCE = new UUIDType();

    public static UUIDType from(final Class<?> javaType) {
        if (javaType != UUID.class) {
            throw errorJavaType(UUIDType.class, javaType);
        }
        return INSTANCE;
    }


    private UUIDType() {

    }

    @Override
    public Class<?> javaType() {
        return UUID.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
