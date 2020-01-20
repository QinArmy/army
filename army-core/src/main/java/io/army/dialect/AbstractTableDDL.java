package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.schema.migration.TableDDL;
import io.army.util.ArrayUtils;
import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractTableDDL implements TableDDL {


    private static final Set<String> SPECIFIED_COLUMN_FUNC_SET = ArrayUtils.asUnmodifiableSet(
            IDomain.NOW
    );

    private static final EnumSet<MappingMode> REQUIRED_MAPPING_MODE = EnumSet.of(
            MappingMode.SIMPLE,
            MappingMode.PARENT
    );


    @Override
    public final String tableDefinition(TableMeta<?> tableMeta) {
        StringBuilder builder = new StringBuilder();
        // 1. header
        appendDefinitionHeader(builder, tableMeta);
        builder.append(" \n");

        // 2. table column definition
        appendColumnDefinitions(builder, tableMeta);

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

    @Override
    public String addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas) {
        return null;
    }

    @Override
    public String modifyIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> indexMetas) {
        return null;
    }

    @Override
    public String dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames) {
        return null;
    }


    /*####################################### below protected template method #################################*/


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
        Iterator<String> iterator = definitionList.iterator();
        for (; iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(",\n");
            }
        }
    }

    protected String primaryKeyDefinition(IndexMeta<?> indexMeta) {
        Supplier<String> message = () -> String.format("table[%s].key[%s] isn't primary key",
                indexMeta.table().tableName(), indexMeta.name());

        Assert.isTrue(indexMeta.isUnique(), message);

        Assert.isTrue(indexMeta.fieldList().size() == 1, message);

        IndexFieldMeta<?, ?> primaryKeyMeta = indexMeta.fieldList().get(0);
        Assert.isTrue(primaryKeyMeta.isUnique(), message);
        Assert.isTrue(TableMeta.ID.equals(primaryKeyMeta.fieldName()), message);

        return String.format("PRIMARY KEY(%s %s)",
                primaryKeyMeta.fieldName(), ascOrDesc(primaryKeyMeta.fieldAsc()));
    }

    protected <T extends IDomain> String doKeyDefinition(IndexMeta<T> indexMeta) {

        StringBuilder builder = new StringBuilder();

        if (indexMeta.isUnique()) {
            builder.append("UNIQUE");
        } else {
            builder.append("KEY");
        }
        builder.append(" ")
                .append(indexMeta.name())
                .append(" ");

        if (StringUtils.hasText(indexMeta.type())) {
            builder.append("USING ")
                    .append(indexMeta.type())
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

    protected final String ascOrDesc(Boolean asc) {
        String text;
        if (asc == null) {
            text = "";
        } else if (asc) {
            text = "ASC";
        } else {
            text = "DESC";
        }
        return text;
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
        String defaultKey;
        if (fieldMeta.isPrimary()) {
            defaultKey = "";
        } else {
            defaultKey = "DEFAULT";
        }
        return String.format("%s %s NOT NULL %s %s COMMENT '%s'",
                fieldMeta.fieldName(),
                dataTypeText(fieldMeta),
                defaultKey,
                sqlDefaultValue(fieldMeta),
                fieldMeta.comment()
        );
    }

    protected final String sqlDefaultValue(FieldMeta<?, ?> fieldMeta) {
        String value;

        if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())) {
            value = requiredPropDefaultValue(fieldMeta);
        } else if (StringUtils.hasText(fieldMeta.defaultValue())) {
            if (isSpecifiedFunction(fieldMeta.defaultValue())) {
                value = nowFunc(fieldMeta.defaultValue(), fieldMeta);
            } else if (isSourceTime(fieldMeta.defaultValue())) {
                value = sourceTimeValue(fieldMeta);
            } else if (fieldMeta.mappingType().isTextValue(fieldMeta.defaultValue())) {
                value = fieldMeta.defaultValue();
            } else {
                throw new MetaException(ErrorCode.META_ERROR, "Entity[%s].column[%s] default value error",
                        fieldMeta.table().javaType().getName(), fieldMeta.fieldName());
            }
        } else if (DialectUtils.TEXT_JDBC_TYPE.contains(fieldMeta.mappingType().jdbcType())) {
            value = "";
        } else {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s].column[%s] default value required",
                    fieldMeta.table().javaType().getName(), fieldMeta.fieldName());
        }
        if (isNeedQuote(fieldMeta)) {
            value = StringUtils.quote(value);
        }
        return value;
    }

    //  dialect implements
    protected abstract String nowFunc(String func, FieldMeta<?, ?> fieldMeta);

    protected final String sourceTimeValue(FieldMeta<?, ?> fieldMeta) {
        String defaultValue = fieldMeta.defaultValue();
        String timeValue;
        switch (defaultValue) {
            case IDomain.SOURCE_DATE_TIME:
                timeValue = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), zoneId())
                        .format(TimeUtils.DATE_TIME_FORMATTER);
                break;
            case IDomain.SOURCE_DATE:
                timeValue = TimeUtils.toDate(0L)
                        .format(TimeUtils.DATE_FORMATTER);
                break;
            default:
                throw new RuntimeException(String.format("Entity[%s].column[%s] default value isn't source time",
                        fieldMeta.table().tableName(), fieldMeta.fieldName()));
        }
        return StringUtils.quote(timeValue);
    }

    protected final boolean isNeedQuote(FieldMeta<?, ?> fieldMeta) {
        return !fieldMeta.isPrimary()
                && !TableMeta.CREATE_TIME.equals(fieldMeta.propertyName())
                && !TableMeta.UPDATE_TIME.equals(fieldMeta.propertyName())
                && !IDomain.NOW.equals(fieldMeta.defaultValue())
                && DialectUtils.QUOTE_JDBC_TYPE.contains(fieldMeta.mappingType().jdbcType());
    }


    protected ZoneId zoneId() {
        return ZoneId.systemDefault();
    }


    protected abstract String dataTypeText(FieldMeta<?, ?> fieldMeta);


    protected final boolean isSpecifiedFunction(String defaultValue) {
        return SPECIFIED_COLUMN_FUNC_SET.contains(defaultValue);
    }

    protected final boolean isSourceTime(String defaultValue) {
        return IDomain.SOURCE_DATE_TIME.equals(defaultValue)
                || IDomain.SOURCE_DATE.equals(defaultValue);
    }



    /*################################## blow private method ##################################*/


    private String requiredPropDefaultValue(FieldMeta<?, ?> fieldMeta) {
        String value;
        switch (fieldMeta.propertyName()) {
            case TableMeta.ID:
                value = "";
                break;
            case TableMeta.CREATE_TIME:
            case TableMeta.UPDATE_TIME:
                value = nowFunc(IDomain.NOW, fieldMeta);
                break;
            case TableMeta.VISIBLE:
                value = fieldMeta.mappingType().nullSafeTextValue(Boolean.TRUE);
                break;
            case TableMeta.VERSION:
                value = IDomain.ZERO;
                break;
            default:
                throw new RuntimeException(String.format("Entity[%s].prop[%s] isn't required prop",
                        fieldMeta.table().tableName(), fieldMeta.propertyName()));
        }
        return value;
    }


    /**
     * create [1,n] col_name column_definition, eg. {@code id BIGINT NOT NULL DEFAULT COMMENT ''}
     */
    private <T extends IDomain> void appendColumnDefinitions(StringBuilder builder, TableMeta<T> tableMeta) {
        Iterator<String> iterator = createColumnDefinitionList(tableMeta).iterator();
        for (String columnDefinition; iterator.hasNext(); ) {
            columnDefinition = iterator.next();
            builder.append(columnDefinition);
            builder.append(",\n");
        }

    }

    private List<String> createColumnDefinitionList(TableMeta<?> tableMeta) {
        List<String> propList = new ArrayList<>(tableMeta.fieldCollection().size());

        final int requiredSize = requiredSize(tableMeta);

        for (int i = 0; i < requiredSize; i++) {
            propList.add("");
        }

        String columnDefinition;
        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            // create one column definition
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
                    propList.add(columnDefinition);
            }
        }
        return Collections.unmodifiableList(propList);
    }

    private int requiredSize(TableMeta<?> tableMeta) {
        int size;
        if (REQUIRED_MAPPING_MODE.contains(tableMeta.mappingMode())) {
            size = tableMeta.immutable() ? TableMeta.DOMAIN_PROPS.size() : TableMeta.VERSION_PROPS.size();
        } else {
            size = 1;
        }
        return size;
    }


}
