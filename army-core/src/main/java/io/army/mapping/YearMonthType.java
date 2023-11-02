package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.function.BiFunction;

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

    public static final YearMonthType INSTANCE = new YearMonthType();

    public static YearMonthType from(final Class<?> fieldType) {
        if (fieldType != YearMonth.class) {
            throw errorJavaType(YearMonthType.class, fieldType);
        }
        return INSTANCE;
    }


    private YearMonthType() {
    }

    @Override
    public Class<?> javaType() {
        return YearMonth.class;
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return LocalDateType.mapToSqlType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public YearMonth convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToYearMonth(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public LocalDate beforeBind(SqlType type, final MappingEnv env, final Object nonNull) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalDate();
        } else {
            final YearMonth v;
            v = _convertToYearMonth(this, nonNull, PARAM_ERROR_HANDLER_0);
            value = LocalDate.of(v.getYear(), v.getMonth(), 1);
        }
        return value;
    }

    @Override
    public YearMonth afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToYearMonth(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static YearMonth _convertToYearMonth(final MappingType type, final Object nonNull,
                                                 final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final YearMonth value;
        if (nonNull instanceof YearMonth) {
            value = (YearMonth) nonNull;
        } else if (nonNull instanceof LocalDate) {
            value = YearMonth.from((LocalDate) nonNull);
        } else if (nonNull instanceof LocalDateTime) {
            value = YearMonth.from((LocalDateTime) nonNull);
        } else if (nonNull instanceof OffsetDateTime) {
            value = YearMonth.from(((OffsetDateTime) nonNull));
        } else if (nonNull instanceof ZonedDateTime) {
            value = YearMonth.from(((ZonedDateTime) nonNull));
        } else if (!(nonNull instanceof String)) {
            throw errorHandler.apply(type, nonNull);
        } else {
            final String text = (String) nonNull;
            try {
                if (text.indexOf('-') == text.lastIndexOf('-')) {
                    value = YearMonth.parse((String) nonNull);
                } else {
                    value = YearMonth.from(LocalDate.parse((String) nonNull));
                }
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }

        }
        return value;
    }


}
