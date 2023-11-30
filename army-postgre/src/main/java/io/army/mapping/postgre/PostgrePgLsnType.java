package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;


/**
 * <p>
 * This class representing Postgre pg_lsn type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/datatype-pg-lsn.html">pg_lsn</a>
 */
public final class PostgrePgLsnType extends _ArmyNoInjectionMapping {


    public static final PostgrePgLsnType TEXT = new PostgrePgLsnType(String.class);
    public static final PostgrePgLsnType LONG = new PostgrePgLsnType(Long.class);


    private final Class<?> javaType;


    private PostgrePgLsnType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(ServerMeta meta) throws UnsupportedDialectException {
        return null;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return super.arrayTypeOfThis();
    }

    @Override
    public boolean isSameType(MappingType type) {
        return super.isSameType(type);
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }


}
