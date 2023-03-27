package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
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
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link LocalDate},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class LocalDateType extends _ArmyNoInjectionMapping {


    public static final LocalDateType INSTANCE = new LocalDateType();

    public static LocalDateType from(final Class<?> fieldType) {
        if (fieldType != LocalDate.class) {
            throw errorJavaType(LocalDateType.class, fieldType);
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
                type = PostgreType.DATE;
                break;
            default:
                throw noMappingError(meta);

        }
        return type;
    }

    @Override
    public LocalDate convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToLocalDateTime(this, env, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        return convertToLocalDateTime(this, env, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate afterGet(SqlType type, MappingEnv env, Object nonNull) {
        return convertToLocalDateTime(this, env, nonNull, DATA_ACCESS_ERROR_HANDLER);
    }

    private static LocalDate convertToLocalDateTime(final MappingType type, final MappingEnv env, final Object nonNull,
                                                    final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            //TODO consider format
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
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
