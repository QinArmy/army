package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.time.*;

/**
 * <p>
 * This class is mapping class of {@link LocalDate}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link YearMonth}</li>
 *     <li>{@link MonthDay}</li>
 *     <li>{@link String} </li>
 * </ul>
 *  to sql date,if overflow,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class LocalDateType extends _ArmyNoInjectionMapping implements MappingType.SqlLocalDateType {


    public static LocalDateType from(final Class<?> javaType) {
        if (javaType != LocalDate.class) {
            throw errorJavaType(LocalDateType.class, javaType);
        }
        return INSTANCE;
    }

    public static final LocalDateType INSTANCE = new LocalDateType();

    /**
     * private constructor
     */
    private LocalDateType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public LocalDate convert(MappingEnv env, Object source) throws CriteriaException {
        return toLocalDateTime(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate beforeBind(DataType dataType, MappingEnv env, Object source) {
        return toLocalDateTime(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public LocalDate afterGet(DataType dataType, MappingEnv env, Object source) {
        return toLocalDateTime(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.DATE;
                break;
            case PostgreSQL:
                sqlType = PostgreType.DATE;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return sqlType;
    }

    public static LocalDate toLocalDateTime(final MappingType type, final DataType dataType, final Object nonNull,
                                            final ErrorHandler errorHandler) {
        final LocalDate value;
        if (nonNull instanceof LocalDate) {
            value = (LocalDate) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = LocalDate.parse((String) nonNull);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).toLocalDate();
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDate();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDate();
        } else if (nonNull instanceof YearMonth) {
            final YearMonth v = (YearMonth) nonNull;
            value = LocalDate.of(v.getYear(), v.getMonth(), 1);
        } else if (nonNull instanceof MonthDay) {
            final MonthDay v = (MonthDay) nonNull;
            value = LocalDate.of(1970, v.getMonth(), v.getDayOfMonth());
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
