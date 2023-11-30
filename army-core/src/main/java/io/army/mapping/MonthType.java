package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.time.*;
import java.time.temporal.TemporalAccessor;


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
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link String} , {@link Month#name()} or {@link LocalDate} string</li>
 * </ul>
 *  to {@link Month},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class MonthType extends _ArmyNoInjectionMapping implements MappingType.SqlTemporalFieldType {


    public static MonthType form(final Class<?> javaType) {
        if (javaType != Month.class) {
            throw errorJavaType(MonthType.class, javaType);
        }
        return INSTANCE;
    }

    public static final MonthType INSTANCE = new MonthType();

    /**
     * private constructor
     */
    private MonthType() {
    }

    @Override
    public Class<?> javaType() {
        return Month.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return NameEnumType.mapToSqlType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Month convert(MappingEnv env, Object source) throws CriteriaException {
        return toMoth(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        return toMoth(this, dataType, source, PARAM_ERROR_HANDLER)
                .name();
    }

    @Override
    public Month afterGet(DataType dataType, MappingEnv env, Object source) {
        return toMoth(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    public static Month toMoth(final MappingType type, final DataType dataType, final Object nonNull,
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


}
