package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

/**
 * <p>
 * This class is mapping class of {@link OffsetTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link java.time.LocalTime}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link OffsetTime},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class OffsetTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlOffsetTimeType {

    public static final OffsetTimeType INSTANCE = new OffsetTimeType();

    public static OffsetTimeType from(Class<?> javaType) {
        if (javaType != OffsetTime.class) {
            throw errorJavaType(OffsetTimeType.class, javaType);
        }
        return INSTANCE;
    }

    private OffsetTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetTime.class;
    }


    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case PostgreSQL:
                type = PostgreDataType.TIMETZ;
                break;
            case Oracle:
                type = OracleDataType.TIMESTAMPTZ;
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
    public OffsetTime convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return this.convertBeforeBind(this.map(env.serverMeta()), nonNull);
    }

    @Override
    public Temporal beforeBind(final SqlType type, final MappingEnv env, final Object nonNull) {
        final OffsetTime time;
        time = this.convertBeforeBind(type, nonNull);
        final Temporal value;
        switch (type.database()) {
            case MySQL:
                value = time.withOffsetSameInstant(env.databaseZoneOffset())
                        .toLocalTime();
                break;
            case PostgreSQL:
            case H2:
            default:
                value = time;
        }
        return value;
    }

    @Override
    public OffsetTime afterGet(final SqlType type, final MappingEnv env, final Object nonNull) {
        final OffsetTime value;
        if (nonNull instanceof OffsetTime) {
            value = (OffsetTime) nonNull;
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).toOffsetTime();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime().toOffsetTime();
        } else if (nonNull instanceof LocalDateTime) {
            value = OffsetDateTime.of((LocalDateTime) nonNull, env.databaseZoneOffset())
                    .toOffsetTime();
        } else if (nonNull instanceof LocalTime) {
            value = OffsetTime.of((LocalTime) nonNull, env.databaseZoneOffset());
        } else if (nonNull instanceof String) {
            try {
                value = parseAfterGet(type, env, nonNull);
            } catch (DateTimeParseException e) {
                throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
            }
        } else {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }

        return value;
    }


    private OffsetTime convertBeforeBind(final SqlType sqlType, final Object nonNull) {
        final OffsetTime value;
        if (nonNull instanceof OffsetTime) {
            value = (OffsetTime) nonNull;
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).toOffsetTime();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime().toOffsetTime();
        } else if (nonNull instanceof LocalDateTime) {
            value = OffsetDateTime.of((LocalDateTime) nonNull, _TimeUtils.systemZoneOffset())
                    .toOffsetTime();
        } else if (nonNull instanceof LocalTime) {
            value = OffsetTime.of((LocalTime) nonNull, _TimeUtils.systemZoneOffset());
        } else if (nonNull instanceof String) {
            try {
                value = parseBeforeBind(sqlType, nonNull);
            } catch (DateTimeParseException e) {
                throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
            }
        } else {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }

        return value;
    }


    private static OffsetTime parseBeforeBind(final SqlType sqlType, final Object nonNull)
            throws DateTimeParseException {
        OffsetTime value;
        switch (sqlType.database()) {
            case MySQL: {
                try {
                    value = OffsetTime.of(LocalTime.parse((String) nonNull), _TimeUtils.systemZoneOffset());
                } catch (DateTimeParseException e) {
                    value = OffsetTime.parse((String) nonNull, _TimeUtils.OFFSET_TIME_FORMATTER_6);
                }
            }
            break;
            case PostgreSQL:
            default: {
                try {
                    value = OffsetTime.parse((String) nonNull, _TimeUtils.OFFSET_TIME_FORMATTER_6);
                } catch (DateTimeParseException e) {
                    value = OffsetTime.of(LocalTime.parse((String) nonNull), _TimeUtils.systemZoneOffset());
                }
            }
        }
        return value;

    }


    private static OffsetTime parseAfterGet(final SqlType sqlType, final MappingEnv env, final Object nonNull)
            throws DateTimeParseException {
        final OffsetTime value;
        switch (sqlType.database()) {
            case MySQL:
                value = OffsetTime.of(LocalTime.parse((String) nonNull), env.databaseZoneOffset());
                break;
            case PostgreSQL:
            default: {
                OffsetTime v;
                try {
                    v = OffsetTime.parse((String) nonNull, _TimeUtils.OFFSET_TIME_FORMATTER_6);
                } catch (DateTimeParseException e) {
                    v = OffsetTime.of(LocalTime.parse((String) nonNull), env.databaseZoneOffset());
                }
                value = v;
            }
        }
        return value;

    }


}
