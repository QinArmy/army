package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.OracleDataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.type.LongString;
import io.army.util.TimeUtils;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.Temporal;

/**
 * <p>
 * This class representing the mapping from {@link String} to {@link SqlType}.
 * </p>
 *
 * @see String
 */
public final class StringType extends AbstractMappingType {


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
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.database()) {
            case MySQL:
                sqlDataType = MySqlType.VARCHAR;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.VARCHAR;
                break;
            case Oracle:
                sqlDataType = OracleDataType.VARCHAR2;
                break;
            case Firebird:
            case H2:
            default:
                throw noMappingError(meta);

        }
        return sqlDataType;
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        return beforeBind(sqlType, nonNull);
    }

    @Override
    public String afterGet(SqlType sqlType, MappingEnvironment env, final Object nonNull) {
        final String value;
        if (nonNull instanceof String) {
            value = (String) nonNull;
        } else if (nonNull instanceof LongString) {
            final LongString v = (LongString) nonNull;
            if (!(v.isString())) {
                throw errorValueForSqlType(sqlType, nonNull, null);
            }
            value = v.asString();
        } else {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }

    public static String beforeBind(SqlType sqlType, final Object nonNull) {
        final String value;
        if (nonNull instanceof String) {
            value = (String) nonNull;
        } else if (nonNull instanceof BigDecimal) {
            value = ((BigDecimal) nonNull).toPlainString();
        } else if (nonNull instanceof Number) {
            value = nonNull.toString();
        } else if (nonNull instanceof Enum) {
            value = ((Enum<?>) nonNull).name();
        } else if (!(nonNull instanceof Temporal)) {
            throw outRangeOfSqlType(sqlType, nonNull);
        } else if (nonNull instanceof LocalDate) {
            value = nonNull.toString();
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).format(TimeUtils.getDatetimeFormatter(6));
        } else if (nonNull instanceof LocalTime) {
            value = ((LocalTime) nonNull).format(TimeUtils.getTimeFormatter(6));
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).format(TimeUtils.getDatetimeOffsetFormatter(6));
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).format(TimeUtils.getDatetimeOffsetFormatter(6));
        } else if (nonNull instanceof OffsetTime) {
            value = ((OffsetTime) nonNull).format(TimeUtils.getOffsetTimeFormatter(6));
        } else if (nonNull instanceof YearMonth || nonNull instanceof Year) {
            value = nonNull.toString();
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return value;
    }


}
