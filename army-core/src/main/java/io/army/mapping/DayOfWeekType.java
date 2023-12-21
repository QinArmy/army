package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.DayOfWeekArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.util._TimeUtils;

import javax.annotation.Nullable;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Objects;

/**
 * <p>This class is mapping class of {@link DayOfWeek}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link String} , {@link DayOfWeek#name()} or {@link LocalDate} string</li>
 * </ul>
 *  to {@link DayOfWeek},if error,throw {@link io.army.ArmyException}
 * * @since 0.6.0
 */
public final class DayOfWeekType extends _ArmyNoInjectionMapping {


    public static DayOfWeekType from(final Class<?> javaType) {
        if (javaType != DayOfWeek.class) {
            throw errorJavaType(DayOfWeekType.class, javaType);
        }
        return DEFAULT;
    }

    public static final DayOfWeekType DEFAULT = new DayOfWeekType(null);

    private final String enumName;


    /**
     * private constructor
     */
    private DayOfWeekType(@Nullable String enumName) {
        this.enumName = enumName;
    }

    @Override
    public Class<?> javaType() {
        return DayOfWeek.class;
    }


    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return DayOfWeekArrayType.LINEAR;
    }

    @Override
    public boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (type instanceof DayOfWeekType) {
            final DayOfWeekType o = (DayOfWeekType) type;
            match = Objects.equals(o.enumName, this.enumName);
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public DataType map(final ServerMeta meta) {
        return NameEnumType.mapToDataType(this, meta, this.enumName);
    }


    @Override
    public DayOfWeek convert(final MappingEnv env, final Object source) throws CriteriaException {
        return toDayOfWeek(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source)
            throws CriteriaException {
        return toDayOfWeek(this, dataType, source, PARAM_ERROR_HANDLER)
                .name();
    }

    @Override
    public DayOfWeek afterGet(DataType dataType, MappingEnv env, final Object source) throws DataAccessException {
        return toDayOfWeek(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    private static DayOfWeek toDayOfWeek(final MappingType type, final DataType dataType, final Object source,
                                         final ErrorHandler errorHandler) {
        final DayOfWeek value;
        final String sourceStr;
        final int length;
        final char ch;

        if (source instanceof DayOfWeek) {
            value = (DayOfWeek) source;
        } else if (source instanceof LocalDate
                || source instanceof LocalDateTime
                || source instanceof OffsetDateTime
                || source instanceof ZonedDateTime) {
            value = DayOfWeek.from((TemporalAccessor) source);
        } else if (source instanceof Integer) {
            value = weekFromInt(type, dataType, (Integer) source, errorHandler);
        } else if (source instanceof Long) {
            final long v = (Long) source;
            if (v < 1 || v > 7) {
                throw errorHandler.apply(type, dataType, source, null);
            }
            value = weekFromInt(type, dataType, (int) v, errorHandler);
        } else if (!(source instanceof String) || (length = (sourceStr = (String) source).length()) == 0) {
            throw errorHandler.apply(type, dataType, source, null);
        } else if (sourceStr.indexOf('-') < 0) {
            try {
                // https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayname
                value = DayOfWeek.valueOf(sourceStr.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else {
            try {
                if (length > 24 && ((ch = sourceStr.charAt(length - 6)) == '-' || ch == '+')) {
                    value = DayOfWeek.from(OffsetDateTime.parse(sourceStr, _TimeUtils.OFFSET_DATETIME_FORMATTER_6));
                } else if (sourceStr.lastIndexOf(':') < 0) {
                    value = DayOfWeek.from(LocalDate.parse(sourceStr));
                } else {
                    value = DayOfWeek.from(LocalDateTime.parse(sourceStr, _TimeUtils.DATETIME_FORMATTER_6));
                }
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        }
        return value;
    }


    private static DayOfWeek weekFromInt(final MappingType type, final DataType dataType, final int source,
                                         final ErrorHandler errorHandler) {
        if (errorHandler != ACCESS_ERROR_HANDLER) {
            throw errorHandler.apply(type, dataType, source, null);
        }

        final DayOfWeek value;
        if (dataType == MySQLType.INT || dataType == MySQLType.BIGINT) {
            // https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofweek
            switch (source) {
                case 1:
                    value = DayOfWeek.SUNDAY;
                    break;
                case 2:
                    value = DayOfWeek.MONDAY;
                    break;
                case 3:
                    value = DayOfWeek.TUESDAY;
                    break;
                case 4:
                    value = DayOfWeek.WEDNESDAY;
                    break;
                case 5:
                    value = DayOfWeek.THURSDAY;
                    break;
                case 6:
                    value = DayOfWeek.FRIDAY;
                    break;
                case 7:
                    value = DayOfWeek.SATURDAY;
                    break;
                default:
                    throw errorHandler.apply(type, dataType, source, null);

            }
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }

        return value;
    }


}
