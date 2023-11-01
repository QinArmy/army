package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SQLType;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;


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

    public static final MonthType INSTANCE = new MonthType();

    public static MonthType form(final Class<?> javaType) {
        if (javaType != Month.class) {
            throw errorJavaType(MonthType.class, javaType);
        }
        return INSTANCE;
    }

    private MonthType() {
    }

    @Override
    public Class<?> javaType() {
        return Month.class;
    }

    @Override
    public SQLType map(final ServerMeta meta) {
        return NameEnumType.mapToSqlEnumType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Month convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToMoth(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String beforeBind(SQLType type, MappingEnv env, Object nonNull) {
        return convertToMoth(this, nonNull, PARAM_ERROR_HANDLER_0)
                .name();
    }

    @Override
    public Month afterGet(SQLType type, MappingEnv env, Object nonNull) {
        return convertToMoth(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static Month convertToMoth(final MappingType type, final Object nonNull,
                                       final BiFunction<MappingType, Object, ArmyException> errorHandler) {
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
            throw errorHandler.apply(type, nonNull);
        } else if (Character.isLetter(((String) nonNull).charAt(0))) {
            try {
                value = Month.valueOf((String) nonNull);
            } catch (IllegalArgumentException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
