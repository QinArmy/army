package io.army.dialect.ddl;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class AbstractTableDDL implements TableDDL {


    @Override
    public final String tableDefinition(TableMeta<?> tableMeta) {
        StringBuilder builder = new StringBuilder();
        // 1. header
        appendDefinitionHeader(builder, tableMeta);
        builder.append(" \n");

        // 2. table column definition
        appendCreateDefinition(builder, tableMeta);

        // 3. key definition
        appendKeyDefinition(builder, tableMeta);

        builder.append("\n)");
        // 4. table options definition
        appendTableOptions(builder, tableMeta);

        builder.append(";");
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




    @Nonnull
    protected abstract Dialect dialect();

    protected abstract void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta);



    /*####################################### below protected method #################################*/


    protected final void appendKeyDefinition(StringBuilder builder, TableMeta<?> tableMeta) {
        List<String> definitionList = new ArrayList<>(tableMeta.indexCollection().size());
        // placeholder for id
        definitionList.add("");
        for (IndexMeta<?> indexMeta : tableMeta.indexCollection()) {
            if (indexMeta.isPrimaryKey()) {
                definitionList.set(0, primaryKeyDefinition(indexMeta));
            } else {
                definitionList.add(doKeyDefinition(indexMeta));
            }
        }
        for (String keyDefinition : definitionList) {
            builder.append(",\n")
                    .append(keyDefinition);
        }
    }

    protected String primaryKeyDefinition(IndexMeta<?> indexMeta) {
        Assert.isTrue(indexMeta.isUnique(), "");
        Assert.isTrue(indexMeta.fieldList().size() == 1, "");

        IndexFieldMeta<?, ?> primaryKeyMeta = indexMeta.fieldList().get(0);
        Assert.isTrue(primaryKeyMeta.isUnique(), "");
        Assert.isTrue(TableMeta.ID.equals(primaryKeyMeta.fieldName()), "");

        return String.format("PRIMARY KEY(%s %s)",
                primaryKeyMeta.fieldName(), ascOrDesc(primaryKeyMeta.fieldAsc()));
    }

    protected <T extends IDomain> String doKeyDefinition(IndexMeta<T> indexMeta) {
        StringBuilder builder = new StringBuilder();
        builder.append("KEY ")
                .append(indexMeta.name())
                .append(" ");

        if (StringUtils.hasText(indexMeta.type())) {
            builder.append(indexMeta.type())
                    .append(" ");
        }
        builder.append("(");
        Iterator<IndexFieldMeta<T, ?>> iterator = indexMeta.fieldList().iterator();
        for (IndexFieldMeta<T, ?> indexFieldMeta; iterator.hasNext(); ) {
            indexFieldMeta = iterator.next();

            builder.append(indexFieldMeta.fieldName())
                    .append(" ")
                    .append(ascOrDesc(indexFieldMeta.fieldAsc()))
            ;

            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    protected final String ascOrDesc(boolean asc) {
        return asc ? "ASC" : "DESC";
    }


    /**
     * eg. CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
     */
    protected void appendDefinitionHeader(StringBuilder builder, TableMeta<?> tableMeta) {
        builder.append("CREATE TABLE ")
                .append(tableMeta.tableName())
                .append("(");
    }

    protected String columnDefinition(FieldMeta<?, ?> fieldMeta) {
        return String.format("%s %s NOT NULL DEFAULT %s COMMON '%s'",
                fieldMeta.fieldName(),
                dataType(fieldMeta),
                sqlDefaultValue(fieldMeta),
                fieldMeta.comment()
        );
    }

    protected final String sqlDefaultValue(FieldMeta<?, ?> fieldMeta) {
        assertSupportTimeType(fieldMeta);

        if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())) {
            return requiredMappingDefaultValue(fieldMeta);
        }
        return sqlDefaultValueByType(fieldMeta);
    }

    protected final String sqlDefaultValueByType(FieldMeta<?, ?> fieldMeta) {
       /* SQLDataType sqlDataType = fieldMeta.mappingType().sqlType(dialect());
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
        }*/
        // return value;
        return null;
    }

    protected final String dataType(FieldMeta<?, ?> fieldMeta) {
      /*  MappingType<?> mappingType = fieldMeta.mappingType();
        SQLDataType sqlDataType = mappingType.sqlType(dialect());
        if (!sqlDataType.supportDialect(dialect())) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "Entity[%s] property[%s] mapping[%s] dialect[%s] is supported by SQLDataType[%s]",
                    fieldMeta.table().javaType().getName(),
                    fieldMeta.propertyName(),
                    fieldMeta.fieldName(),
                    dialect().name(),
                    sqlDataType.typeName(fieldMeta.precision(), fieldMeta.scale())
            );
        }
        return sqlDataType.typeName(fieldMeta.precision(), fieldMeta.scale());*/
        return null;
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
                value = sqlDefaultValueByType(requiredFieldMeta);
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
        Iterator<String> iterator = createColumnDefinitionList(tableMeta).iterator();
        for (String columnDefinition; iterator.hasNext(); ) {
            columnDefinition = iterator.next();
            builder.append(columnDefinition);
            if (iterator.hasNext()) {
                builder.append(",\n");
            }
        }

    }

    private List<String> createColumnDefinitionList(TableMeta<?> tableMeta) {
        List<String> propList = new ArrayList<>(tableMeta.fieldCollection().size());

        final int requiredSize = tableMeta.immutable() ? TableMeta.DOMAIN_PROPS.size() : TableMeta.VERSION_PROPS.size();

        for (int i = 0; i < requiredSize; i++) {
            propList.add("");
        }
        List<String> notRequiredPropList = new ArrayList<>(tableMeta.fieldCollection().size() - requiredSize);

        String columnDefinition;
        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            columnDefinition = this.columnDefinition(fieldMeta);
            switch (fieldMeta.propertyName()) {
                case TableMeta.ID:
                    propList.set(0, columnDefinition);
                    break;
                case TableMeta.CREATE_TIME:
                    propList.set(1, columnDefinition);
                    break;
                case TableMeta.VISIBLE:
                    propList.set(2, columnDefinition);
                    break;
                case TableMeta.UPDATE_TIME:
                    propList.set(3, columnDefinition);
                    break;
                case TableMeta.VERSION:
                    propList.set(4, columnDefinition);
                    break;
                default:
                    notRequiredPropList.add(columnDefinition);
            }
        }
        propList.addAll(notRequiredPropList);
        return Collections.unmodifiableList(propList);
    }

}
