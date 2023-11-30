package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

/**
 * <p>
 * This class is mapping class of {@link DayOfWeek}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} , {@link DayOfWeek#name()} or {@link LocalDate} string</li>
 * </ul>
 *  to {@link DayOfWeek},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class DayOfWeekType extends _ArmyNoInjectionMapping {


    public static DayOfWeekType from(final Class<?> javaType) {
        if (javaType != DayOfWeek.class) {
            throw errorJavaType(DayOfWeekType.class, javaType);
        }
        return INSTANCE;
    }

    public static final DayOfWeekType INSTANCE = new DayOfWeekType();


    /**
     * private constructor
     */
    private DayOfWeekType() {
    }

    @Override
    public Class<?> javaType() {
        return DayOfWeekType.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return NameEnumType.mapToSqlType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public DayOfWeek convert(final MappingEnv env, final Object source) throws CriteriaException {
        return convertToDayOfWeek(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source)
            throws CriteriaException {
        return convertToDayOfWeek(this, dataType, source, PARAM_ERROR_HANDLER)
                .name();
    }

    @Override
    public DayOfWeek afterGet(DataType dataType, MappingEnv env, final Object source) throws DataAccessException {
        return convertToDayOfWeek(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    private static DayOfWeek convertToDayOfWeek(final MappingType type, final DataType dataType, final Object nonNull,
                                                final ErrorHandler errorHandler) {
        final DayOfWeek value;
        if (nonNull instanceof DayOfWeek) {
            value = (DayOfWeek) nonNull;
        } else if (nonNull instanceof LocalDate
                || nonNull instanceof LocalDateTime) {
            value = DayOfWeek.from((TemporalAccessor) nonNull);
        } else if (nonNull instanceof OffsetDateTime) {
            value = DayOfWeek.from(((OffsetDateTime) nonNull));
        } else if (nonNull instanceof ZonedDateTime) {
            value = DayOfWeek.from(((ZonedDateTime) nonNull));
        } else if (!(nonNull instanceof String) || ((String) nonNull).length() == 0) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        } else if (((String) nonNull).indexOf('-') < 0) {
            try {
                value = DayOfWeek.valueOf((String) nonNull);
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            try {
                value = DayOfWeek.from(LocalDate.parse((String) nonNull));
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        }
        return value;
    }


}
