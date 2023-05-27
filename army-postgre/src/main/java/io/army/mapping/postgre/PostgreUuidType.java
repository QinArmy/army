package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.util.UUID;

public final class PostgreUuidType extends _ArmyNoInjectionMapping {

    public static final PostgreUuidType INSTANCE = new PostgreUuidType();

    public static PostgreUuidType from(Class<?> javaType) {
        if (javaType != UUID.class) {
            throw errorJavaType(PostgreUuidType.class, javaType);
        }
        return INSTANCE;
    }


    private PostgreUuidType() {
    }

    @Override
    public Class<?> javaType() {
        return UUID.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.UUID;
    }

    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public UUID convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        final UUID value;
        if (nonNull instanceof UUID) {
            value = (UUID) nonNull;
        } else if (nonNull instanceof String) {

        } else {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
