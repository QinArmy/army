package io.army.dialect.mysql;

import io.army.dialect.DDLUtils;
import io.army.meta.FieldMeta;

import java.time.*;

class MySQL80TableDDL extends MySQL57DDL {

    public MySQL80TableDDL(MySQL80Dialect mysql) {
        super(mysql);
    }


    @Override
    protected void doNowExpressionForDefaultClause(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {
        Class<?> javaClass = fieldMeta.javaType();
        if (javaClass == LocalDateTime.class) {
            MySQL57DDLUtils.assertTimePrecision(fieldMeta, database());
            MySQL57DDLUtils.nowFunc(fieldMeta, builder);
        } else if (javaClass == LocalDate.class) {
            builder.append("(CURRENT_DATE)");
        } else if (javaClass == LocalTime.class) {
            MySQL57DDLUtils.assertTimePrecision(fieldMeta, database());
            builder.append("(CURRENT_TIME");
            if (fieldMeta.precision() > 0) {
                builder.append(fieldMeta.precision());
            }
            builder.append(")");
        } else if (javaClass == Year.class) {
            builder.append("(YEAR(CURRENT_DATE))");
        } else if (javaClass == Month.class) {
            builder.append("(UPPER(MONTHNAME(CURRENT_DATE)))");
        } else if (javaClass == YearMonth.class) {
            builder.append("(DATE_FORMAT(CURRENT_DATE,'%Y-%m'))");
        } else if (javaClass == MonthDay.class) {
            builder.append("(DATE_FORMAT(CURRENT_DATE,'%m-%d'))");
        } else if (javaClass == DayOfWeek.class) {
            builder.append("(UPPER(DATE_FORMAT(CURRENT_DATE,'%W')))");
        } else {
            throw DDLUtils.createNowExpressionNotSupportJavaTypeException(fieldMeta, database());
        }
    }

    @Override
    protected void doDefaultExpression(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {
        String defaultExp = fieldMeta.defaultValue().trim();
        if (defaultExp.startsWith("(") && defaultExp.endsWith(")")) {
            builder.append(defaultExp);
        } else {
            super.doDefaultExpression(fieldMeta, builder);
        }
    }
}
