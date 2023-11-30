package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;


/**
 * <p>
 * This class representing Postgre tsvector type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/datatype-textsearch.html#DATATYPE-TSVECTOR">tsvector</a>
 */
public final class PostgreTsVectorType extends _ArmyNoInjectionMapping {


    public static final PostgreTsVectorType INSTANCE = new PostgreTsVectorType();

    public static PostgreTsVectorType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgreTsVectorType.class, javaType);
        }
        return INSTANCE;
    }


    private PostgreTsVectorType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.TSVECTOR;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
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
        if (source instanceof String) {
            return source;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }

}
