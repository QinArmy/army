package io.army.mapping.postgre.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.mapping.array.PostgreArrays;
import io.army.mapping.postgre.PostgreAclItemType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;


/**
 * <p>
 * This class representing Postgre aclitem array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/ddl-priv.html">Privileges</a>
 */
public final class PostgreAclItemArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {


    public static PostgreAclItemArrayType from(final Class<?> javaType) {
        if (javaType != String[].class) {
            throw errorJavaType(PostgreAclItemArrayType.class, javaType);
        }
        return LINEAR;
    }


    public static final PostgreAclItemArrayType LINEAR = new PostgreAclItemArrayType();


    private PostgreAclItemArrayType() {
    }

    @Override
    public Class<?> javaType() {
        return String[].class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.ACLITEM_ARRAY;
    }

    @Override
    public MappingType elementType() {
        return PostgreAclItemType.TEXT;
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        throw noMatchCompatibleMapping(this, targetType);
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof String[])) {
            throw PARAM_ERROR_HANDLER.apply(this, PostgreType.ACLITEM_ARRAY, nonNull, null);
        }
        return nonNull;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof String[])) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        return nonNull;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        if (!(nonNull instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        final Object array;

        try {
            array = PostgreArrays.parseArray((String) nonNull, false, String::substring, _Constant.COMMA, type, this,
                    ACCESS_ERROR_HANDLER);
            assert array instanceof String[];
            return array;
        } catch (Throwable e) {
            throw ACCESS_ERROR_HANDLER.apply(this, type, nonNull, e);
        }
    }


}
