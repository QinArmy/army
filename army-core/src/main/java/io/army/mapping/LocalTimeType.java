package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.*;

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


    public static LocalTimeType from(final Class<?> fieldType) {
        if (fieldType != LocalTime.class) {
            throw errorJavaType(LocalTimeType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final LocalTimeType INSTANCE = new LocalTimeType();

    /**
     * private constructor
     */
    private LocalTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalTime.class;
    }


    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.TIME;
                break;
            case PostgreSQL:
                type = PostgreType.TIME;
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
    public LocalTime convert(MappingEnv env, Object source) throws CriteriaException {
        return toLocalTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalTime beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toLocalTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalTime afterGet(final DataType dataType, MappingEnv env, final Object source) {
        return toLocalTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    public static LocalTime toLocalTime(final MappingType type, final DataType dataType, final Object nonNull,
                                        final ErrorHandler errorHandler) {
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
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
