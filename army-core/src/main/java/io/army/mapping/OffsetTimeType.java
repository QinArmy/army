package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.OffsetTimeArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

/**
 * <p>
 * This class is mapping class of {@link OffsetTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link java.time.OffsetTime}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link OffsetTime},if error,throw {@link io.army.ArmyException}
 *
 * @since 0.6.0
 */
public final class OffsetTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlOffsetTimeType {

    public static OffsetTimeType from(Class<?> javaType) {
        if (javaType != OffsetTime.class) {
            throw errorJavaType(OffsetTimeType.class, javaType);
        }
        return INSTANCE;
    }

    public static final OffsetTimeType INSTANCE = new OffsetTimeType();


    /**
     * private constructor
     */
    private OffsetTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetTime.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return OffsetTimeArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.TIME;
                break;
            case PostgreSQL:
                type = PostgreType.TIMETZ;
                break;
            case Oracle:
                type = OracleDataType.TIMESTAMPTZ;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }

    @Override
    public OffsetTime convert(MappingEnv env, Object source) throws CriteriaException {
        return toOffsetTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public OffsetTime beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        return toOffsetTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public OffsetTime afterGet(DataType dataType, final MappingEnv env, final Object source) {
        return toOffsetTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static OffsetTime toOffsetTime(MappingType type, DataType dataType, final Object source,
                                   ErrorHandler errorHandler) {
        final OffsetTime value;
        if (source instanceof OffsetTime) {
            value = (OffsetTime) source;
        } else if (source instanceof OffsetDateTime) {
            value = ((OffsetDateTime) source).toOffsetTime();
        } else if (source instanceof ZonedDateTime) {
            value = ((ZonedDateTime) source).toOffsetDateTime().toOffsetTime();
        } else if (source instanceof String) {
            try {
                value = OffsetTime.parse((String) source, _TimeUtils.OFFSET_TIME_FORMATTER_6);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }

}
