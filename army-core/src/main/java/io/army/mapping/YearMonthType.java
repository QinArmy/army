package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * @see {@link YearMonth}
 */
public final class YearMonthType extends _ArmyNoInjectionMapping {

    public static final YearMonthType INSTANCE = new YearMonthType();

    public static YearMonthType from(final Class<?> fieldType) {
        if (fieldType != YearMonth.class) {
            throw errorJavaType(YearMonthType.class, fieldType);
        }
        return INSTANCE;
    }


    private YearMonthType() {
    }

    @Override
    public Class<?> javaType() {
        return YearMonth.class;
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return LocalDateType.INSTANCE.map(meta);
    }

    @Override
    public LocalDate beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        final YearMonth value;
        if (nonNull instanceof YearMonth) {
            value = (YearMonth) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = YearMonth.parse((String) nonNull);
            } catch (DateTimeException e) {
                throw valueOutRange(sqlType, nonNull, e);
            }
        } else {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return LocalDate.of(value.getYear(), value.getMonth(), 1);
    }

    @Override
    public YearMonth afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof LocalDate)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final LocalDate v = (LocalDate) nonNull;
        return YearMonth.of(v.getYear(), v.getMonth());
    }


}
