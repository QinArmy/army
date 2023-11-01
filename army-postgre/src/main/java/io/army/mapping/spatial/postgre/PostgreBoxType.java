package io.army.mapping.spatial.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SQLType;

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
    public SQLType map(ServerMeta meta) throws NotSupportDialectException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SQLType type, MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SQLType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }
}
