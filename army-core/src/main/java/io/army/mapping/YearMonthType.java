package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.YearMonth;

public final class YearMonthType extends _ArmyNoInjectionMapping {

    public static final YearMonthType INSTANCE = new YearMonthType();

    public static YearMonthType build(Class<?> javaType) {
        if (javaType != YearMonth.class) {
            throw createNotSupportJavaTypeException(YearMonthType.class, javaType);
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
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        return LocalDateType.INSTANCE.sqlType(serverMeta);
    }

    @Override
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof YearMonth)) {
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
        return YearMonth.of(v.getYear(), v.getMonth());
    }


}
