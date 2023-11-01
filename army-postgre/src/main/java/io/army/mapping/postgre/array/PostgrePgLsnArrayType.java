package io.army.mapping.postgre.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SQLType;


/**
 * <p>
 * This class representing Postgre pg_lsn array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/datatype-pg-lsn.html">pg_lsn</a>
 */
public final class PostgrePgLsnArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    private PostgrePgLsnArrayType() {
    }

    @Override
    public Class<?> javaType() {
        return null;
    }

    @Override
    public SQLType map(ServerMeta meta) throws NotSupportDialectException {
        return null;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(SQLType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(SQLType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }

    @Override
    public MappingType elementType() {
        return null;
    }


}
