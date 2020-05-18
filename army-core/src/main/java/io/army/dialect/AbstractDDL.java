package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.modelgen.MetaConstant;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class AbstractDDL extends AbstractSQL implements DDL {

    private Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> defaultFunctionMap;


    public AbstractDDL(InnerDialect dialect) {
        super(dialect);
    }

    /*################################## blow interfaces method ##################################*/

    @Override
    public final List<String> createTable(TableMeta<?> tableMeta) {
        DDLContext context = new TableDDLContext(this.dialect, tableMeta, defaultFunctionMap());
        StringBuilder tableBuilder = context.sqlBuilder();
        // 1. create clause
        createClause(context);
        tableBuilder.append(" (\n");
        // 2. column definition clause
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : DDLUtils.sortFieldMetaCollection(tableMeta)) {
            if (index > 0) {
                tableBuilder.append(",\n");
            }
            columnDefinitionClause(fieldMeta, context);
            index++;
        }

        final boolean independentIndexDefinition = independentIndexDefinition();

        if (independentIndexDefinition) {
            tableBuilder.append(" )\n");
        }
        // 3. index definition
        index = 0;
        for (IndexMeta<?> indexMeta : DDLUtils.sortIndexMetaCollection(tableMeta)) {
            if (index > 0) {
                indexSuffix(context);
            } else if (!independentIndexDefinition) {
                tableBuilder.append(",\n");
            }

            indexDefinitionClause(indexMeta, context);
            index++;
        }

        if (!independentIndexDefinition) {
            tableBuilder.append(" )\n");
        }
        // 4. tableMeta options definition
        tableOptionsClause(context);

        return context.build();
    }

    @Override
    public final List<String> addColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> addFieldMetas) {

        final DDLContext context = new ColumnDDLContext(this.dialect, tableMeta, defaultFunctionMap());

        for (FieldMeta<?, ?> fieldMeta : addFieldMetas) {

            Assert.isTrue(fieldMeta.tableMeta() == tableMeta, () -> String.format(
                    "TableMeta[%s] then FieldMeta[%s] not match."
                    , tableMeta, fieldMeta));

            doAddColumn(fieldMeta, context);
            context.append(context.sqlBuilder().toString());
            context.resetBuilder();
        }
        return context.build();
    }

    @Override
    public final List<String> changeColumn(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> changeFieldMetas) {
        final DDLContext context = new ColumnDDLContext(this.dialect, tableMeta, defaultFunctionMap());

        for (FieldMeta<?, ?> fieldMeta : changeFieldMetas) {

            Assert.isTrue(fieldMeta.tableMeta() == tableMeta, () -> String.format(
                    "TableMeta[%s] then FieldMeta[%s] not match."
                    , tableMeta, fieldMeta));

            doChangeColumn(fieldMeta, context);
            context.append(context.sqlBuilder().toString());
            context.resetBuilder();
        }
        return context.build();
    }

    @Override
    public final List<String> addIndex(TableMeta<?> tableMeta, Collection<IndexMeta<?>> addIndexMetas) {
        final DDLContext context = new IndexDDLContext(this.dialect, tableMeta, defaultFunctionMap());
        int index = 0;
        for (IndexMeta<?> indexMeta : addIndexMetas) {
            Assert.isTrue(indexMeta.table() == tableMeta, () -> String.format(
                    "TableMeta[%s] then Index[%s] not match."
                    , tableMeta.tableName(), indexMeta.name()));
            if (index > 0) {
                context.resetBuilder();
            }
            indexDefinitionClause(indexMeta, context);
            context.append(context.sqlBuilder().toString());

            index++;
        }
        return context.build();
    }

    @Override
    public final List<String> dropIndex(TableMeta<?> tableMeta, Collection<String> indexNames) {
        final DDLContext context = new IndexDDLContext(this.dialect, tableMeta, defaultFunctionMap());
        int index = 0;
        for (String indexName : indexNames) {
            if (index > 0) {
                context.resetBuilder();
            }
            doDropIndex(tableMeta, indexName, context);
            context.append(context.sqlBuilder().toString());
            index++;
        }
        return context.build();
    }

    /*####################################### below protected template method #################################*/


    protected abstract void tableOptionsClause(DDLContext context);

    protected abstract void nonReservedPropDefault(FieldMeta<?, ?> fieldMeta, DDLContext context);

    protected abstract void defaultOfCreateAndUpdate(FieldMeta<?, ?> fieldMeta, DDLContext context);

    protected abstract void dataTypeClause(FieldMeta<?, ?> fieldMeta, DDLContext context);

    protected abstract boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta);

    protected abstract void indexSuffix(DDLContext context);

    protected abstract boolean independentIndexDefinition();


    /*####################################### below protected method #################################*/

    protected void doDropIndex(TableMeta<?> tableMeta, String indexName, DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append("ALTER TABLE ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()))
                .append(" DROP INDEX ")
                .append(this.dialect.quoteIfNeed(indexName))
        ;
    }

    protected void doAddColumn(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        TableMeta<?> tableMeta = context.tableMeta();
        StringBuilder builder = context.sqlBuilder();
        builder.append("ALTER TABLE ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()))
                .append(" ADD COLUMN ")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));

        dataTypeClause(fieldMeta, context);
        nullableClause(fieldMeta, context);
        defaultClause(fieldMeta, context);
        columnCommentClause(fieldMeta, context);
    }

    protected void doChangeColumn(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        TableMeta<?> tableMeta = context.tableMeta();
        StringBuilder builder = context.sqlBuilder();
        builder.append("ALTER TABLE ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()))
                .append(" CHANGE COLUMN ")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));

        dataTypeClause(fieldMeta, context);
        nullableClause(fieldMeta, context);
        defaultClause(fieldMeta, context);
        columnCommentClause(fieldMeta, context);
    }


    protected void createClause(DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append("CREATE TABLE ")
                .append(this.quoteIfNeed(context.tableMeta().tableName()))
        ;
    }

    protected void columnDefinitionClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder().append(" ");

        //1. field name clause
        builder.append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
        //2. data type clause
        dataTypeClause(fieldMeta, context);
        //3. null or not null clause
        nullableClause(fieldMeta, context);
        //3. default clause
        defaultClause(fieldMeta, context);
        //4. common clause
        columnCommentClause(fieldMeta, context);
    }

    protected final void columnCommentClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        context.sqlBuilder().append(" COMMENT '")
                .append(fieldMeta.comment())
                .append("'")
        ;
    }

    protected void nullableClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder tableBuilder = context.sqlBuilder();
        tableBuilder.append(" ");
        if (fieldMeta.nullable()) {
            tableBuilder.append("NULL");
        } else {
            tableBuilder.append("NOT NULL");
        }
    }

    protected final void defaultClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {

        if (!hasDefaultClause(fieldMeta)) {
            return;
        }

        StringBuilder tableBuilder = context.sqlBuilder();
        final int start = tableBuilder.length();
        tableBuilder.append(" ")
                .append(Keywords.DEFAULT)
                .append(" ");
        if (TableMeta.RESERVED_PROPS.contains(fieldMeta.propertyName())) {
            reservedPropDefaultValue(fieldMeta, context);
        } else if (fieldMeta.tableMeta().discriminator() == fieldMeta) {
            tableBuilder.append(fieldMeta.tableMeta().discriminatorValue());
        } else if (!StringUtils.hasText(fieldMeta.defaultValue())
                && (MetaConstant.MAYBE_NO_DEFAULT_TYPES.contains(fieldMeta.javaType())
                || CodeEnum.class.isAssignableFrom(fieldMeta.javaType()))) {
            tableBuilder.append(context.defaultValue(fieldMeta));
        } else if (fieldMeta.nullable() && !StringUtils.hasText(fieldMeta.defaultValue())) {
            tableBuilder.delete(start, start + Keywords.DEFAULT.length() + 2);
        } else {
            nonReservedPropDefault(fieldMeta, context);
        }

    }

    protected void indexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        if (indexMeta.isPrimaryKey()) {
            builder.append("PRIMARY KEY ");
        } else if (indexMeta.unique()) {
            builder.append("UNIQUE ");
            builder.append(this.dialect.quoteIfNeed(indexMeta.name()));
        } else {
            builder.append("KEY ");
            builder.append(this.dialect.quoteIfNeed(indexMeta.name()));
        }
        if (StringUtils.hasText(indexMeta.type())) {
            builder.append(indexMeta.type());
        }
        builder.append(" (");
        int index = 0;
        for (IndexFieldMeta<?, ?> fieldMeta : indexMeta.fieldList()) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
            if (Boolean.TRUE.equals(fieldMeta.fieldAsc())) {
                builder.append(" ASC");
            } else if (Boolean.FALSE.equals(fieldMeta.fieldAsc())) {
                builder.append(" DESC");
            }
            index++;
        }
        builder.append(")");
    }


    /*################################## blow private method ##################################*/

    private void reservedPropDefaultValue(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder tableBuilder = context.sqlBuilder().append(" ");
        switch (fieldMeta.propertyName()) {
            case TableMeta.ID:
                // no-op
                break;
            case TableMeta.CREATE_TIME:
            case TableMeta.UPDATE_TIME:
                defaultOfCreateAndUpdate(fieldMeta, context);
                break;
            case TableMeta.VISIBLE:
                tableBuilder.append(
                        fieldMeta.mappingMeta().nonNullTextValue(Boolean.TRUE)
                );
                break;
            case TableMeta.VERSION:
                tableBuilder.append("0");
                break;
            default:
                throw new RuntimeException(String.format("Entity[%s].prop[%s] isn'field required prop",
                        fieldMeta.tableMeta().tableName(), fieldMeta.propertyName()));
        }
    }


    private Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> defaultFunctionMap() {
        Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> funcMap = this.defaultFunctionMap;
        if (funcMap == null) {
            funcMap = DDLUtils.createDefaultFunctionMap();
            this.defaultFunctionMap = funcMap;
        }
        return funcMap;
    }

}
