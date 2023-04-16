package io.army.mapping.spatial;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;

public final class GeometryType extends ArmyGeometryType implements AbstractMappingType.SqlGeometryType {

    public static final GeometryType TEXT_INSTANCE = new GeometryType(String.class);

    public static final GeometryType BINARY_INSTANCE = new GeometryType(byte[].class);

    public static GeometryType from(final Class<?> javaType) {
        final GeometryType instance;
        if (javaType == String.class) {
            instance = TEXT_INSTANCE;
        } else if (javaType == byte[].class) {
            instance = BINARY_INSTANCE;
        } else {
            throw errorJavaType(GeometryType.class, javaType);
        }
        return instance;
    }


    private final Class<?> javaType;

    private GeometryType(Class<?> javaType) {
        this.javaType = javaType;
    }


    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        //TODO
        throw new UnsupportedOperationException();
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
