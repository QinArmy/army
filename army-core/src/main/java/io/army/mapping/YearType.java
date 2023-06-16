package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link Year}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link YearMonth}</li>
 *     <li>{@link String} ,{@link Year} string {@link YearMonth} string or {@link LocalDate} string</li>
 * </ul>
 *  to {@link Year},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class YearType extends _ArmyNoInjectionMapping implements MappingType.SqlTemporalType,
        MappingType.SqlTemporalFieldType {

    public static final YearType INSTANCE = new YearType();

    public static YearType from(final Class<?> fieldType) {
        if (fieldType != Year.class) {
            throw errorJavaType(YearType.class, fieldType);
        }
        return INSTANCE;
    }

    private YearType() {
    }

    @Override
    public Class<?> javaType() {
        return Year.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.YEAR;
                break;
            case PostgreSQL:
                type = PostgreSqlType.DATE;
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
    public Year convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToYear(this, env, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Temporal beforeBind(final SqlType type, final MappingEnv env, final Object nonNull) {
        final Temporal value;
        switch (type.database()) {
            case MySQL:
                value = _convertToYear(this, env, nonNull, PARAM_ERROR_HANDLER_0);
                break;
            case PostgreSQL: {
                if (nonNull instanceof LocalDate) {
                    value = (LocalDate) nonNull;
                } else if (nonNull instanceof LocalDateTime) {
                    value = ((LocalDateTime) nonNull).toLocalDate();
                } else {
                    final Year year;
                    year = _convertToYear(this, env, nonNull, PARAM_ERROR_HANDLER_0);
                    value = LocalDate.of(year.getValue(), Month.JANUARY, 1);
                }
            }
            break;
            default:
                throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return value;
    }

    @Override
    public Year afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToYear(this, env, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }

    private static Year _convertToYear(final MappingType type, final MappingEnv env, final Object nonNull,
                                       final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final Year value;
        if (nonNull instanceof Year) {
            value = (Year) nonNull;
        } else if (nonNull instanceof LocalDate) {
            value = Year.from((LocalDate) nonNull);
        } else if (nonNull instanceof LocalDateTime) {
            value = Year.from((LocalDateTime) nonNull);
        } else if (nonNull instanceof OffsetDateTime) {
            value = Year.from(((OffsetDateTime) nonNull).atZoneSameInstant(env.databaseZoneOffset()));
        } else if (nonNull instanceof ZonedDateTime) {
            value = Year.from(((ZonedDateTime) nonNull).withZoneSameInstant(env.databaseZoneOffset()));
        } else if (nonNull instanceof YearMonth) {
            value = Year.from((YearMonth) nonNull);
        } else if (!(nonNull instanceof String)) {
            throw errorHandler.apply(type, nonNull);
        } else {
            final String text = (String) nonNull;
            final int index = text.indexOf('-');

            try {
                if (index < 0) {
                    value = Year.parse((String) nonNull);
                } else if (index == text.lastIndexOf('-')) {
                    value = Year.from(YearMonth.parse((String) nonNull));
                } else {
                    value = Year.from(LocalDate.parse((String) nonNull));
                }
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }

        }
        return value;
    }


}
