package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

public final class TextEnumArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static TextEnumArrayType from(final Class<?> javaType) {
        throw new UnsupportedOperationException();
    }


    /**
     * private constructor
     */
    private TextEnumArrayType() {
    }

    @Override
    public Class<?> javaType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> underlyingJavaType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MappingType elementType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        throw new UnsupportedOperationException();
    }


}
