package io.army.mapping.spatial.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SQLType;


/**
 * <p>
 * This class representing Postgre polygon type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/datatype-geometric.html#DATATYPE-GEO-TABLE">polygon</a>
 */
public final class PostgrePolygonType extends PostgreGeometricType {


    public static final PostgrePolygonType INSTANCE = new PostgrePolygonType();

    public static PostgrePolygonType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgrePolygonType.class, javaType);
        }
        return INSTANCE;
    }


    private PostgrePolygonType() {
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
