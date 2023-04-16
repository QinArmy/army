package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.function.BiFunction;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

/**
 * <p>
 * This class is mapping class of {@link MonthDay}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link MonthDay},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class MonthDayType extends _ArmyNoInjectionMapping implements AbstractMappingType.SqlLocalDateType {

    public static final MonthDayType INSTANCE = new MonthDayType();

    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter(Locale.ENGLISH);


    public static MonthDayType from(final Class<?> fieldType) {
        if (fieldType != MonthDay.class) {
            throw errorJavaType(MonthDayType.class, fieldType);
        }
        return INSTANCE;
    }


    private MonthDayType() {
    }

    @Override
    public Class<?> javaType() {
        return MonthDay.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        return LocalDateType.mapToSqlType(this, meta);
    }

    @Override
    public MonthDay convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToMonthDay(this, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalDate();
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).toLocalDate();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toLocalDate();
        } else {
            final MonthDay monthDay;
            monthDay = convertToMonthDay(this, nonNull, PARAM_ERROR_HANDLER);
            value = LocalDate.of(1970, monthDay.getMonth(), monthDay.getDayOfMonth());
        }
        return value;
    }

    @Override
    public MonthDay afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToMonthDay(this, nonNull, DATA_ACCESS_ERROR_HANDLER);

    }


    private static MonthDay convertToMonthDay(final MappingType type, final Object nonNull,
                                              final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final MonthDay value;
        if (nonNull instanceof MonthDay) {
            value = (MonthDay) nonNull;
        } else if (nonNull instanceof LocalDate) {
            value = MonthDay.from((LocalDate) nonNull);
        } else if (nonNull instanceof LocalDateTime) {
            value = MonthDay.from((LocalDateTime) nonNull);
        } else if (nonNull instanceof OffsetDateTime) {
            value = MonthDay.from(((OffsetDateTime) nonNull));
        } else if (nonNull instanceof ZonedDateTime) {
            value = MonthDay.from(((ZonedDateTime) nonNull));
        } else if (!(nonNull instanceof String)) {
            throw errorHandler.apply(type, nonNull);
        } else if (((String) nonNull).contains("--")) {
            try {
                value = MonthDay.parse((String) nonNull);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (((String) nonNull).length() == 5) {
            try {
                value = MonthDay.parse((String) nonNull, FORMATTER);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            try {
                value = MonthDay.parse((String) nonNull, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        }
        return value;
    }


}
