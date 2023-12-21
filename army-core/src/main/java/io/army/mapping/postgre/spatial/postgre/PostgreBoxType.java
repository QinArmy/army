package io.army.mapping.postgre.spatial.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

public final class PostgreBoxType extends PostgreGeometricType {

    public static final PostgreBoxType INSTANCE = new PostgreBoxType();

    public static PostgreBoxType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgreBoxType.class, javaType);
        }
        return INSTANCE;
    }


    private PostgreBoxType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
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
