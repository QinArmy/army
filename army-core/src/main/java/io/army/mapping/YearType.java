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
 * This class is mapping class of {@link Year}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link YearMonth}</li>
 *     <li>{@link String} ,{@link Year} string {@link YearMonth} string or {@link LocalDate} string</li>
 * </ul>
 *  to {@link Year},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @since 1.0
 */
public final class YearType extends _ArmyNoInjectionMapping implements MappingType.SqlTemporalType,
        MappingType.SqlTemporalFieldType {

    public static YearType from(final Class<?> fieldType) {
        if (fieldType != Year.class) {
            throw errorJavaType(YearType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final YearType INSTANCE = new YearType();

    /**
     * private constructor
     */
    private YearType() {
    }

    @Override
    public Class<?> javaType() {
        return Year.class;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.YEAR;
                break;
            case PostgreSQL:
                type = PostgreType.DATE;
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
    public Year convert(MappingEnv env, Object source) throws CriteriaException {
        return toYear(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(final DataType dataType, final MappingEnv env, final Object source) {
        final Object value;
        switch (((SqlType) dataType).database()) {
            case MySQL: {
                if (source instanceof Short) {
                    value = source;
                } else if (source instanceof Integer) {
                    final int v = (Integer) source;
                    if (v < Short.MIN_VALUE || v > Short.MAX_VALUE) {
                        throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
                    }
                    value = (short) v;
                } else {
                    value = toYear(this, dataType, source, PARAM_ERROR_HANDLER);
                }
            }
            break;
            case PostgreSQL: {
                if (source instanceof LocalDate) {
                    value = source;
                } else if (source instanceof LocalDateTime) {
                    value = ((LocalDateTime) source).toLocalDate();
                } else {
                    final Year year;
                    year = toYear(this, dataType, source, PARAM_ERROR_HANDLER);
                    value = LocalDate.of(year.getValue(), Month.JANUARY, 1);
                }
            }
            break;
            default:
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public Year afterGet(DataType dataType, MappingEnv env, Object source) {
        return toYear(this, dataType, source, ACCESS_ERROR_HANDLER);
    }

    public static Year toYear(final MappingType type, DataType dataType, final Object nonNull,
                              final ErrorHandler errorHandler) {
        final Year value;
        if (nonNull instanceof Year) {
            value = (Year) nonNull;
        } else if (nonNull instanceof Integer || nonNull instanceof Short) {
            value = Year.of(((Number) nonNull).intValue());
        } else if (nonNull instanceof LocalDate) {
            value = Year.from((LocalDate) nonNull);
        } else if (nonNull instanceof LocalDateTime) {
            value = Year.from((LocalDateTime) nonNull);
        } else if (nonNull instanceof YearMonth) {
            value = Year.from((YearMonth) nonNull);
        } else if (nonNull instanceof String) {
            final String text = (String) nonNull;
            final int index = text.indexOf('-');

            try {
                if (index < 0) {
                    value = Year.parse((String) nonNull);
                } else if (index == text.lastIndexOf('-')) {
                    value = Year.from(YearMonth.parse((String) nonNull));
                } else {
                    value = Year.from(LocalDate.parse((String) nonNull));
                }
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
