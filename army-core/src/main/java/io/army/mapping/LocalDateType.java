package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link LocalDate}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link YearMonth}</li>
 *     <li>{@link MonthDay}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to sql date,if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class LocalDateType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalDateType {


    public static final LocalDateType INSTANCE = new LocalDateType();

    public static LocalDateType from(final Class<?> javaType) {
        if (javaType != LocalDate.class) {
            throw errorJavaType(LocalDateType.class, javaType);
        }
        return INSTANCE;
    }


    private LocalDateType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }

    @Override
    public LocalDate convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToLocalDateTime(this, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToLocalDateTime(this, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToLocalDateTime(this, nonNull, DATA_ACCESS_ERROR_HANDLER);
    }


    static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.DATE;
                break;
            case PostgreSQL:
                sqlType = PostgreTypes.DATE;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return sqlType;
    }

    static LocalDate _convertToLocalDateTime(final MappingType type, final Object nonNull,
                                             final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDate.parse((String) nonNull);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalDate();
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDate();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDate();
        } else if (nonNull instanceof YearMonth) {
            final YearMonth v = (YearMonth) nonNull;
            value = LocalDate.of(v.getYear(), v.getMonth(), 1);
        } else if (nonNull instanceof MonthDay) {
            final MonthDay v = (MonthDay) nonNull;
            value = LocalDate.of(1970, v.getMonth(), v.getDayOfMonth());
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
