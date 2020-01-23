package io.army.dialect;

import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.util.Assert;
import io.army.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractTableDDL implements TableDDL {

    private static final EnumSet<MappingMode> REQUIRED_MAPPING_MODE = EnumSet.of(
            MappingMode.SIMPLE,
            MappingMode.PARENT
    );


    /*################################## blow interfaces method ##################################*/

    @Override
    public final List<String> tableDefinition(TableMeta<?> tableMeta) {
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

        return Collections.singletonList(builder.toString());
    }

    @Nonnull
    @Override
    public final List<String> addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {
        List<String> addColumnList = new ArrayList<>(addFieldMetas.size());

        for (FieldMeta<?, ?> addFieldMeta : addFieldMetas) {
            Assert.isTrue(addFieldMeta.table() == tableMeta, () -> String.format(
                    "TableMeta[%s] and FieldMeta[%s] not match."
                    , tableMeta.tableName(), addFieldMeta.fieldName()));

            addColumnList.add(
                    String.format("ALTER TABLE %s ADD COLUMN %s %s NULL %s %s COMMENT '%s'"
                            , this.quoteIfNeed(tableMeta.tableName())
                            , this.quoteIfNeed(addFieldMeta.fieldName())
                            , this.dataTypeText(addFieldMeta)
                            , DDLUtils.nullable(addFieldMeta.isNullable())
                            , this.sqlDefaultValue(addFieldMeta)
                            , addFieldMeta.comment()
                    )
            );

        }
        return Collections.unmodifiableList(addColumnList);
    }

    @Nonnull
    @Override
    public List<String> changeColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> changeFieldMetas) {
        List<String> changeColumnList = new ArrayList<>(changeFieldMetas.size());

        for (FieldMeta<?, ?> addFieldMeta : changeFieldMetas) {
            Assert.isTrue(addFieldMeta.table() == tableMeta, () -> String.format(
                    "TableMeta[%s] and FieldMeta[%s] not match."
                    , tableMeta.tableName(), addFieldMeta.fieldName()));
            String fieldName = this.quoteIfNeed(addFieldMeta.fieldName());
            changeColumnList.add(
                    String.format("ALTER TABLE %s CHANGE COLUMN %s %s %s %s NULL %s COMMENT '%s'"
                            , this.quoteIfNeed(tableMeta.tableName())
                            , fieldName
                            , fieldName
                            , this.dataTypeText(addFieldMeta)
                            , DDLUtils.nullable(addFieldMeta.isNullable())
                            , this.sqlDefaultValue(addFieldMeta)
                            , addFieldMeta.comment()
                    )
            );

        }
        return Collections.unmodifiableList(changeColumnList);
    }

    @Override
    public List<String> addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> addIndexMetas) {
        List<String> addIndexList = new ArrayList<>(addIndexMetas.size());

        for (IndexMeta<?> addIndexMeta : addIndexMetas) {
            Assert.isTrue(addIndexMeta.table() == tableMeta, () -> String.format(
                    "TableMeta[%s] and Index[%s] not match."
                    , tableMeta.tableName(), addIndexMeta.name()));

            StringBuilder builder = new StringBuilder();
            builder.append("ALTER TABLE ")
                    .append(this.quoteIfNeed(tableMeta.tableName()))
                    .append(" ADD INDEX ")
                    .append(this.quoteIfNeed(addIndexMeta.name()))
                    .append(" ")
                    .append(this.indexTypeText(addIndexMeta))
                    .append(" (")
            ;

            appendIndexField(builder,addIndexMeta.fieldList());
            builder.append(")");

            addIndexList.add(builder.toString());
        }
        return Collections.unmodifiableList(addIndexList);
    }

    @Override
    public List<String> dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames) {
        List<String> dropIndexList = new ArrayList<>(indexNames.size());

        for (String indexName : indexNames) {
            dropIndexList.add(
                    String.format("ALTER TABLE %s DROP INDEX %s"
                            ,this.quoteIfNeed(tableMeta.tableName())
                            ,indexName
                    )
            );
        }
        return Collections.unmodifiableList(dropIndexList);
    }



    /*####################################### below protected template method #################################*/


    protected abstract void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta);

    protected abstract String nonRequiredPropDefault(FieldMeta<?, ?> fieldMeta);

    protected abstract String createUpdateDefault(FieldMeta<?, ?> fieldMeta);

    protected abstract String dataTypeText(FieldMeta<?, ?> fieldMeta);



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
                primaryKeyMeta.fieldName(), DDLUtils.ascOrDesc(primaryKeyMeta.fieldAsc()));
    }

    protected <T extends IDomain> String doKeyDefinition(IndexMeta<T> indexMeta) {

        StringBuilder builder = new StringBuilder();

        if (indexMeta.isUnique()) {
            builder.append("UNIQUE");
        } else {
            builder.append("KEY");
        }
        builder.append(" ")
                .append(this.quoteIfNeed(indexMeta.name()))
                .append(" ")
                .append(indexTypeText(indexMeta))
                .append(" (");

        Iterator<IndexFieldMeta<T, ?>> iterator = indexMeta.fieldList().iterator();
        for (IndexFieldMeta<T, ?> indexFieldMeta; iterator.hasNext(); ) {
            indexFieldMeta = iterator.next();

            builder.append(this.quoteIfNeed(indexFieldMeta.fieldName()))
                    .append(" ")
                    .append(DDLUtils.ascOrDesc(indexFieldMeta.fieldAsc()))
            ;

            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    protected String indexTypeText(IndexMeta<?> indexMeta) {
        String text = "";
        if (StringUtils.hasText(indexMeta.type())) {
            text = "USING " + indexMeta.type();

        }
        return text;
    }


    /**
     * eg. CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
     */
    protected void appendDefinitionHeader(StringBuilder builder, TableMeta<?> tableMeta) {
        builder.append("CREATE TABLE ")
                .append(this.quoteIfNeed(tableMeta.tableName()))
                .append("(");
    }

    protected String columnDefinition(FieldMeta<?, ?> fieldMeta) {
        String defaultKey, nullable;
        if (fieldMeta.isPrimary()) {
            defaultKey = "";
        } else {
            defaultKey = "DEFAULT";
        }
        if (fieldMeta.isNullable()) {
            nullable = "";
        } else {
            nullable = "NOT";
        }
        return String.format("%s %s %s NULL %s %s COMMENT '%s'",
                quoteIfNeed(fieldMeta.fieldName()),
                dataTypeText(fieldMeta),
                nullable,
                defaultKey,
                sqlDefaultValue(fieldMeta),
                fieldMeta.comment()
        );
    }


    /*################################## blow private method ##################################*/

    private String sqlDefaultValue(FieldMeta<?, ?> fieldMeta) {
        String value;

        if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())) {
            value = requiredPropDefaultValue(fieldMeta);
        } else {
            value = nonRequiredPropDefault(fieldMeta);
        }
        return value;
    }

    private String requiredPropDefaultValue(FieldMeta<?, ?> fieldMeta) {
        String value;
        switch (fieldMeta.propertyName()) {
            case TableMeta.ID:
                value = "";
                break;
            case TableMeta.CREATE_TIME:
            case TableMeta.UPDATE_TIME:
                value = createUpdateDefault(fieldMeta);
                break;
            case TableMeta.VISIBLE:
                value = fieldMeta.mappingType().nullSafeTextValue(Boolean.TRUE);
                break;
            case TableMeta.VERSION:
                value = IDomain.ONE;
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
        Assert.isTrue(propList.size() == tableMeta.fieldCollection().size(), "");
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



    private <T extends IDomain> void appendIndexField(StringBuilder builder ,List<IndexFieldMeta<T, ?>> indexMetaList){
        Iterator<IndexFieldMeta<T, ?>> iterator =indexMetaList.iterator();
        for(IndexFieldMeta<T, ?> indexFieldMeta;iterator.hasNext();){
            indexFieldMeta = iterator.next();

            builder.append(this.quoteIfNeed(indexFieldMeta.fieldName()))
                    .append(" ")
                    .append(DDLUtils.ascOrDesc(indexFieldMeta.fieldAsc()))
            ;
            if(iterator.hasNext()){
                builder.append(",");
            }
        }
    }
}
