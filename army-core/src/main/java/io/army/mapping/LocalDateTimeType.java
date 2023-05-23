package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link LocalDateTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link LocalDateTime},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class LocalDateTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalDateTimeType {


    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType from(final Class<?> fieldType) {
        if (fieldType != LocalDateTime.class) {
            throw errorJavaType(LocalDateTimeType.class, fieldType);
        }
        return INSTANCE;
    }


    private LocalDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.DATETIME;
                break;
            case PostgreSQL:
                type = PostgreDataType.TIMESTAMP;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }

    @Override
    public LocalDateTime convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        return convertToLocalDateTime(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public LocalDateTime beforeBind(SqlType type, final MappingEnv env, final Object nonNull) {
        return convertToLocalDateTime(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public LocalDateTime afterGet(SqlType type, final MappingEnv env, Object nonNull) {
        return convertToLocalDateTime(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }

    private static LocalDateTime convertToLocalDateTime(final MappingType type, final Object nonNull,
                                                        final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final LocalDateTime value;
        if (nonNull instanceof LocalDateTime) {
            value = (LocalDateTime) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof LocalDate) {
            value = LocalDateTime.of((LocalDate) nonNull, LocalTime.MIDNIGHT);
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
