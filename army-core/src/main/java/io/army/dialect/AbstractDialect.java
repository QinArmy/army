package io.army.dialect;


import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.time.LocalTime;
import java.time.temporal.Temporal;

/**
 * this class is abstract implementation of {@link Dialect} .
 * created  on 2018/10/21.
 */
public abstract class AbstractDialect implements Dialect {


    @Override
    public <T extends IDomain> String tableDefinition(TableMeta<T> tableMeta) {
        StringBuilder builder = new StringBuilder(tableCreatePrefix())
                .append(" ")
                .append(tableMeta.tableName())
                .append(" (");


        // primary key column
        builder.append(fieldDefinition(tableMeta.primaryKey()));

        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldList()) {
            if (fieldMeta.isPrimary()) {
                continue;
            }
            builder.append(',')
                    .append(fieldDefinition(fieldMeta));
        }

        // primary key
        builder.append(',')
                .append(primaryDefinition(tableMeta));
        // TODO 设计 IndexMeta
        return builder.toString();
    }




    /*############################### sub class override method ####################################*/


    protected String tableCreatePrefix() {
        return "CREATE TABLE";
    }

    protected String primaryDefinition(TableMeta<?> tableMeta) {
        // String direction = tableMeta.primaryDesc() ? "DESC" : "ASC";
        //return String.format(" PRIMARY KEY(%s %s)", tableMeta.primaryKey().fieldName(), direction);
        return "";
    }

    /**
     * return field 的 column definition clause.
     * <p>
     * e.g {@code id BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'primary key'}
     * </p>
     *
     * @param field column meta
     * @return column definition clause
     */
    protected String fieldDefinition(FieldMeta<?, ?> field) {
        String format = columnFormat();
        return String.format(format,
                field.fieldName(),
                sqlDataType(field),
                sqlDefaultValue(field),
                field.comment()
        );
    }


    protected String sqlDataType(FieldMeta<?, ?> field) {
        return field.mappingType()
                .sqlType(this)
                .typeName(field.precision(), field.scale());
    }

    /**
     * @see #fieldDefinition(FieldMeta)
     */
    protected String sqlDefaultValue(FieldMeta<?, ?> field) {
        Class<?> javaType = field.javaType();
        String value;
        if (javaType == String.class) {
            value = StringUtils.quote(field.defaultValue());
        } else if (Temporal.class.isAssignableFrom(javaType)) {
            value = timeTypeDefault(field);
        } else {
            value = field.defaultValue();
        }
        return value;
    }

    /**
     * @see #sqlDefaultValue(FieldMeta)
     */
    protected String timeTypeDefault(FieldMeta<?, ?> field) {
        String value;
        switch (field.defaultValue()) {
            case IDomain.NOW:
                value = func().now(field.precision());
                break;
            case IDomain.SOURCE_DATE_TIME:
                value = sourceDateTime();
                break;
            case IDomain.SOURCE_DATE:
                value = sourceDate();
                break;
            case IDomain.MIDNIGHT:
                value = StringUtils.quote(LocalTime.MIDNIGHT.format(TimeUtils.TIME_FORMATTER));
                break;
            case IDomain.CURRENT_DATE:
                value = func().currentDate();
                break;
            case IDomain.CURRENT_TIME:
                value = func().currentTime(field.precision());
                break;
            default:
                value = field.defaultValue();
        }
        return value;
    }

    /**
     * @see #timeTypeDefault(FieldMeta)
     */
    protected String sourceDateTime() {
        return TimeUtils.toDateTime(0L).format(TimeUtils.DATE_TIME_FORMATTER);
    }

    /**
     * @see #timeTypeDefault(FieldMeta)
     */
    protected String sourceDate() {
        return TimeUtils.toDate(0L).format(TimeUtils.DATE_FORMATTER);
    }

    /*############################### sub class implements method ####################################*/

    protected abstract String columnFormat();


}
