package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;

final class StandardValueInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static StandardValueInsertContext build(InnerStandardInsert insert, @Nullable ReadonlyWrapper beanWrapper
            , Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        String primaryRouteSuffix = obtainPrimaryRouteSuffix(tableMeta, dialect, beanWrapper);

        TableContext tableContext = TableContext.singleTable(insert, false, primaryRouteSuffix);
        return new StandardValueInsertContext(dialect, visible, tableContext);
    }

    static StandardValueInsertContext buildParent(InnerStandardInsert insert, @Nullable ReadonlyWrapper beanWrapper
            , Dialect dialect
            , final Visible visible) {
        ParentTableMeta<?> parentMeta = ((ChildTableMeta<?>) insert.tableMeta()).parentMeta();

        String primaryRouteSuffix = obtainPrimaryRouteSuffix(parentMeta, dialect, beanWrapper);

        TableContext tableContext = TableContext.singleTable(insert, true, primaryRouteSuffix);
        return new StandardValueInsertContext(dialect, visible, tableContext);
    }

    static StandardValueInsertContext buildChild(InnerStandardInsert insert, @Nullable ReadonlyWrapper beanWrapper
            , Dialect dialect
            , final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        String primaryRouteSuffix = obtainPrimaryRouteSuffix(childMeta.parentMeta(), dialect, beanWrapper);

        TableContext tableContext = TableContext.singleTable(insert, false, primaryRouteSuffix);
        return new StandardValueInsertContext(dialect, visible, tableContext);
    }

    private static String obtainPrimaryRouteSuffix(TableMeta<?> tableMeta, Dialect dialect
            , @Nullable ReadonlyWrapper beanWrapper) {
        String primaryRouteSuffix;
        if (beanWrapper == null) {
            //no sharding
            primaryRouteSuffix = "";
        } else {
            // sharding
            primaryRouteSuffix = TableRouteUtils.valueInsertPrimaryRouteSuffix(tableMeta, dialect, beanWrapper);
        }
        return primaryRouteSuffix;

    }


    private final SQLBuilder fieldBuilder = DialectUtils.createSQLBuilder();

    private final TableMeta<?> physicalTable;

    private StandardValueInsertContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible, tableContext);
        this.physicalTable = tableContext.singleTable();
    }


    @Override
    public final SQLBuilder fieldsBuilder() {
        return this.fieldBuilder;
    }

    @Override
    public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.physicalTable) {
            throw DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        doAppendField(null, fieldMeta);
    }

    @Override
    protected final SQLBuilder obtainTablePartBuilder() {
        return this.fieldBuilder;
    }

    @Override
    protected final String parseTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        return this.tableContext.primaryRouteSuffix;
    }

    @Override
    protected final boolean canAppendTableAlias(TableMeta<?> tableMeta) {
        return false;
    }

    @Override
    public final SimpleStmt build() {
        return SimpleStmt.build(this.fieldBuilder.toString() + this.sqlBuilder.toString(), this.paramList);
    }
}
