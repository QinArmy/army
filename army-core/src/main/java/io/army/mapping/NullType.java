package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.SQLs;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLType;

/**
 * <p>Just for {@link SQLs#NULL}
 */
public final class NullType extends MappingType {


    public final static NullType INSTANCE = new NullType();


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
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return super.arrayTypeOfThis();
    }

    @Override
    public <Z> MappingType compatibleFor(DataType dataType, Class<Z> targetType) throws NoMatchMappingException {
        // always return this
        return this;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SQLType type;
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
        throw new CriteriaException("SQL key word NULL don't support convert method");
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        throw new CriteriaException("SQL key word NULL don't support beforeBind method");
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        throw new UnsupportedOperationException("bug,never here");
    }


} // NullType
