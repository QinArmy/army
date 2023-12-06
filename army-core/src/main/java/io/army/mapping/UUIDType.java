package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.util.UUID;

public final class UUIDType extends _ArmyNoInjectionMapping {


    public static UUIDType from(final Class<?> javaType) {
        if (javaType != UUID.class) {
            throw errorJavaType(UUIDType.class, javaType);
        }
        return INSTANCE;
    }

    public static final UUIDType INSTANCE = new UUIDType();

    /**
     * private constructor
     */
    private UUIDType() {

    }

    @Override
    public Class<?> javaType() {
        return UUID.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.UUID;
                break;
            case MySQL:
                dataType = MySQLType.CHAR;
                break;
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return toUUID(map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        final Object value;
        switch (((SqlType) dataType).database()) {
            case PostgreSQL:
                value = toUUID(dataType, source, PARAM_ERROR_HANDLER);
                break;
            case MySQL: {
                if (source instanceof UUID) {
                    value = source.toString();
                } else if (source instanceof String) {
                    try {
                        UUID.fromString((String) source);
                    } catch (Exception e) {
                        throw PARAM_ERROR_HANDLER.apply(this, dataType, source, e);
                    }
                    value = source;
                } else {
                    throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
                }
            }
            break;
            case SQLite:
            default:
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return toUUID(dataType, source, ACCESS_ERROR_HANDLER);
    }


    private UUID toUUID(final DataType dataType, final Object source, final ErrorHandler errorHandler) {
        final UUID value;
        if (source instanceof UUID) {
            value = (UUID) source;
        } else if (source instanceof String) {
            try {
                value = UUID.fromString((String) source);
            } catch (Exception e) {
                throw errorHandler.apply(this, dataType, source, e);
            }
        } else {
            throw errorHandler.apply(this, dataType, source, null);
        }
        return value;
    }


}
