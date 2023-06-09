package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.mapping.postgre.array.PostgreAclItemArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;


/**
 * <p>
 * This class representing Postgre aclitem type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/ddl-priv.html">Privileges</a>
 */
public final class PostgreAclItemType extends _ArmyBuildInMapping {

    public static PostgreAclItemType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgreAclItemType.class, javaType);
        }
        return TEXT;
    }


    public static final PostgreAclItemType TEXT = new PostgreAclItemType();


    private PostgreAclItemType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.Postgre) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreSqlType.ACLITEM;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return PostgreAclItemArrayType.LINEAR;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        throw noMatchCompatibleMapping(this, targetType);
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof String)) {
            throw PARAM_ERROR_HANDLER.apply(this, PostgreSqlType.ACLITEM, nonNull, null);
        }
        return nonNull;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof String)) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        return nonNull;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        if (!(nonNull instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        return nonNull;
    }


}
