package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.NotFoundRouteException;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.FactoryMode;
import io.army.session.GenericRmSessionFactory;
import io.army.util.StringUtils;

/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractTableContextSQLContext extends AbstractSQLContext implements _TablesSqlContext {

    protected final TablesContext primaryTableContext;

    protected final TablesContext tableContext;

    protected final FactoryMode factoryMode;

    protected final String primaryRouteSuffix;

    protected final boolean childContext;

    protected final boolean  allowSpanSharding;

    protected AbstractTableContextSQLContext(Dialect dialect, Visible visible, TablesContext tableContext) {
        super(dialect, visible);
        GenericRmSessionFactory sessionFactory = dialect.sessionFactory();

        this.factoryMode = dialect.sessionFactory().factoryMode();
        this.tableContext = tableContext;
        this.primaryTableContext = tableContext;
        this.childContext = false;

        this.allowSpanSharding = sessionFactory.allowSpanSharding();
        this.primaryRouteSuffix = tableContext.primaryRouteSuffix;
        assertPrimaryRouteSuffix();
    }

    protected AbstractTableContextSQLContext(_TablesSqlContext parentContext, TablesContext tableContext) {
        super(parentContext);
        GenericRmSessionFactory sessionFactory = dialect.sessionFactory();

        this.factoryMode = dialect.sessionFactory().factoryMode();
        this.tableContext = tableContext;
        if (parentContext instanceof ComposeSelectContext) {
            this.primaryTableContext = this.tableContext;
            this.primaryRouteSuffix = this.tableContext.primaryRouteSuffix;
        } else {
            this.primaryTableContext = parentContext.primaryTableContext();
            this.primaryRouteSuffix = parentContext.primaryRouteSuffix();
        }
        this.childContext = true;
        this.allowSpanSharding = sessionFactory.allowSpanSharding();
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> field) {
        doAppendField(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?, ?> field) {
        doAppendField(findTableAlias(field), field);
    }


    @Override
    public final void appendParentOf(ChildTableMeta<?> childMeta,String childAlias) {
        validateTableAndAlias(childMeta,childAlias);
        this.sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(childMeta.parentMeta().tableName()));
        if (this.factoryMode != FactoryMode.NO_SHARDING) {
            this.sqlBuilder.append(obtainRouteSuffix(childMeta, childAlias));
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
    public final TablesContext primaryTableContext() {
        return this.primaryTableContext;
    }

    @Override
    public final TablesContext tableContext() {
        return this.tableContext;
    }



    @Override
    public final void appendIdentifier(String identifier) {
        sqlBuilder.append(" ")
                .append(dialect.quoteIfNeed(identifier));
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
     *     <li>append table name( and suffix if need) to builder</li>
     *     <li>append table alias to builder if need</li>
     * </ol>
     * <p>
     * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
     * </p>
     */
    protected final void doAppendTable(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        final Dialect dialect = this.dialect;
        StringBuilder builder = obtainTablePartBuilder();

        String actualTableName = tableMeta.tableName();
        if (this.factoryMode != FactoryMode.NO_SHARDING
                && !tableMeta.routeFieldList(false).isEmpty()) {
            //2.  route suffix
            actualTableName += obtainRouteSuffix(tableMeta, tableAlias);
        }
        //1. append table name
        builder.append(" ")
                .append(dialect.quoteIfNeed(actualTableName));
        if (canAppendTableAlias(tableMeta)) {
            //3. append table alias
            if (tableAlias == null) {
                throw new IllegalArgumentException(String.format(
                        "TableMeta[%s] table alias required int SQLContext[%s].", tableMeta, this));
            }
            validateTableAndAlias(tableMeta, tableAlias);
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
        if (this.factoryMode != FactoryMode.NO_SHARDING
                && (this.primaryRouteSuffix == null || !this.primaryRouteSuffix.startsWith("_"))) {
            throw new NotFoundRouteException("not found legal primary route[%s].", this.primaryRouteSuffix);
        }
    }


}
