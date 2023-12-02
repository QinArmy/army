package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

import java.util.UUID;

public final class UUIDType extends _ArmyNoInjectionMapping {


    public static UUIDType from(final Class<?> javaType) {
        if (javaType != UUID.class) {
            throw errorJavaType(UUIDType.class, javaType);
        }
        return INSTANCE;
    }

    public static final UUIDType INSTANCE = new UUIDType();

    /**
     * private constructor
     */
    private UUIDType() {

    }

    @Override
    public Class<?> javaType() {
        return UUID.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
