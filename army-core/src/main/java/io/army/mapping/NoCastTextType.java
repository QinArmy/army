package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PgSqlType;
import io.army.sqltype.SqlType;

public final class NoCastTextType extends _ArmyInnerMapping implements MappingType.SqlTextType {


    public static final NoCastTextType INSTANCE = new NoCastTextType();

    public static NoCastTextType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(NoCastTextType.class, javaType);
        }
        return INSTANCE;
    }

    private NoCastTextType() {
    }


    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.TEXT;
                break;
            case PostgreSQL:
                type = PgSqlType.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public String convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType._convertToString(this, this.map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType._convertToString(this, type, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return StringType._convertToString(this, type, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
