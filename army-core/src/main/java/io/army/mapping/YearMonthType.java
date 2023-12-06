package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * <p>
 * This class is mapping class of {@link YearMonth}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} ,{@link YearMonth} string or {@link LocalDate} string</li>
 * </ul>
 *  to {@link YearMonth},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class YearMonthType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalDateType {

    public static YearMonthType from(final Class<?> fieldType) {
        if (fieldType != YearMonth.class) {
            throw errorJavaType(YearMonthType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final YearMonthType INSTANCE = new YearMonthType();

    /**
     * private constructor
     */
    private YearMonthType() {
    }

    @Override
    public Class<?> javaType() {
        return YearMonth.class;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return LocalDateType.mapToSqlType(this, meta);
    }

    @Override
    public YearMonth convert(MappingEnv env, Object source) throws CriteriaException {
        return toYearMonth(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        final LocalDate value;
        if (source instanceof YearMonth) {
            final YearMonth v = (YearMonth) source;
            value = LocalDate.of(v.getYear(), v.getMonth(), 1);
        } else if (source instanceof LocalDate) {
            value = (LocalDate) source;
        } else if (source instanceof LocalDateTime) {
            value = ((LocalDateTime) source).toLocalDate();
        } else {
            final YearMonth v;
            v = toYearMonth(this, dataType, source, PARAM_ERROR_HANDLER);
            value = LocalDate.of(v.getYear(), v.getMonth(), 1);
        }
        return value;
    }

    @Override
    public YearMonth afterGet(DataType dataType, MappingEnv env, Object source) {
        return toYearMonth(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    public static YearMonth toYearMonth(final MappingType type, final DataType dataType, final Object nonNull,
                                        final ErrorHandler errorHandler) {
        final YearMonth value;
        if (nonNull instanceof YearMonth) {
            value = (YearMonth) nonNull;
        } else if (nonNull instanceof LocalDate) {
            value = YearMonth.from((LocalDate) nonNull);
        } else if (nonNull instanceof LocalDateTime) {
            value = YearMonth.from((LocalDateTime) nonNull);
        } else if (nonNull instanceof String) {
            final String text = (String) nonNull;
            try {
                if (text.indexOf('-') == text.lastIndexOf('-')) {
                    value = YearMonth.parse((String) nonNull);
                } else {
                    value = YearMonth.from(LocalDate.parse((String) nonNull));
                }
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }

        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
