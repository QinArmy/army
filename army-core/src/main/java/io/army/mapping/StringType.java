package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.StringArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util._TimeUtils;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;

/**
 * <p>
 * This class is mapping class of {@link String}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Number}</li>
 *     <li>{@link Boolean} </li>
 *     <li>{@link CodeEnum} </li>
 *     <li>{@link TextEnum} </li>
 *     <li>{@link Enum} </li>
 *     <li>{@link LocalDate} </li>
 *     <li>{@link LocalDateTime} </li>
 *     <li>{@link LocalTime} </li>
 *     <li>{@link OffsetDateTime} </li>
 *     <li>{@link ZonedDateTime} </li>
 *     <li>{@link OffsetTime} </li>
 *     <li>{@link Year}  to {@link Year} string or {@link LocalDate} string</li>
 *     <li>{@link YearMonth}  to {@link LocalDate} string </li>
 *     <li>{@link MonthDay} to {@link LocalDate} string</li>
 *     <li>{@link Instant} to {@link Instant#getEpochSecond()} string</li>
 *     <li>{@link java.time.Duration} </li>
 *     <li>{@link java.time.Period} </li>
 * </ul>
 *  to {@link String},if error,throw {@link io.army.ArmyException}
*
 * @see TextType
 * @see MediumTextType
 * @since 1.0
 */
public final class StringType extends _ArmyBuildInMapping implements MappingType.SqlStringType {


    public static StringType from(final Class<?> fieldType) {
        if (fieldType != String.class) {
            throw errorJavaType(StringType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final StringType INSTANCE = new StringType();

    /**
     * private constructor
     */
    private StringType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }


    @Override
    public MappingType arrayTypeOfThis() {
        return StringArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return mapToDataType(this, meta);
    }


    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        return toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(final DataType dataType, final MappingEnv env, final Object source) {
        return toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.VARCHAR;
                break;
            case PostgreSQL:
                sqlType = PostgreType.VARCHAR;
                break;
            case Oracle:
                sqlType = OracleDataType.VARCHAR2;
                break;
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);

        }
        return sqlType;
    }

    public static String toString(final MappingType type, final DataType dataType, final Object nonNull,
                                  final ErrorHandler errorHandler) {
        final String value;
        if (nonNull instanceof String) {
            value = (String) nonNull;
        } else if (nonNull instanceof BigDecimal) {
            value = ((BigDecimal) nonNull).toPlainString();
        } else if (nonNull instanceof Number) {
            value = nonNull.toString();
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? BooleanType.TRUE : BooleanType.FALSE;
        } else if (nonNull instanceof Enum) {
            if (nonNull instanceof CodeEnum) {
                value = Integer.toString(((CodeEnum) nonNull).code());
            } else if (nonNull instanceof TextEnum) {
                value = ((TextEnum) nonNull).text();
            } else {
                value = ((Enum<?>) nonNull).name();
            }
        } else if (nonNull instanceof Character) {
            value = nonNull.toString();
        } else if (nonNull instanceof TemporalAccessor) {
            if (nonNull instanceof LocalDate) {
                // TODO postgre format ?
                value = nonNull.toString();
            } else if (nonNull instanceof LocalDateTime) {
                value = ((LocalDateTime) nonNull).format(_TimeUtils.DATETIME_FORMATTER_6);
            } else if (nonNull instanceof LocalTime) {
                value = ((LocalTime) nonNull).format(_TimeUtils.TIME_FORMATTER_6);
            } else if (nonNull instanceof OffsetDateTime || nonNull instanceof ZonedDateTime) {
                value = _TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) nonNull);
            } else if (nonNull instanceof OffsetTime) {
                value = ((OffsetTime) nonNull).format(_TimeUtils.OFFSET_TIME_FORMATTER_6);
            } else if (nonNull instanceof Instant) {
                value = Long.toString(((Instant) nonNull).getEpochSecond());
            } else {
                value = nonNull.toString();
            }
        } else if (nonNull instanceof TemporalAmount) {
//            if (nonNull instanceof Period) {
//
//            } else if (!(nonNull instanceof Duration)) {
//                throw errorHandler.apply(type, nonNull);
//            } //TODO handle
            throw errorHandler.apply(type, dataType, nonNull, null);
        } else {
            value = nonNull.toString();
        }
        return value;
    }


}
