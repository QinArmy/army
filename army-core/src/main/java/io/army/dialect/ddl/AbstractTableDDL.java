package io.army.dialect.ddl;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.meta.sqltype.SQLDataType;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractTableDDL implements TableDDL {


    @Override
    public final String tableDefinition(TableMeta<?> tableMeta) {
        StringBuilder builder = new StringBuilder();
        // 1. header
        appendDefinitionHeader(builder, tableMeta);
        builder.append("\n ");

        // 2. table column definition
        appendCreateDefinition(builder, tableMeta);
        builder.append("\n ");

        // 3. key definition
        String keyDefinition = keyDefinition(tableMeta);
        if (StringUtils.hasText(keyDefinition)) {
            builder.append(keyDefinition);
        }
        // 4. table options definition
        appendTableOptions(builder, tableMeta);

        builder.append(tableOptions(tableMeta));
        return builder.toString();
    }

    @Nonnull
    @Override
    public String addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        return null;
    }

    @Nonnull
    @Override
    public String modifyColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        return null;
    }

    /*####################################### below protected template method #################################*/


    /**
     * [1,n] key definition , eg. primary key (id)
     *
     * @return end with new line
     */
    @Nullable
    protected abstract String keyDefinition(TableMeta<?> tableMeta);

    /**
     * table options, eg : {@code ENGINE = InnoDB}
     *
     * @return not end with new line
     */
    @Nonnull
    protected abstract String tableOptions(TableMeta<?> tableMeta);


    @Nonnull
    protected abstract Dialect dialect();

    protected abstract void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta);



    /*####################################### below protected method #################################*/

    /**
     * @return eg. CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
     */
    @Nonnull
    protected void appendDefinitionHeader(StringBuilder builder, TableMeta<?> tableMeta) {
        builder.append("CREATE TABLE ")
                .append(tableMeta.tableName())
                .append("(");
    }

    protected void appendColumnDefinition(StringBuilder builder, FieldMeta<?, ?> fieldMeta) {
        builder.append(fieldMeta.fieldName())
                .append(" ")
                .append(dataType(fieldMeta))
                .append(" NOT NULL DEFAULT ")
                .append(sqlDefaultValue(fieldMeta))
                .append(" COMMON '")
                .append(fieldMeta.comment())
                .append("'")
        ;

    }

    protected final String sqlDefaultValue(FieldMeta<?, ?> fieldMeta) {
        assertSupportTimeType(fieldMeta);

        if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())) {
            return requiredMappingDefaultValue(fieldMeta);
        }
        SQLDataType sqlDataType = fieldMeta.mappingType().sqlType(dialect());
        String value;

        switch (sqlDataType.dataKind().family()) {
            case TEXT:
                value = textTypeDefaultValue(fieldMeta);
                value = StringUtils.quote(value);
                break;
            case NUMBER:
                value = numberTypeDefaultValue(fieldMeta);
                break;
            case DATE_TIME:
                value = timeTypeDefault(fieldMeta);
                break;
            default:
                value = otherDefaultValue(fieldMeta);
        }
        return value;
    }

    protected final String dataType(FieldMeta<?, ?> fieldMeta) {
        MappingType<?> mappingType = fieldMeta.mappingType();
        SQLDataType sqlDataType = mappingType.sqlType(dialect());
        return sqlDataType.typeName(fieldMeta.precision(), fieldMeta.scale());
    }


    protected String textTypeDefaultValue(FieldMeta<?, ?> fieldMeta) {
        return fieldMeta.defaultValue();
    }

    protected String numberTypeDefaultValue(FieldMeta<?, ?> fieldMeta) {
        return fieldMeta.defaultValue();
    }

    protected String otherDefaultValue(FieldMeta<?, ?> fieldMeta) {
        return fieldMeta.javaType() == String.class
                ? StringUtils.quote(fieldMeta.defaultValue())
                : fieldMeta.defaultValue();
    }

    @SuppressWarnings("unchecked")
    protected final String requiredMappingDefaultValue(FieldMeta<?, ?> requiredFieldMeta) {
        String value;
        switch (requiredFieldMeta.propertyName()) {
            case TableMeta.ID:
                value = "";
                break;
            case TableMeta.CREATE_TIME:
            case TableMeta.UPDATE_TIME:
                value = requiredFieldMeta.precision() >= 0
                        ? dialect().now(requiredFieldMeta.precision())
                        : dialect().now();
                break;
            case TableMeta.VISIBLE:
                value = requiredFieldMeta.mappingType().toSql(Boolean.TRUE).toString();
                break;
            case TableMeta.VERSION:
                value = "0";
                break;
            default:
                throw new IllegalArgumentException(String.format("entity[%s] mapping prop[%s] isn't  required ",
                        requiredFieldMeta.table().javaType(),
                        requiredFieldMeta.propertyName()))
                        ;

        }
        return value;
    }


    /**
     * @see #sqlDefaultValue(FieldMeta)
     */
    protected final String timeTypeDefault(FieldMeta<?, ?> field) {
        String value;
        switch (field.defaultValue()) {
            case IDomain.NOW:
                value = field.precision() >= 0 ? dialect().now(field.precision()) : dialect().now();
                break;
            case IDomain.SOURCE_DATE_TIME:
                value = TimeUtils.SOURCE_DATE_TIME.format(TimeUtils.SIX_FRACTION_DATE_TIME_FORMATTER);
                value = StringUtils.quote(value);
                break;
            case IDomain.SOURCE_DATE:
                value = TimeUtils.SOURCE_DATE.format(TimeUtils.DATE_FORMATTER);
                value = StringUtils.quote(value);
                break;
            case IDomain.MIDNIGHT:
                value = StringUtils.quote(LocalTime.MIDNIGHT.format(TimeUtils.TIME_FORMATTER));
                value = StringUtils.quote(value);
                break;
            case IDomain.CURRENT_DATE:
                value = dialect().currentDate();
                break;
            case IDomain.CURRENT_TIME:
                value = field.precision() >= 0 ? dialect().currentTime(field.precision()) : dialect().currentTime();
                break;
            default:
                // TODO zoro handle expression
                value = field.defaultValue();
        }
        return value;
    }


    protected final void assertSupportTimeType(FieldMeta<?, ?> fieldMeta) {
        if (isZonedTimeType(fieldMeta.javaType()) && !dialect().supportZoneId()) {
            throw new MetaException(ErrorCode.META_ERROR, String.format(
                    "dialect[%s] not support time type with zone,entity[%s] mapping property[%s].",
                    dialect().name(),
                    fieldMeta.table().javaType().getName(),
                    fieldMeta.javaType().getName()
            ));
        }
    }

    protected final boolean isZonedTimeType(Class<?> timeTypeClass) {
        return timeTypeClass == ZonedDateTime.class
                || timeTypeClass == OffsetDateTime.class;
    }

    /*####################################### below private method #################################*/


    /**
     * create [1,n] col_name column_definition, eg. {@code id BIGINT NOT NULL DEFAULT COMMENT ''}
     */
    private <T extends IDomain> void appendCreateDefinition(StringBuilder builder, TableMeta<T> tableMeta) {

        Iterator<FieldMeta<T, ?>> iterator = tableMeta.fieldCollection().iterator();
        for (FieldMeta<T, ?> fieldMeta; iterator.hasNext(); ) {
            fieldMeta = iterator.next();
            appendColumnDefinition(builder, fieldMeta);
            if (iterator.hasNext()) {
                builder.append(",\n");
            }
        }


    }

}
