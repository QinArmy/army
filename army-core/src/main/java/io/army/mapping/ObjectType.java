package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

import java.util.Objects;

public final class ObjectType extends _ArmyBuildInMapping {

    public static ObjectType from(final Class<?> javaType) {
        Objects.requireNonNull(javaType);
        return INSTANCE;
    }

    public static final ObjectType INSTANCE = new ObjectType();

    private ObjectType() {
    }

    @Override
    public Class<?> javaType() {
        return Object.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        return TextType.mapToDataType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        throw errorUseCase();
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        throw errorUseCase();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        // TODO convert
        return source;
    }

    private static CriteriaException errorUseCase() {
        String m = String.format("%s only can use read column from database", ObjectType.class.getName());
        return new CriteriaException(m);
    }


}
