package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.MonthArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>
 * This class is mapping class of {@link Month}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link YearMonth}</li>
 *     <li>{@link MonthDay}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link String} , {@link Month#name()} or {@link LocalDate} string</li>
 * </ul>
 *  to {@link Month},if error,throw {@link io.army.ArmyException}
*
 * @since 1.0
 */
public class MonthType extends _ArmyNoInjectionMapping implements MappingType.SqlTemporalFieldType {


    public static MonthType form(final Class<?> javaType) {
        if (javaType != Month.class) {
            throw errorJavaType(MonthType.class, javaType);
        }
        return DEFAULT;
    }

    public static MonthType fromParam(final Class<?> enumType, final String enumName) {
        if (enumType != Month.class) {
            throw errorJavaType(MonthType.class, enumType);
        } else if (!_StringUtils.hasText(enumName)) {
            throw new IllegalArgumentException("no text");
        }
        return MonthEnumType.INSTANCE.computeIfAbsent(enumName, MonthEnumType::new);
    }

    public static final MonthType DEFAULT = new MonthType(null);

    private final String enumName;

    /**
     * private constructor
     */
    private MonthType(@Nullable String enumName) {
        this.enumName = enumName;
    }

    @Override
    public final Class<?> javaType() {
        return Month.class;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        return MonthArrayType.LINEAR;
    }

    @Override
    public boolean isSameType(final MappingType type) {
        final boolean match;
        if (type == this) {
            match = true;
        } else if (type instanceof MonthType) {
            final MonthType o = (MonthType) type;
            match = Objects.equals(o.enumName, this.enumName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final DataType map(final ServerMeta meta) {
        return NameEnumType.mapToDataType(this, meta, this.enumName);
    }


    @Override
    public final Month convert(MappingEnv env, Object source) throws CriteriaException {
        return toMoth(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, Object source) {
        return toMoth(this, dataType, source, PARAM_ERROR_HANDLER)
                .name();
    }

    @Override
    public final Month afterGet(DataType dataType, MappingEnv env, Object source) {
        return toMoth(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static Month toMoth(final MappingType type, final DataType dataType, final Object nonNull,
                        final ErrorHandler errorHandler) {
        final Month value;
        if (nonNull instanceof Month) {
            value = (Month) nonNull;
        } else if (nonNull instanceof LocalDate
                || nonNull instanceof YearMonth
                || nonNull instanceof MonthDay
                || nonNull instanceof LocalDateTime) {
            value = Month.from((TemporalAccessor) nonNull);
        } else if (nonNull instanceof OffsetDateTime) {
            value = Month.from(((OffsetDateTime) nonNull));
        } else if (nonNull instanceof ZonedDateTime) {
            value = Month.from(((ZonedDateTime) nonNull));
        } else if (!(nonNull instanceof String) || ((String) nonNull).length() == 0) {
            throw errorHandler.apply(type, dataType, nonNull, null);
        } else if (Character.isLetter(((String) nonNull).charAt(0))) {
            try {
                value = Month.valueOf((String) nonNull);
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


    private static final class MonthEnumType extends MonthType {

        private static final ConcurrentMap<String, MonthEnumType> INSTANCE = _Collections.concurrentHashMap();

        private MonthEnumType(String enumName) {
            super(enumName);
        }

    } // MonthEnumType


}
