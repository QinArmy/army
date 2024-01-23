package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

/**
 * <p>Just for {@link io.army.criteria.impl.SQLs#NULL}
 */
public final class NullType extends _ArmyNoInjectionMapping {


    public static NullType INSTANCE = new NullType();


    /**
     * private constructor
     */
    private NullType() {
    }

    @Override
    public Class<?> javaType() {
        return Object.class;
    }


    @Override
    public boolean isSameType(MappingType type) {
        return type instanceof NullType;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.NULL;
                break;
            case PostgreSQL:
                type = PostgreType.UNKNOWN;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        throw new UnsupportedOperationException("bug,never here");
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        throw new UnsupportedOperationException("bug,never here");
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        throw new UnsupportedOperationException("bug,never here");
    }


}
