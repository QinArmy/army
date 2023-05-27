package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
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
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case PostgreSQL:
                type = PostgreDataType.JSONPATH;
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
        if (nonNull instanceof String) {
            return nonNull;
        }
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
