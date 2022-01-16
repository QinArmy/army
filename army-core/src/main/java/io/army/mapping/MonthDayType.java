package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.MonthDay;

public final class MonthDayType extends _ArmyNoInjectionMapping {

    public static final MonthDayType INSTANCE = new MonthDayType();

    public static MonthDayType build(Class<?> javaType) {
        if (javaType != MonthDay.class) {
            throw createNotSupportJavaTypeException(YearMonthType.class, javaType);
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
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        return LocalDateType.INSTANCE.sqlType(serverMeta);
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof MonthDay)) {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return nonNull;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof LocalDate)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        final LocalDate v = (LocalDate) nonNull;
        return MonthDay.of(v.getMonth(), v.getDayOfMonth());
    }


}
