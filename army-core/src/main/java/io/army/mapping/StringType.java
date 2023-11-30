package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.mapping.array.StringArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;
import io.army.util._TimeUtils;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.function.BiFunction;

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
 * </p>
 *
 * @see TextType
 * @see MediumTextType
 * @since 1.0
 */
public final class StringType extends _ArmyBuildInMapping implements MappingType.SqlStringType {


    public static final StringType INSTANCE = new StringType();

    public static StringType from(final Class<?> fieldType) {
        if (fieldType != String.class) {
            throw errorJavaType(StringType.class, fieldType);
        }
        return INSTANCE;
    }

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
    public SqlType map(final ServerMeta meta) {
        return mapToSqlType(this, meta);
    }


    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public String convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return _convertToString(this, this.map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        return _convertToString(this, type, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String afterGet(final SqlType type, final MappingEnv env, final Object nonNull) {
        return _convertToString(this, type, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }

    @Deprecated
    public static String beforeBind(SqlType sqlType, final Object nonNull) {
        throw new UnsupportedOperationException();
    }

    static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
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

    static String _convertToString(final MappingType type, final SqlType sqlType, final Object nonNull,
                                   final BiFunction<MappingType, Object, ArmyException> errorHandler) {
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
        } else if (nonNull instanceof TemporalAmount) {
//            if (nonNull instanceof Period) {
//
//            } else if (!(nonNull instanceof Duration)) {
//                throw errorHandler.apply(type, nonNull);
//            } //TODO handle
            throw errorHandler.apply(type, nonNull);
        } else if (!(nonNull instanceof TemporalAccessor)) {
            throw errorHandler.apply(type, nonNull);
        } else if (nonNull instanceof LocalDate) {
            value = nonNull.toString();
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).format(_TimeUtils.getDatetimeFormatter(6));
        } else if (nonNull instanceof LocalTime) {
            value = ((LocalTime) nonNull).format(_TimeUtils.getTimeFormatter(6));
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).format(_TimeUtils.getDatetimeOffsetFormatter(6));
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).format(_TimeUtils.getDatetimeOffsetFormatter(6));
        } else if (nonNull instanceof OffsetTime) {
            value = ((OffsetTime) nonNull).format(_TimeUtils.getOffsetTimeFormatter(6));
        } else if (nonNull instanceof Year) {
            if (sqlType.database() == Database.MySQL) {
                value = nonNull.toString();
            } else {
                value = LocalDate.of(((Year) nonNull).getValue(), 1, 1).toString();
            }
        } else if (nonNull instanceof YearMonth) {
            final YearMonth v = (YearMonth) nonNull;
            value = LocalDate.of(v.getYear(), v.getMonth(), 1).toString();
        } else if (nonNull instanceof MonthDay) {
            final MonthDay v = (MonthDay) nonNull;
            value = LocalDate.of(1970, v.getMonth(), v.getDayOfMonth()).toString();
        } else if (nonNull instanceof Instant) {
            value = Long.toString(((Instant) nonNull).getEpochSecond());
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
