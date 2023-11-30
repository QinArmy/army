package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;

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
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.UUID;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public UUID convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID beforeBind(DataType dataType, MappingEnv env, Object nonNull) throws CriteriaException {
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
    public UUID afterGet(DataType dataType, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
