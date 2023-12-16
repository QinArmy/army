package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.DayOfWeekArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

import javax.annotation.Nullable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
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
        return DayOfWeekType.class;
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

    private static DayOfWeek toDayOfWeek(final MappingType type, final DataType dataType, final Object nonNull,
                                         final ErrorHandler errorHandler) {
        final DayOfWeek value;
        if (nonNull instanceof DayOfWeek) {
            value = (DayOfWeek) nonNull;
        } else if (nonNull instanceof LocalDate
                || nonNull instanceof LocalDateTime) {
            value = DayOfWeek.from((TemporalAccessor) nonNull);
        } else if (!(nonNull instanceof String) || ((String) nonNull).length() == 0) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        } else if (((String) nonNull).indexOf('-') < 0) {
            try {
                value = DayOfWeek.valueOf((String) nonNull);
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            try {
                value = DayOfWeek.from(LocalDate.parse((String) nonNull));
            } catch (DateTimeParseException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        }
        return value;
    }


}
