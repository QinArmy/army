package io.army.dialect;

import io.army.SessionFactory;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.util.Assert;
import io.army.util.StringUtils;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractTableDDL implements TableDDL {

    private static final EnumSet<MappingMode> REQUIRED_MAPPING_MODE = EnumSet.of(
            MappingMode.SIMPLE,
            MappingMode.PARENT
    );

    protected final SQL sql;

    public AbstractTableDDL(SQL sql) {
        this.sql = sql;
    }

    @Override
    public final String quoteIfNeed(String identifier) {
        return sql.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return sql.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return sql.zoneId();
    }

    @Override
    public final SessionFactory sessionFactory() {
        return sql.sessionFactory();
    }


    /*################################## blow interfaces method ##################################*/

    @Override
    public final List<String> tableDefinition(TableMeta<?> tableMeta) {
        StringBuilder builder = new StringBuilder();
        // 1. header
        builder.append(createHeaderClause(tableMeta));
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
                    String.format("ALTER TABLE %s ADD COLUMN %s %s %s %s COMMENT '%s'"
                            , this.quoteIfNeed(tableMeta.tableName())
                            , this.quoteIfNeed(addFieldMeta.fieldName())
                            , this.dataTypeClause(addFieldMeta)
                            , this.nullableClause(addFieldMeta)
                            , this.defaultClause(addFieldMeta)
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

        for (FieldMeta<?, ?> changeFieldMeta : changeFieldMetas) {
            Assert.isTrue(changeFieldMeta.table() == tableMeta, () -> String.format(
                    "TableMeta[%s] and FieldMeta[%s] not match."
                    , tableMeta.tableName(), changeFieldMeta.fieldName()));

            String fieldName = this.quoteIfNeed(changeFieldMeta.fieldName());
            changeColumnList.add(
                    String.format("ALTER TABLE %s CHANGE COLUMN %s %s %s %s %s COMMENT '%s'"
                            , this.quoteIfNeed(tableMeta.tableName())
                            , fieldName
                            , fieldName
                            , this.dataTypeClause(changeFieldMeta)
                            , this.nullableClause(changeFieldMeta)
                            , this.defaultClause(changeFieldMeta)
                            , changeFieldMeta.comment()
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
                    .append(this.indexTypeClause(addIndexMeta))
                    .append(" (")
            ;

            appendIndexField(builder, addIndexMeta.fieldList());
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
                            , this.quoteIfNeed(tableMeta.tableName())
                            , indexName
                    )
            );
        }
        return Collections.unmodifiableList(dropIndexList);
    }



    /*####################################### below protected template method #################################*/


    protected abstract void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta);

    protected abstract String nonRequiredPropDefault(FieldMeta<?, ?> fieldMeta);

    protected abstract String defaultOfCreateAndUpdate(FieldMeta<?, ?> fieldMeta);

    protected abstract String dataTypeClause(FieldMeta<?, ?> fieldMeta);

    protected abstract boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta);



    /*####################################### below protected method #################################*/


    protected String primaryKeyClause(IndexMeta<?> indexMeta) {
        Supplier<String> message = () -> String.format("table[%s].key[%s] isn't primary key",
                indexMeta.table().tableName(), indexMeta.name());

        Assert.isTrue(indexMeta.isUnique(), message);

        Assert.isTrue(indexMeta.fieldList().size() == 1, message);

        IndexFieldMeta<?, ?> primaryKeyMeta = indexMeta.fieldList().get(0);
        Assert.isTrue(primaryKeyMeta.unique(), message);
        Assert.isTrue(TableMeta.ID.equals(primaryKeyMeta.fieldName()), message);

        return String.format("PRIMARY KEY(%s %s)",
                primaryKeyMeta.fieldName(), DDLUtils.ascOrDesc(primaryKeyMeta.fieldAsc()));
    }

    protected <T extends IDomain> String keyDefinitionClause(IndexMeta<T> indexMeta) {

        StringBuilder builder = new StringBuilder();

        if (indexMeta.isUnique()) {
            builder.append("UNIQUE");
        } else {
            builder.append("KEY");
        }
        builder.append(" ")
                .append(this.quoteIfNeed(indexMeta.name()))
                .append(" ")
                .append(indexTypeClause(indexMeta))
                .append(" (");

        appendIndexField(builder, indexMeta.fieldList());

        builder.append(")");
        return builder.toString();
    }

    protected String indexTypeClause(IndexMeta<?> indexMeta) {
        String text = "";
        if (StringUtils.hasText(indexMeta.type())) {
            text = "USING " + indexMeta.type();

        }
        return text;
    }


    protected String createHeaderClause(TableMeta<?> tableMeta) {
        return "CREATE TABLE " + this.quoteIfNeed(tableMeta.tableName()) + "(";
    }

    protected String columnDefinitionClause(FieldMeta<?, ?> fieldMeta) {
        return String.format("%s %s %s %s COMMENT '%s'",
                quoteIfNeed(fieldMeta.fieldName()),
                dataTypeClause(fieldMeta),
                nullableClause(fieldMeta),
                defaultClause(fieldMeta),
                fieldMeta.comment()
        );
    }

    protected String nullableClause(FieldMeta<?, ?> fieldMeta) {
        String clause;
        if (fieldMeta.nullable()) {
            clause = "NULL";
        } else {
            clause = "NOT NULL";
        }
        return clause;
    }

    protected final String defaultClause(FieldMeta<?, ?> fieldMeta) {
        String clause;
        if (hasDefaultClause(fieldMeta)) {
            clause = "DEFAULT ";
            if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())) {
                clause += requiredPropDefaultValue(fieldMeta);
            } else if (fieldMeta.table().discriminator() == fieldMeta) {
                clause += fieldMeta.table().discriminatorValue();
            } else {
                clause += nonRequiredPropDefault(fieldMeta);
            }

        } else {
            clause = "";
        }
        return clause;
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
                value = defaultOfCreateAndUpdate(fieldMeta);
                break;
            case TableMeta.VISIBLE:
                value = StringUtils.quote(fieldMeta.mappingType().nonNullTextValue(Boolean.TRUE));
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
     * debugSQL [1,n] col_name column_definition, eg. {@code id BIGINT NOT NULL DEFAULT COMMENT ''}
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
            // debugSQL one column definition
            columnDefinition = this.columnDefinitionClause(fieldMeta);
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

    private void appendKeyDefinition(StringBuilder builder, TableMeta<?> tableMeta) {
        List<String> definitionList = new ArrayList<>(tableMeta.indexCollection().size());
        // placeholder for id
        definitionList.add("");
        for (IndexMeta<?> indexMeta : tableMeta.indexCollection()) {
            if (indexMeta.isPrimaryKey()) {
                definitionList.set(0, primaryKeyClause(indexMeta));
            } else {
                definitionList.add(keyDefinitionClause(indexMeta));
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


    private <T extends IDomain> void appendIndexField(StringBuilder builder, List<IndexFieldMeta<T, ?>> indexMetaList) {
        Iterator<IndexFieldMeta<T, ?>> iterator = indexMetaList.iterator();
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
    }
}
