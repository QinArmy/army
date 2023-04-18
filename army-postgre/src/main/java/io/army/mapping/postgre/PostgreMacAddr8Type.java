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
 * This class representing Postgre macaddr8 type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/datatype-net-types.html#DATATYPE-MACADDR8">macaddr8</a>
 */
public final class PostgreMacAddr8Type extends _ArmyNoInjectionMapping {


    public static final PostgreMacAddr8Type INSTANCE = new PostgreMacAddr8Type();

    public static PostgreMacAddr8Type from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgreMacAddr8Type.class, javaType);
        }
        return INSTANCE;
    }


    private PostgreMacAddr8Type() {
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
