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
public final class LocalDateType extends _ArmyNoInjectionMapping {


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
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.DATE;
                break;
            case PostgreSQL:
                type = PostgreTypes.DATE;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }

    @Override
    public LocalDate convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToLocalDateTime(this, env, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToLocalDateTime(this, env, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return _convertToLocalDateTime(this, env, nonNull, DATA_ACCESS_ERROR_HANDLER);
    }

    static LocalDate _convertToLocalDateTime(final MappingType type, final MappingEnv env, final Object nonNull,
                                             final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            //TODO consider other format
            try {
                value = LocalDate.parse((String) nonNull);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalDate();
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).atZoneSameInstant(env.zoneId())
                    .toLocalDate();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).withZoneSameInstant(env.zoneId())
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
