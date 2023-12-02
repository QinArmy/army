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
public final class PointType extends ArmyGeometryType implements MappingType.SqlPointType {

    public static final PointType TEXT_INSTANCE = new PointType(String.class);

    public static final PointType BINARY_INSTANCE = new PointType(byte[].class);

    public static PointType from(final Class<?> javaType) {
        final PointType instance;
        if (javaType == String.class) {
            instance = TEXT_INSTANCE;
        } else if (javaType == byte[].class) {
            instance = BINARY_INSTANCE;
        } else {
            throw errorJavaType(PointType.class, javaType);
        }
        return instance;
    }


    private final Class<?> javaType;

    private PointType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
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
