package io.army.dialect;

import io.army.ShardingMode;
import io.army.criteria.CriteriaException;
import io.army.criteria.NotFoundRouteException;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.StringUtils;

public abstract class AbstractTableContextSQLContext extends AbstractSQLContext implements TableContextSQLContext {

    protected final TableContext primaryTableContext;

    protected final TableContext tableContext;

    protected final ShardingMode shardingMode;

    protected AbstractTableContextSQLContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible);
        this.shardingMode = dialect.sessionFactory().shardingMode();
        this.tableContext = tableContext;
        this.primaryTableContext = tableContext;
        assertPrimaryRouteSuffix();
    }

    protected AbstractTableContextSQLContext(TableContextSQLContext original, TableContext tableContext) {
        super(original);
        this.shardingMode = dialect.sessionFactory().shardingMode();
        this.tableContext = tableContext;
        this.primaryTableContext = original.primaryTableContext();
        assertPrimaryRouteSuffix();
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        doAppendField(tableAlias, fieldMeta, this.sqlBuilder);
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        doAppendField(findTableAlias(fieldMeta), fieldMeta, this.sqlBuilder);
    }


    @Override
    public final void appendParentOf(ChildTableMeta<?> tableMeta) {

    }

    @Override
    public void appendTable(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        if (!this.tableContext.tableCountMap.containsKey(tableMeta)) {
            throw DialectUtils.createUnKnownTableException(tableMeta);
        }
        doAppendTable(tableMeta, tableAlias, this.sqlBuilder);
    }


    @Override
    public final TableContext primaryTableContext() {
        return this.primaryTableContext;
    }

    @Override
    public final TableContext tableContext() {
        return this.tableContext;
    }

    @Override
    public TableContext parentTableContext() {
        return null;
    }

    @Override
    public final void appendText(String textValue) {
        sqlBuilder.append(" ")
                .append(dialect.quoteIfNeed(textValue));
    }

    @Override
    public final void appendTextValue(MappingMeta mappingType, Object value) {
        sqlBuilder.append(
                DialectUtils.quoteIfNeed(
                        mappingType
                        , mappingType.nonNullTextValue(value)
                )
        );
    }

    @Override
    public final String primaryRouteSuffix() {
        return this.tableContext.primaryRouteSuffix;
    }

    protected final String findTableAlias(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        Integer count = this.tableContext.tableCountMap.get(fieldMeta.tableMeta());
        String tableAlias;
        if (count == null) {
            tableAlias = findTableAliasFromParent(fieldMeta);
        } else if (count.equals(1)) {
            tableAlias = this.tableContext.tableAliasMap.get(fieldMeta.tableMeta());
        } else {
            throw DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        if (tableAlias == null) {
            // fromContext or parentFromContext error.
            throw DialectUtils.createArmyCriteriaException();
        }
        return tableAlias;
    }

    protected final void doAppendTable(TableMeta<?> tableMeta, @Nullable String tableAlias, StringBuilder builder) {
        final Dialect dialect = this.dialect;
        builder.append(" ")
                .append(dialect.quoteIfNeed(tableMeta.tableName()));

        if (this.shardingMode != ShardingMode.NO_SHARDING
                && !tableMeta.routeFieldList(false).isEmpty()) {
            doAppendTableSuffix(tableMeta, tableAlias, builder);
        }

        if (canAppendTableAlias(tableMeta)) {
            if (tableAlias == null) {
                throw new IllegalArgumentException(String.format(
                        "TableMeta[%s] table alias required int SQLContext[%s].", tableMeta, this));
            }
            builder.append(" ");
            if (dialect.tableAliasAfterAs()) {
                builder.append("AS ");
            }
            builder.append(dialect.quoteIfNeed(tableAlias));
        }

    }

    protected boolean canAppendTableAlias(TableMeta<?> tableMeta) {
        return true;
    }

    protected void validateTableAndAlias(TableMeta<?> tableMeta, String tableAlias) {
        if (this.tableContext.aliasTableMap.get(tableAlias) != tableMeta) {
            throw new IllegalArgumentException(String.format("TableMeta[%s] and tableAlias[%s] not match."
                    , tableMeta, tableAlias));
        }
    }

    protected abstract void doAppendTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias
            , StringBuilder builder);

    protected String findTableAliasFromParent(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        throw DialectUtils.createUnKnownFieldException(fieldMeta);
    }

    /*################################## blow private method ##################################*/

    private void assertPrimaryRouteSuffix() {
        if (this.shardingMode != ShardingMode.NO_SHARDING
                && !StringUtils.hasText(this.primaryTableContext.primaryRouteSuffix)) {
            throw new NotFoundRouteException("not found primary route.");
        }
    }
}
