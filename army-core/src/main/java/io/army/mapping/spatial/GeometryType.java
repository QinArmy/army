package io.army.mapping.spatial;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

/**
 * @see <a href="https://www.ogc.org/standards/sfa">Simple Feature Access - Part 1: Common Architecture PDF</a>
 */
public final class GeometryType extends ArmyGeometryType implements MappingType.SqlGeometryType {

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
    public Object beforeBind(DataType type, MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
