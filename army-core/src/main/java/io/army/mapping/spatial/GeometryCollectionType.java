package io.army.mapping.spatial;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

public final class GeometryCollectionType extends _ArmyBuildInMapping implements MappingType.SqlGeometryCollectionType {


    public static final GeometryCollectionType TEXT = new GeometryCollectionType(String.class);

    public static final GeometryCollectionType BINARY = new GeometryCollectionType(byte[].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private GeometryCollectionType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
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
