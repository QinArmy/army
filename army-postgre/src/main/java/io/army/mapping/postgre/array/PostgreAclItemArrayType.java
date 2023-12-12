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
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;


/**
 * <p>
 * This class representing Postgre aclitem array type {@link MappingType}
 * * @see <a href="https://www.postgresql.org/docs/current/ddl-priv.html">Privileges</a>
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
    public Class<?> underlyingJavaType() {
        return null;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
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
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        throw noMatchCompatibleMapping(this, targetType);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof String[])) {
            throw PARAM_ERROR_HANDLER.apply(this, PostgreType.ACLITEM_ARRAY, source, null);
        }
        return source;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof String[])) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return source;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        final Object array;

        try {
            array = PostgreArrays.parseArray((String) source, false, String::substring, _Constant.COMMA, dataType, this,
                    ACCESS_ERROR_HANDLER);
            assert array instanceof String[];
            return array;
        } catch (Throwable e) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, e);
        }
    }


}
