package io.army.dialect;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.sqldatatype.SQLDataType;
import io.army.sqldatatype.SQLDataTypeUtils;
import io.army.sqldatatype.UnsupportedSQLDataTypeException;
import io.army.util.StringUtils;

import java.util.Collection;
import java.util.List;

public abstract class AbstractDDL extends AbstractSQL implements DDL {

    public AbstractDDL(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow interfaces method ##################################*/

    @Override
    public final List<String> createTable(TableMeta<?> tableMeta, @Nullable String tableSuffix) {
        DDLContext context = new DDLContextImpl(this.dialect, tableMeta, tableSuffix);
        // 1. create clause
        createTableClause(context);
        context.sqlBuilder().append(" (\n");
        // 2. column definition clause
        columnListDefinitions(context);
        primaryKeyClause(context);
        if (useIndependentIndexDefinition()) {
            context.sqlBuilder().append("\n)");
            // 3. table option clause
            tableOptionsClause(context);
            //4. rest builder
            context.resetBuilder();
            //5. independent index list clause
            independentIndexListDefinitions(context);
        } else {
            context.sqlBuilder().append(",\n");
            // 3. index definition
            inlineIndexListDefinitionClause(context);
            context.sqlBuilder().append("\n)");
            // 4. tableMeta options definition
            tableOptionsClause(context);
            context.resetBuilder();
        }

        if (useIndependentComment()) {
            // finally , independent comment statement
            independentTableComment(context);
            context.resetBuilder();
        }
        return context.build();
    }

    @Override
    public final List<String> addColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> addFieldMetas) {

        DDLContext context = new DDLContextImpl(this.dialect, tableMeta, tableSuffix);

        for (FieldMeta<?, ?> fieldMeta : addFieldMetas) {

            if (fieldMeta.tableMeta() != tableMeta) {
                throw new IllegalArgumentException(String.format("%s and %s not match.", tableMeta, fieldMeta));
            }
            doAddColumn(fieldMeta, context);
            if (useIndependentComment()) {
                // reset builder
                context.resetBuilder();
                independentColumnComment(fieldMeta, context);
            }
            context.resetBuilder();
        }
        return context.build();
    }

    @Override
    public final List<String> changeColumn(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<FieldMeta<?, ?>> changeFieldMetas) {
        DDLContext context = new DDLContextImpl(this.dialect, tableMeta, tableSuffix);
        doChangeColumn(changeFieldMetas, context);
        return context.build();
    }

    @Override
    public final List<String> addIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<IndexMeta<?>> addIndexMetas) {
        DDLContext context = new DDLContextImpl(this.dialect, tableMeta, tableSuffix);
        for (IndexMeta<?> indexMeta : addIndexMetas) {
            if (indexMeta.table() != tableMeta) {
                throw new IllegalArgumentException(String.format("%s and %s not match.", tableMeta, indexMeta));
            }
            createIndexDefinition(indexMeta, context);
            // append sql statement
            context.resetBuilder();
        }
        return context.build();
    }

    @Override
    public final List<String> dropIndex(TableMeta<?> tableMeta, @Nullable String tableSuffix
            , Collection<String> indexNames) {
        DDLContext context = new DDLContextImpl(this.dialect, tableMeta, tableSuffix);
        for (String indexName : indexNames) {
            doDropIndex(indexName, context);
            context.resetBuilder();
        }
        return context.build();
    }

    @Override
    public final List<String> modifyTableComment(TableMeta<?> tableMeta, @Nullable String tableSuffix) {
        DDLContext context = new DDLContextImpl(this.dialect, tableMeta, tableSuffix);
        internalModifyTableComment(context);
        context.resetBuilder();
        return context.build();
    }

    @Override
    public final List<String> modifyColumnComment(FieldMeta<?, ?> fieldMeta, @Nullable String tableSuffix) {
        DDLContext context = new DDLContextImpl(this.dialect, fieldMeta.tableMeta(), tableSuffix);
        internalModifyColumnComment(fieldMeta, context);
        context.resetBuilder();
        return context.build();
    }

    @Override
    public void clearForDDL() {

    }



    /*####################################### below protected template method #################################*/

    protected abstract void internalModifyTableComment(DDLContext context);

    protected abstract void internalModifyColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context);

    protected abstract void tableOptionsClause(DDLContext context);

    protected abstract void doDefaultExpression(FieldMeta<?, ?> fieldMeta, SQLBuilder builder);

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


    protected abstract boolean supportSQLDateType(SQLDataType dataType);


    /*####################################### below protected method #################################*/

    protected final void doChangeColumn(Collection<FieldMeta<?, ?>> changeFieldMetas, DDLContext context) {
        TableMeta<?> tableMeta = context.tableMeta();
        for (FieldMeta<?, ?> fieldMeta : changeFieldMetas) {

            if (fieldMeta.tableMeta() != tableMeta) {
                throw new IllegalArgumentException(String.format("%s and %s not match.", tableMeta, fieldMeta));
            }
            doChangeColumn(fieldMeta, context);
            if (useIndependentComment()) {
                // reset builder
                context.resetBuilder();
                independentColumnComment(fieldMeta, context);
            }
            context.resetBuilder();
        }
    }

    protected void doDropIndex(String indexName, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
        builder.append("ALTER TABLE ");
        context.appendTable();
        builder.append(" DROP INDEX ")
                .append(this.dialect.quoteIfNeed(indexName))
        ;
    }

    protected void doAddColumn(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
        builder.append("ALTER TABLE");
        context.appendTable();
        builder.append(" ADD COLUMN");
        context.appendField(fieldMeta);

        dataTypeClause(fieldMeta, context);
        nullableClause(fieldMeta, context);
        defaultClause(fieldMeta, context);
        inlineColumnCommentClause(fieldMeta, context);
    }

    protected void doChangeColumn(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
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
        inlineColumnCommentClause(fieldMeta, context);
    }

    /**
     * append create clause before {@code '(\n' }
     */
    protected final void createTableClause(DDLContext context) {
        context.sqlBuilder().append("CREATE TABLE IF NOT EXISTS");
        context.appendTable();

    }

    protected void columnDefinitionClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {

        //1. field name clause
        context.appendField(fieldMeta);
        //2. data type clause
        dataTypeClause(fieldMeta, context);
        //3. null or not null clause
        nullableClause(fieldMeta, context);
        //4. default clause
        defaultClause(fieldMeta, context);
        if (!useIndependentComment()) {
            // 5. comment clause
            inlineColumnCommentClause(fieldMeta, context);
        }
    }

    protected final void inlineColumnCommentClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        context.sqlBuilder().append(" COMMENT '")
                .append(DDLUtils.escapeQuote(fieldMeta.comment().trim()))
                .append("'")
        ;
    }


    protected final void dataTypeClause(FieldMeta<?, ?> fieldMeta, DDLContext context) throws MetaException {
        SQLDataType dataType = fieldMeta.mappingMeta().sqlDataType(database());
        if (!supportSQLDateType(dataType)) {
            throw new UnsupportedSQLDataTypeException("%s,database[%s] not support SQLDataType[%s]."
                    , fieldMeta, database(), dataType);
        }

        dataType.dataTypeClause(fieldMeta, context.sqlBuilder().append(" "));

    }


    protected final void nullableClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
        if (fieldMeta.nullable()) {
            builder.append(" NULL");
        } else {
            builder.append(" NOT NULL");
        }
    }

    protected final void defaultClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        if (fieldMeta.nullable() && !StringUtils.hasText(fieldMeta.defaultValue())) {
            return;
        }

        SQLBuilder builder = context.sqlBuilder();
        final String defaultKeyWord = " DEFAULT ";
        if (TableMeta.RESERVED_PROPS.contains(fieldMeta.propertyName())) {
            // defaultKeyWord in reservedPropDefaultValue
            reservedPropDefaultValue(fieldMeta, context);
        } else if (fieldMeta.tableMeta().discriminator() == fieldMeta) {
            builder.append(defaultKeyWord)
                    .append(fieldMeta.tableMeta().discriminatorValue());
        } else {
            if (StringUtils.hasText(fieldMeta.defaultValue())) {
                builder.append(defaultKeyWord);
                handleDefaultExpression(fieldMeta, builder);
            } else if (DDLUtils.simpleJavaType(fieldMeta)) {
                Database database = database();
                SQLDataType sqlDataType = fieldMeta.mappingMeta().sqlDataType(database);
                if (sqlDataType.supportZeroValue(database)) {
                    builder.append(defaultKeyWord);
                    sqlDataType.zeroValue(fieldMeta, builder, database);
                } else {
                    throw SQLDataTypeUtils.createNotSupportZeroValueException(sqlDataType, fieldMeta, database);
                }
            } else if (!fieldMeta.nullable()) {
                throw new MetaException(fieldMeta + " nullable and no default" +
                        ",only [reserved properties,discriminator, simple java type] can no default value."
                );
            }
        }

    }

    protected void createIndexDefinition(IndexMeta<?> indexMeta, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder()
                .append("ALTER TABLE ");
        context.appendTable();
        builder.append(" ADD ");
        inlineIndexDefinitionClause(indexMeta, context);
    }

    protected void inlineIndexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder().append(" ");
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
        builder.append("(");
        int index = 0;
        for (IndexFieldMeta<?, ?> fieldMeta : indexMeta.fieldList()) {
            if (index > 0) {
                builder.append(",");
            }
            context.appendField(fieldMeta);
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
        Database database = database();
        SQLDataType sqlDataType = fieldMeta.mappingMeta().sqlDataType(database);
        SQLBuilder builder = context.sqlBuilder();
        final String defaultKey = " DEFAULT ";
        switch (fieldMeta.propertyName()) {
            case TableMeta.ID:
                // no-op
                break;
            case TableMeta.CREATE_TIME:
            case TableMeta.UPDATE_TIME:
                if (sqlDataType.supportNowValue(database)) {
                    builder.append(defaultKey);
                    sqlDataType.nowValue(fieldMeta, builder, database);
                } else {
                    throw SQLDataTypeUtils.createNotSupportNowExpressionException(sqlDataType, fieldMeta, database);
                }
                break;
            case TableMeta.VISIBLE:
            case TableMeta.VERSION:
                if (sqlDataType.supportZeroValue(database)) {
                    builder.append(defaultKey);
                    sqlDataType.zeroValue(fieldMeta, builder, database);
                } else {
                    throw SQLDataTypeUtils.createNotSupportZeroValueException(sqlDataType, fieldMeta, database);
                }
                break;
            default:
                throw new IllegalArgumentException(String.format("%s isn't reserved property.", fieldMeta));
        }
    }

    private void columnListDefinitions(DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
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
        SQLBuilder builder = context.sqlBuilder();
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

        for (IndexMeta<?> indexMeta : DDLUtils.sortIndexMetaCollection(context.tableMeta())) {
            independentIndexDefinitionClause(indexMeta, context);
            context.resetBuilder();
        }
    }

    private void handleDefaultExpression(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) {
        Database database = database();
        SQLDataType sqlDataType = fieldMeta.mappingMeta().sqlDataType(database);
        switch (fieldMeta.defaultValue()) {
            case IDomain.NOW:
                if (sqlDataType.supportNowValue(database)) {
                    sqlDataType.nowValue(fieldMeta, builder, database);
                } else {
                    throw SQLDataTypeUtils.createNotSupportNowExpressionException(sqlDataType, fieldMeta, database);
                }
                break;
            case IDomain.ZERO_VALUE:
                if (sqlDataType.supportZeroValue(database)) {
                    sqlDataType.zeroValue(fieldMeta, builder, database);
                } else {
                    throw SQLDataTypeUtils.createNotSupportZeroValueException(sqlDataType, fieldMeta, database);
                }
                break;
            default:
                doDefaultExpression(fieldMeta, builder);
        }
    }

    private void primaryKeyClause(DDLContext context) {
        SQLBuilder builder = context.sqlBuilder().append(",\n");
        builder.append("PRIMARY KEY(");
        context.appendField(context.tableMeta().id());
        builder.append(")");
    }

}
