package io.army.mapping.spatial.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;


/**
 * <p>
 * This class representing Postgre point type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/datatype-geometric.html#DATATYPE-GEO-TABLE">point</a>
 */
public final class PostgrePointType extends PostgreGeometricType implements MappingType.SqlPointType {


    public static final PostgrePointType INSTANCE = new PostgrePointType();

    public static PostgrePointType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgrePointType.class, javaType);
        }
        return INSTANCE;
    }


    private PostgrePointType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
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