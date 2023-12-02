package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class JsonPathType extends _ArmyBuildInMapping implements MappingType.SqlJsonPathType {

    public static final JsonPathType INSTANCE = new JsonPathType();

    public static JsonPathType from(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(JsonPathType.class, javaType);
        }
        return INSTANCE;
    }

    private JsonPathType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                type = PostgreType.JSONPATH;
                break;
            case MySQL: //TODO
            case Oracle:
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
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
    public Object beforeBind(DataType type, MappingEnv env, Object source) throws CriteriaException {
        if (source instanceof String) {
            return source;
        }
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
