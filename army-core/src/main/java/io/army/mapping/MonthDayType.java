package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.MonthDayArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

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
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link MonthDay},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class MonthDayType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalDateType {

    public static MonthDayType from(final Class<?> fieldType) {
        if (fieldType != MonthDay.class) {
            throw errorJavaType(MonthDayType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final MonthDayType INSTANCE = new MonthDayType();

    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter(Locale.ENGLISH);

    /**
     * private constructor
     */
    private MonthDayType() {
    }

    @Override
    public Class<?> javaType() {
        return MonthDay.class;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return MonthDayArrayType.LINEAR;
    }

    @Override
    public DataType map(ServerMeta meta) {
        return LocalDateType.mapToSqlType(this, meta);
    }


    @Override
    public MonthDay convert(MappingEnv env, Object source) throws CriteriaException {
        return toMonthDay(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(DataType dataType, MappingEnv env, final Object source) {
        final LocalDate value;
        if (source instanceof LocalDate) {
            value = (LocalDate) source;
        } else if (source instanceof LocalDateTime) {
            value = ((LocalDateTime) source).toLocalDate();
        } else {
            final MonthDay monthDay;
            monthDay = toMonthDay(this, dataType, source, PARAM_ERROR_HANDLER);
            value = LocalDate.of(1970, monthDay.getMonth(), monthDay.getDayOfMonth());
        }
        return value;
    }

    @Override
    public MonthDay afterGet(DataType dataType, MappingEnv env, final Object source) {
        return toMonthDay(this, dataType, source, ACCESS_ERROR_HANDLER);

    }


    static MonthDay toMonthDay(final MappingType type, final DataType dataType, final Object nonNull,
                               final ErrorHandler errorHandler) {
        final MonthDay value;
        if (nonNull instanceof MonthDay) {
            value = (MonthDay) nonNull;
        } else if (nonNull instanceof LocalDate) {
            value = MonthDay.from((LocalDate) nonNull);
        } else if (nonNull instanceof LocalDateTime) {
            value = MonthDay.from((LocalDateTime) nonNull);
        } else if (!(nonNull instanceof String)) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        } else if (((String) nonNull).contains("--")) {
            try {
                value = MonthDay.parse((String) nonNull);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (((String) nonNull).length() == 5) {
            try {
                value = MonthDay.parse((String) nonNull, FORMATTER);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            try {
                value = MonthDay.parse((String) nonNull, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        }
        return value;
    }


}
