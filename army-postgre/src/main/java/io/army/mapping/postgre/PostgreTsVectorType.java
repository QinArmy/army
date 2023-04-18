package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;


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
