package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.OffsetDateTimeArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;
import io.army.util._TimeUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * <p>
 * This class is mapping class of {@link OffsetDateTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link OffsetDateTime},if error,throw {@link io.army.ArmyException}
*
 * @since 1.0
 */
public final class OffsetDateTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlOffsetDateTimeType {

    public static OffsetDateTimeType from(Class<?> javaType) {
        if (javaType != OffsetDateTime.class) {
            throw errorJavaType(OffsetDateTimeType.class, javaType);
        }
        return INSTANCE;
    }

    public static final OffsetDateTimeType INSTANCE = new OffsetDateTimeType();

    /**
     * private constructor
     */
    private OffsetDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetDateTime.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToDataType(this, meta);
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return OffsetDateTimeArrayType.LINEAR;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        final MappingType instance;
        if (targetType == String.class) {
            instance = StringType.INSTANCE;
        } else if (targetType == ZonedDateTime.class) {
            instance = ZonedDateTimeType.INSTANCE;
        } else {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return instance;
    }

    @Override
    public OffsetDateTime convert(MappingEnv env, Object source) throws CriteriaException {
        return toOffsetDateTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public OffsetDateTime beforeBind(final DataType dataType, MappingEnv env, final Object source) {
        return toOffsetDateTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public OffsetDateTime afterGet(final DataType dataType, final MappingEnv env, final Object source) {
        return toOffsetDateTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static SqlType mapToDataType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.DATETIME;
                break;
            case PostgreSQL:
                sqlType = PostgreType.TIMESTAMPTZ;
                break;
            case Oracle:
                sqlType = OracleDataType.TIMESTAMPTZ;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return sqlType;
    }


    static OffsetDateTime toOffsetDateTime(MappingType type, DataType dataType, final Object nonNull,
                                           ErrorHandler errorHandler) {
        final OffsetDateTime value;
        if (nonNull instanceof OffsetDateTime) {
            value = (OffsetDateTime) nonNull;
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime();
        } else if (nonNull instanceof String) {
            try {
                value = OffsetDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
