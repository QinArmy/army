package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
import io.army.sqltype.SqlType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link LocalTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalTime}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.OffsetTime}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link LocalTime},if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class LocalTimeType extends _ArmyNoInjectionMapping {


    public static final LocalTimeType INSTANCE = new LocalTimeType();

    public static LocalTimeType from(final Class<?> fieldType) {
        if (fieldType != LocalTime.class) {
            throw errorJavaType(LocalTimeType.class, fieldType);
        }
        return INSTANCE;
    }


    private LocalTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalTime.class;
    }


    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.TIME;
                break;
            case PostgreSQL:
                type = PostgreTypes.TIME;
                break;
            default:
                throw noMappingError(meta);

        }
        return type;
    }


    @Override
    public LocalTime convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToLocalDateTime(this, env, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalTime beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToLocalDateTime(this, env, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalTime afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        return convertToLocalDateTime(this, env, nonNull, DATA_ACCESS_ERROR_HANDLER);
    }

    private static LocalTime convertToLocalDateTime(final MappingType type, final MappingEnv env, final Object nonNull,
                                                    final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final LocalTime value;
        if (nonNull instanceof LocalTime) {
            value = (LocalTime) nonNull;
        } else if (nonNull instanceof String) {
            //TODO consider format
            try {
                value = LocalTime.parse((String) nonNull);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalTime();
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).atZoneSameInstant(env.zoneId())
                    .toLocalTime();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).withZoneSameInstant(env.zoneId())
                    .toLocalTime();
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
