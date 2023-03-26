package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

/**
 * @see MonthDay
 */
public final class MonthDayType extends _ArmyNoInjectionMapping {

    public static final MonthDayType INSTANCE = new MonthDayType();

    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter(Locale.ENGLISH);


    public static MonthDayType from(final Class<?> fieldType) {
        if (fieldType != MonthDay.class) {
            throw errorJavaType(MonthDayType.class, fieldType);
        }
        return INSTANCE;
    }


    private MonthDayType() {
    }

    @Override
    public Class<?> javaType() {
        return MonthDay.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        return LocalDateType.INSTANCE.map(meta);
    }

    @Override
    public LocalDate beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        final MonthDay value;
        if (nonNull instanceof MonthDay) {
            value = (MonthDay) nonNull;
        } else if (nonNull instanceof String) {
            try {
                final String v = (String) nonNull;
                if (v.startsWith("--")) {
                    value = MonthDay.parse(v);
                } else {
                    value = MonthDay.parse(v, FORMATTER);
                }
            } catch (DateTimeException e) {
                throw valueOutRange(type, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(type, nonNull);
        }
        return LocalDate.of(1970, value.getMonth(), value.getDayOfMonth());
    }

    @Override
    public MonthDay afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        if (!(nonNull instanceof LocalDate)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        final LocalDate v = (LocalDate) nonNull;
        return MonthDay.of(v.getMonth(), v.getDayOfMonth());
    }


}
