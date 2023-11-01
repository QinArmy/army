package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;
import io.army.util._TimeUtils;

import java.time.*;
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
public final class LocalTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalTimeType {


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
    public SQLType map(final ServerMeta meta) {
        final SQLType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.TIME;
                break;
            case PostgreSQL:
                type = PostgreSqlType.TIME;
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
    public LocalTime convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToLocalTime(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public LocalTime beforeBind(SQLType type, MappingEnv env, final Object nonNull) {
        return convertToLocalTime(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public LocalTime afterGet(final SQLType type, MappingEnv env, final Object nonNull) {
        return convertToLocalTime(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }

    private static LocalTime convertToLocalTime(final MappingType type, final Object nonNull,
                                                final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final LocalTime value;
        if (nonNull instanceof LocalTime) {
            value = (LocalTime) nonNull;
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalTime();
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalTime();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalTime();
        } else if (nonNull instanceof String) {
            try {
                value = LocalTime.parse((String) nonNull, _TimeUtils.TIME_FORMATTER_6);
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
