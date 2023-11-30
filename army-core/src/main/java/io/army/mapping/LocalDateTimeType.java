package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.util._TimeUtils;

import java.time.*;
import java.time.format.DateTimeParseException;

/**
 * <p>
 * This class is mapping class of {@link LocalDateTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link LocalDateTime},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class LocalDateTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalDateTimeType {


    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType from(final Class<?> fieldType) {
        if (fieldType != LocalDateTime.class) {
            throw errorJavaType(LocalDateTimeType.class, fieldType);
        }
        return INSTANCE;
    }

    /**
     * private constructor
     */
    private LocalDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final DataType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.DATETIME;
                break;
            case PostgreSQL:
                type = PostgreType.TIMESTAMP;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public LocalDateTime convert(final MappingEnv env, final Object source) throws CriteriaException {
        return toLocalDateTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDateTime beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        return toLocalDateTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDateTime afterGet(DataType dataType, final MappingEnv env, Object source) {
        return toLocalDateTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    public static LocalDateTime toLocalDateTime(final MappingType type, final DataType dataType, final Object nonNull,
                                                final ErrorHandler errorHandler) {
        final LocalDateTime value;
        if (nonNull instanceof LocalDateTime) {
            value = (LocalDateTime) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof LocalDate) {
            value = LocalDateTime.of((LocalDate) nonNull, LocalTime.MIDNIGHT);
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
