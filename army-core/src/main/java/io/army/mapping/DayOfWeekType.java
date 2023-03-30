package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;

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

    public static final DayOfWeekType INSTANCE = new DayOfWeekType();


    public static DayOfWeekType from(final Class<?> javaType) {
        if (javaType != DayOfWeek.class) {
            throw errorJavaType(DayOfWeekType.class, javaType);
        }
        return INSTANCE;
    }


    private DayOfWeekType() {
    }

    @Override
    public Class<?> javaType() {
        return DayOfWeekType.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        return NameEnumType.mapToSqlEnumType(this, meta);
    }

    @Override
    public DayOfWeek convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        return convertToDayOfWeek(this, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        return convertToDayOfWeek(this, nonNull, PARAM_ERROR_HANDLER)
                .name();
    }

    @Override
    public DayOfWeek afterGet(SqlType type, MappingEnv env, final Object nonNull) throws DataAccessException {
        return convertToDayOfWeek(this, nonNull, DATA_ACCESS_ERROR_HANDLER);
    }

    private static DayOfWeek convertToDayOfWeek(final MappingType type, final Object nonNull,
                                                final BiFunction<MappingType, Object, ArmyException> errorHandler) {
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
            throw errorHandler.apply(type, nonNull);
        } else if (((String) nonNull).indexOf('-') < 0) {
            try {
                value = DayOfWeek.valueOf((String) nonNull);
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            try {
                value = DayOfWeek.from(LocalDate.parse((String) nonNull));
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        }
        return value;
    }


}
