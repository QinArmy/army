package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.util._TimeUtils;

import java.time.Duration;
import java.time.LocalTime;

final class DurationType extends _ArmyNoInjectionMapping implements MappingType.SqlDecimalType {

    public static DurationType from(final Class<?> javaType) {
        if (javaType == Duration.class) {
            throw errorJavaType(DurationType.class, javaType);
        }
        return INSTANCE;
    }

    public static final DurationType INSTANCE = new DurationType();

    private DurationType() {
    }

    @Override
    public Class<?> javaType() {
        return Duration.class;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.TIME;
                break;
            case PostgreSQL:
                dataType = PostgreType.INTERVAL;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Duration convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Duration afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return null;
    }


    private Duration toDuration(DataType dataType, final Object source, ErrorHandler errorHandler) {
        final Duration value;
        if (source instanceof Duration) {
            value = (Duration) source;
        } else if (source instanceof LocalTime) {
            if (dataType != MySQLType.TIME) {
                throw errorHandler.apply(this, dataType, source, null);
            }
            value = _TimeUtils.convertToDuration((LocalTime) source);
        } else if (source instanceof String) {

        }
        return null;
    }


}
