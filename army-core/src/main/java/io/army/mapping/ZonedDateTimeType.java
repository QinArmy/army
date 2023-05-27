package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

/**
 * <p>
 * This class is mapping class of {@link ZonedDateTime}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to {@link ZonedDateTime},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class ZonedDateTimeType extends _ArmyNoInjectionMapping implements MappingType.SqlOffsetDateTimeType {

    public static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    public static ZonedDateTimeType from(Class<?> javaType) {
        if (javaType != ZonedDateTime.class) {
            throw errorJavaType(ZonedDateTimeType.class, javaType);
        }
        return INSTANCE;
    }


    private ZonedDateTimeType() {
    }


    @Override
    public Class<?> javaType() {
        return ZonedDateTime.class;
    }


    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return OffsetDateTimeType.mapToSqlType(meta, this);
    }

    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public ZonedDateTime convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return this.convertBeforeBind(this.map(env.serverMeta()), nonNull);
    }

    @Override
    public Temporal beforeBind(final SqlType type, final MappingEnv env, final Object nonNull) throws CriteriaException {
        final ZonedDateTime dateTime;
        dateTime = this.convertBeforeBind(type, nonNull);
        final Temporal value;
        switch (type.database()) {
            case MySQL:
                value = dateTime.withZoneSameInstant(env.databaseZoneOffset())
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
    public ZonedDateTime afterGet(final SqlType type, final MappingEnv env, final Object nonNull)
            throws DataAccessException {
        final ZonedDateTime value;
        if (nonNull instanceof ZonedDateTime) {
            value = (ZonedDateTime) nonNull;
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).toZonedDateTime();
        } else if (nonNull instanceof LocalDateTime) {
            value = ZonedDateTime.of((LocalDateTime) nonNull, env.databaseZoneOffset());
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


    private ZonedDateTime convertBeforeBind(final SqlType sqlType, final Object nonNull) {
        final ZonedDateTime dateTime;
        if (nonNull instanceof ZonedDateTime) {
            dateTime = (ZonedDateTime) nonNull;
        } else if (nonNull instanceof OffsetDateTime) {
            dateTime = ((OffsetDateTime) nonNull).toZonedDateTime();
        } else if (nonNull instanceof LocalDateTime) {
            dateTime = ZonedDateTime.of((LocalDateTime) nonNull, _TimeUtils.systemZoneOffset());
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


    private static ZonedDateTime parseBeforeBind(final SqlType sqlType, final Object nonNull)
            throws DateTimeParseException {
        ZonedDateTime value;
        switch (sqlType.database()) {
            case MySQL: {
                try {
                    value = ZonedDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                            _TimeUtils.systemZoneOffset());
                } catch (DateTimeParseException e) {
                    value = ZonedDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                }
            }
            break;
            case PostgreSQL:
            default: {
                try {
                    value = ZonedDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                } catch (DateTimeParseException e) {
                    value = ZonedDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                            _TimeUtils.systemZoneOffset());
                }
            }
        }
        return value;

    }


    private static ZonedDateTime parseAfterGet(final SqlType sqlType, final MappingEnv env, final Object nonNull)
            throws DateTimeParseException {
        final ZonedDateTime value;
        switch (sqlType.database()) {
            case MySQL:
                value = ZonedDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                        env.databaseZoneOffset());
                break;
            case PostgreSQL:
            default: {
                ZonedDateTime v;
                try {
                    v = ZonedDateTime.parse((String) nonNull, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                } catch (DateTimeParseException e) {
                    v = ZonedDateTime.of(LocalDateTime.parse((String) nonNull, _TimeUtils.DATETIME_FORMATTER_6),
                            env.databaseZoneOffset());
                }
                value = v;
            }
        }
        return value;

    }


}
