package io.army.dialect;

import io.army.criteria.MetaException;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.modelgen.MetaConstant;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.sql.JDBCType;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractDDL extends AbstractSQL implements DDL {

    private Map<Class<?>, BiFunction<FieldMeta<?, ?>, ZoneId, String>> defaultFunctionMap;

    private Map<JDBCType, Function<FieldMeta<?, ?>, String>> jdbcTypeFunctionMap;

    public AbstractDDL(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow interfaces method ##################################*/

    @Override
    public final List<String> createTable(TableMeta<?> tableMeta, @Nullable String tableSuffix) {
        DDLContext context = new TableDDLContext(this.dialect, tableMeta, tableSuffix, defaultFunctionMap());
        StringBuilder builder = context.sqlBuilder();
        // 1. create clause
        createTableClause(context);
        builder.append(" (\n");
        // 2. column definition clause
        columnListDefinitions(context);
        if (useIndependentIndexDefinition()) {
            builder.append(" )\n");
            // 3. table option clause
            tableOptionsClause(context);
            //4. rest builder
            context.append(builder.toString());
            context.resetBuilder();
            //5. independent index list clause
            independentIndexListDefinitions(context);
        } else {
            // 3. index definition
            inlineIndexListDefinitionClause(context);
            builder.append(" )\n");
            // 4. tableMeta options definition
            tableOptionsClause(context);
        }

        if (useIndependentComment()) {
            independentTableComment(context);
        }
        return context.build();
    }

    @Override
    public final List<String> addColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> addFieldMetas) {

        final DDLContext context = new ColumnDDLContext(this.dialect, tableMeta, tableSuffix, defaultFunctionMap());

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
    public final List<String> changeColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> changeFieldMetas) {
        final DDLContext context = new ColumnDDLContext(this.dialect, tableMeta, tableSuffix, defaultFunctionMap());
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : changeFieldMetas) {

            Assert.isTrue(fieldMeta.tableMeta() == tableMeta, () -> String.format(
                    "TableMeta[%s] then FieldMeta[%s] not match."
                    , tableMeta, fieldMeta));
            if (index > 0) {
                context.resetBuilder();
            }

            doChangeColumn(fieldMeta, context);
            context.append(context.sqlBuilder().toString());
            index++;
        }
        return context.build();
    }

    @Override
    public final List<String> addIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<IndexMeta<?>> addIndexMetas) {
        final DDLContext context = new IndexDDLContext(this.dialect, tableMeta, tableSuffix, defaultFunctionMap());
        int index = 0;
        for (IndexMeta<?> indexMeta : addIndexMetas) {
            Assert.isTrue(indexMeta.table() == tableMeta, () -> String.format(
                    "TableMeta[%s] then Index[%s] not match."
                    , tableMeta.tableName(), indexMeta.name()));
            if (index > 0) {
                context.resetBuilder();
            }
            createIndexDefinition(indexMeta, context);
            context.append(context.sqlBuilder().toString());

            index++;
        }
        return context.build();
    }

    @Override
    public final List<String> dropIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<String> indexNames) {
        final DDLContext context = new IndexDDLContext(this.dialect, tableMeta, tableSuffix, defaultFunctionMap());
        int index = 0;
        for (String indexName : indexNames) {
            if (index > 0) {
                context.resetBuilder();
            }
            doDropIndex(indexName, context);
            context.append(context.sqlBuilder().toString());
            index++;
        }
        return context.build();
    }

    @Override
    public void clearForDDL() {
        this.defaultFunctionMap = null;
        this.jdbcTypeFunctionMap = null;
    }

    /*####################################### below protected template method #################################*/


    protected abstract void tableOptionsClause(DDLContext context);

    protected abstract void doFieldDefaultValue(FieldMeta<?, ?> fieldMeta, DDLContext context);

    protected abstract void defaultOfCreateAndUpdate(FieldMeta<?, ?> fieldMeta, DDLContext context);


    protected abstract boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta);

    protected abstract boolean useIndependentIndexDefinition();

    protected abstract boolean useIndependentComment();

    protected abstract void independentIndexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context);

    /**
     * This method performance when create table .
     */
    protected abstract void independentTableComment(DDLContext context);

    /**
     * This method performance when add/modify column .
     */
    protected abstract void independentColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context);

    /**
     * @return a unmodifiable map
     */
    protected abstract Map<JDBCType, Function<FieldMeta<?, ?>, String>> createJdbcFunctionMap();



    /*####################################### below protected method #################################*/

    protected final void dataTypeClause(FieldMeta<?, ?> fieldMeta, DDLContext context) throws MetaException {
        Map<JDBCType, Function<FieldMeta<?, ?>, String>> functionMap = this.jdbcTypeFunctionMap;
        if (functionMap == null) {
            functionMap = createJdbcFunctionMap();
        }
        Function<FieldMeta<?, ?>, String> function = functionMap.get(fieldMeta.mappingMeta().jdbcType());
        if (function == null) {
            throw new MetaException("%s,JDBCType[%s] isn't supported by Dialect[%s] .", fieldMeta, this.database());
        }
        context.sqlBuilder()
                .append(" ")
                .append(function.apply(fieldMeta));
    }


    protected void doDropIndex(String indexName, DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append("ALTER TABLE ");
        context.appendTable();
        builder.append(" DROP INDEX ")
                .append(this.dialect.quoteIfNeed(indexName))
        ;
    }

    protected void doAddColumn(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append("ALTER TABLE");
        context.appendTable();
        builder.append(" ADD COLUMN ")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));

        dataTypeClause(fieldMeta, context);
        nullableClause(fieldMeta, context);
        defaultClause(fieldMeta, context);
        columnCommentClause(fieldMeta, context);
    }

    protected void doChangeColumn(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        final String safeColumnName = this.dialect.quoteIfNeed(fieldMeta.fieldName());
        builder.append("ALTER TABLE ");
        context.appendTable();
        builder.append(" CHANGE COLUMN ")
                .append(safeColumnName)
                .append(" ")
                .append(safeColumnName)
        ;

        dataTypeClause(fieldMeta, context);
        nullableClause(fieldMeta, context);
        defaultClause(fieldMeta, context);
        columnCommentClause(fieldMeta, context);
    }

    /**
     * append create clause before {@code '(\n' }
     */
    protected final void createTableClause(DDLContext context) {
        context.sqlBuilder().append("CREATE TABLE IF NOT EXISTS ");
        context.appendTable();

    }

    protected void columnDefinitionClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {

        //1. field name clause
        context.appendField(fieldMeta);
        //4. null or not null clause
        nullableClause(fieldMeta, context);
        //3. data type clause
        dataTypeClause(fieldMeta, context);
        //3. default clause
        defaultClause(fieldMeta, context);
        //4. common clause
        columnCommentClause(fieldMeta, context);
    }

    protected final void columnCommentClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        context.sqlBuilder().append(" COMMENT '")
                .append(escapeQuote(fieldMeta.comment()))
                .append("'")
        ;
    }

    protected String escapeQuote(String text) {
        return text.replaceAll("'", "\\\\'");
    }

    protected final void nullableClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        if (fieldMeta.nullable()) {
            builder.append(" NULL");
        } else {
            builder.append(" NOT NULL");
        }
    }

    protected final void defaultClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {

        if (!hasDefaultClause(fieldMeta)) {
            return;
        }

        StringBuilder builder = context.sqlBuilder();
        final String defaultKeyword = " DEFAULT ";

        if (TableMeta.RESERVED_PROPS.contains(fieldMeta.propertyName())) {
            builder.append(defaultKeyword);
            reservedPropDefaultValue(fieldMeta, context);
        } else if (fieldMeta.tableMeta().discriminator() == fieldMeta) {
            builder.append(defaultKeyword)
                    .append(fieldMeta.tableMeta().discriminatorValue());
        } else if (!StringUtils.hasText(fieldMeta.defaultValue())
                && (MetaConstant.MAYBE_NO_DEFAULT_TYPES.contains(fieldMeta.javaType())
                || CodeEnum.class.isAssignableFrom(fieldMeta.javaType()))) {
            builder.append(defaultKeyword)
                    .append(context.defaultValue(fieldMeta));
        } else {
            if (StringUtils.hasText(fieldMeta.defaultValue())) {
                doFieldDefaultValue(fieldMeta, context);
            } else if (!fieldMeta.nullable()) {
                throw new MetaException("%s isn't nullable and no default value.", fieldMeta);
            }
        }

    }


    protected void createIndexDefinition(IndexMeta<?> indexMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder()
                .append("ALTER TABLE ");
        context.appendTable();
        builder.append(" ADD ");
        inlineIndexDefinitionClause(indexMeta, context);
    }

    protected void inlineIndexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder().append(" ");
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
        StringBuilder builder = context.sqlBuilder().append(" ");
        switch (fieldMeta.propertyName()) {
            case TableMeta.ID:
                // no-op
                break;
            case TableMeta.CREATE_TIME:
            case TableMeta.UPDATE_TIME:
                defaultOfCreateAndUpdate(fieldMeta, context);
                break;
            case TableMeta.VISIBLE:
                builder.append(
                        fieldMeta.mappingMeta().nonNullTextValue(Boolean.TRUE)
                );
                break;
            case TableMeta.VERSION:
                builder.append("0");
                break;
            default:
                throw new IllegalArgumentException(String.format("%s isn't reserved property.", fieldMeta));
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

    private void columnListDefinitions(DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : DDLUtils.sortFieldMetaCollection(context.tableMeta())) {
            if (index > 0) {
                builder.append(",\n");
            }
            columnDefinitionClause(fieldMeta, context);
            index++;
        }
    }

    private void inlineIndexListDefinitionClause(DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        int index = 0;
        for (IndexMeta<?> indexMeta : DDLUtils.sortIndexMetaCollection(context.tableMeta())) {
            if (index > 0) {
                builder.append(",\n");
            }
            inlineIndexDefinitionClause(indexMeta, context);
            index++;
        }
    }

    private void independentIndexListDefinitions(DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        int index = 0;
        for (IndexMeta<?> indexMeta : DDLUtils.sortIndexMetaCollection(context.tableMeta())) {
            if (index > 0) {
                context.append(builder.toString());
                context.resetBuilder();
                builder = context.sqlBuilder();
            }
            independentIndexDefinitionClause(indexMeta, context);
            index++;
        }
    }

}
