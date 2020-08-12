package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.GenericRmSessionFactory;
import io.army.ShardingMode;
import io.army.criteria.CriteriaException;
import io.army.criteria.NotFoundRouteException;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.StringUtils;

/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractTableContextSQLContext extends AbstractSQLContext implements TableContextSQLContext {

    protected final TableContext primaryTableContext;

    protected final TableContext tableContext;

    protected final ShardingMode shardingMode;

    protected final String primaryRouteSuffix;

    protected final boolean childContext;

    protected final boolean  allowSpanSharding;

    protected AbstractTableContextSQLContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible);
        GenericRmSessionFactory sessionFactory = dialect.sessionFactory();

        this.shardingMode = dialect.sessionFactory().shardingMode();
        this.tableContext = tableContext;
        this.primaryTableContext = tableContext;
        this.childContext = false;

        this.allowSpanSharding = sessionFactory.allowSpanSharding();
        this.primaryRouteSuffix = tableContext.primaryRouteSuffix;
        assertPrimaryRouteSuffix();
    }

    protected AbstractTableContextSQLContext(TableContextSQLContext parentContext, TableContext tableContext) {
        super(parentContext);
        GenericRmSessionFactory sessionFactory = dialect.sessionFactory();

        this.shardingMode = dialect.sessionFactory().shardingMode();
        this.tableContext = tableContext;
        if(parentContext instanceof ComposeSelectContext){
            this.primaryTableContext = this.tableContext;
            this.primaryRouteSuffix = this.tableContext.primaryRouteSuffix;
        }else {
            this.primaryTableContext = parentContext.primaryTableContext();
            this.primaryRouteSuffix = parentContext.primaryRouteSuffix();
        }
        this.childContext = true;
        this.allowSpanSharding = sessionFactory.allowSpanSharding();
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        doAppendField(tableAlias, fieldMeta);
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        doAppendField(findTableAlias(fieldMeta), fieldMeta);
    }


    @Override
    public final void appendParentOf(ChildTableMeta<?> childMeta,String childAlias) {
        validateTableAndAlias(childMeta,childAlias);
        this.sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(childMeta.parentMeta().tableName()));
        if(this.shardingMode != ShardingMode.NO_SHARDING){
            this.sqlBuilder.append(obtainRouteSuffix(childMeta,childAlias));
        }
    }

    @Override
    public void appendTable(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        if (!this.tableContext.tableCountMap.containsKey(tableMeta)) {
            throw DialectUtils.createUnKnownTableException(tableMeta);
        }
        doAppendTable(tableMeta, tableAlias);
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
    public final void appendText(String textValue) {
        sqlBuilder.append(" ")
                .append(dialect.quoteIfNeed(textValue));
    }

    @Override
    public final String primaryRouteSuffix() {
        return this.primaryRouteSuffix;
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

    /**
     * <ol>
     *     <li>append table name to builder</li>
     *     <li>append route suffix to builder if need</li>
     *     <li>append table alias to builder if need</li>
     * </ol>
     * <p>
     * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
     * </p>
     */
    protected final void doAppendTable(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        final Dialect dialect = this.dialect;
        SQLBuilder builder = obtainTablePartBuilder();
        //1. append table name
        builder.append(" ")
                .append(dialect.quoteIfNeed(tableMeta.tableName()));

        if (this.shardingMode != ShardingMode.NO_SHARDING
                && !tableMeta.routeFieldList(false).isEmpty()) {
            //2. append table route suffix
            builder.append(obtainRouteSuffix(tableMeta,tableAlias));
        }

        if (canAppendTableAlias(tableMeta)) {
            //3. append table alias
            if (tableAlias == null) {
                throw new IllegalArgumentException(String.format(
                        "TableMeta[%s] table alias required int SQLContext[%s].", tableMeta, this));
            }
            validateTableAndAlias(tableMeta,tableAlias);
            builder.append(" ");
            if (dialect.tableAliasAfterAs()) {
                builder.append("AS ");
            }
            builder.append(dialect.quoteIfNeed(tableAlias));
        }

    }

    protected boolean canAppendTableAlias(TableMeta<?> tableMeta) {
        boolean can ;
        if(this instanceof DeleteContext ){
            can =  this.dialect.singleDeleteHasTableAlias();
        }else {
            can = true;
        }
        return can;
    }

    protected void validateTableAndAlias(TableMeta<?> tableMeta, String tableAlias) {
        if (this.tableContext.aliasTableMap.get(tableAlias) != tableMeta) {
            throw new IllegalArgumentException(String.format("TableMeta[%s] and tableAlias[%s] not match."
                    , tableMeta, tableAlias));
        }
    }

    protected abstract String parseTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias);

    protected String findTableAliasFromParent(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        throw DialectUtils.createUnKnownFieldException(fieldMeta);
    }

    /*################################## blow private method ##################################*/

    private String obtainRouteSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias){
        String routeSuffix;
        if(this.allowSpanSharding){
            routeSuffix = parseTableSuffix(tableMeta, tableAlias);
            if (!StringUtils.hasText(routeSuffix)) {
                throw new ArmyRuntimeException(ErrorCode.CRITERIA_ERROR, "parseTableSuffix method return error.");
            }
        }else {
            if(this instanceof SubQueryInsertContext  ){
                SubQueryInsertContext.assertSupportRoute(this.dialect);
            }
            routeSuffix = this.primaryRouteSuffix();
        }
        return routeSuffix;
    }



    private void assertPrimaryRouteSuffix() {
        if (this.shardingMode != ShardingMode.NO_SHARDING
                && (this.primaryRouteSuffix == null || !this.primaryRouteSuffix.startsWith("_"))) {
            throw new NotFoundRouteException("not found legal primary route[%s].",this.primaryRouteSuffix);
        }
    }


}
