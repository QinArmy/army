package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;
import io.army.util._TimeUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

/**
 * <p>
 * This class is mapping class of {@link OffsetDateTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link OffsetDateTime},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class OffsetDateTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlOffsetDateTimeType {

    public static final OffsetDateTimeType INSTANCE = new OffsetDateTimeType();

    public static OffsetDateTimeType from(Class<?> javaType) {
        if (javaType != OffsetDateTime.class) {
            throw errorJavaType(OffsetDateTimeType.class, javaType);
        }
        return INSTANCE;
    }


    private OffsetDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetDateTime.class;
    }

    @Override
    public SQLType map(final ServerMeta meta) {
        return mapToSqlType(meta, this);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public OffsetDateTime convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return this.convertBeforeBind(this.map(env.serverMeta()), nonNull);
    }

    @Override
    public Temporal beforeBind(final SQLType type, MappingEnv env, final Object nonNull) {
        final OffsetDateTime dateTime;
        dateTime = this.convertBeforeBind(type, nonNull);
        final Temporal value;
        switch (type.database()) {
            case MySQL:
                value = dateTime.withOffsetSameInstant(env.zoneOffset())
                        .toLocalDateTime();
                break;
            case PostgreSQL:
            case H2:
            default:
                value = dateTime;
        }
        return value;
    }

    @Override
    public OffsetDateTime afterGet(final SQLType type, final MappingEnv env, final Object nonNull) {
        final OffsetDateTime value;
        if (nonNull instanceof OffsetDateTime) {
            value = (OffsetDateTime) nonNull;
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime();
        } else if (nonNull instanceof LocalDateTime) {
            value = OffsetDateTime.of((LocalDateTime) nonNull, env.zoneOffset());
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


    private OffsetDateTime convertBeforeBind(final SQLType sqlType, final Object nonNull) {
        final OffsetDateTime dateTime;
        if (nonNull instanceof OffsetDateTime) {
            dateTime = (OffsetDateTime) nonNull;
        } else if (nonNull instanceof ZonedDateTime) {
            dateTime = ((ZonedDateTime) nonNull).toOffsetDateTime();
        } else if (nonNull instanceof LocalDateTime) {
            dateTime = OffsetDateTime.of((LocalDateTime) nonNull, _TimeUtils.systemZoneOffset());
        } else if (nonNull instanceof String) {
            try {
                dateTime = parseBeforeBind(sqlType, nonNull);
            } catch (DateTimeParseException e) {
                throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
            }
        } else {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }

        return dateTime;
    }


    static SQLType mapToSqlType(final ServerMeta meta, final MappingType type) {
        final SQLType sqlType;
        switch (meta.dialectDatabase()) {
            case PostgreSQL:
                sqlType = PostgreSqlType.TIMESTAMPTZ;
                break;
            case Oracle:
                sqlType = OracleDataType.TIMESTAMPTZ;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return sqlType;
    }


    private static OffsetDateTime parseBeforeBind(final SQLType sqlType, final Object nonNull)
            throws DateTimeParseException {
        OffsetDateTime value;
        switch (sqlType.database()) {
            case MySQL: {
                try {
                    value = OffsetDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                            _TimeUtils.systemZoneOffset());
                } catch (DateTimeParseException e) {
                    value = OffsetDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                }
            }
            break;
            case PostgreSQL:
            default: {
                try {
                    value = OffsetDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                } catch (DateTimeParseException e) {
                    value = OffsetDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                            _TimeUtils.systemZoneOffset());
                }
            }
        }
        return value;

    }


    private static OffsetDateTime parseAfterGet(final SQLType sqlType, final MappingEnv env, final Object nonNull)
            throws DateTimeParseException {
        final OffsetDateTime value;
        switch (sqlType.database()) {
            case MySQL:
                value = OffsetDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                        env.zoneOffset());
                break;
            case PostgreSQL:
            default: {
                OffsetDateTime v;
                try {
                    v = OffsetDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                } catch (DateTimeParseException e) {
                    v = OffsetDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                            env.zoneOffset());
                }
                value = v;
            }
        }
        return value;

    }


}
