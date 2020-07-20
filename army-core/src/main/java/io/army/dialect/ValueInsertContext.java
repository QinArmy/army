package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.SimpleSQLWrapper;

final class ValueInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static ValueInsertContext build(InnerStandardInsert insert, ReadonlyWrapper beanWrapper
            , Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.valueInsertPrimaryRouteSuffix(tableMeta,dialect,beanWrapper);

        TableContext tableContext = TableContext.singleTable(tableMeta, "t",-1, primaryRouteSuffix);
        return new ValueInsertContext(dialect, visible, tableContext);
    }

    static ValueInsertContext buildParent(InnerStandardInsert insert, ReadonlyWrapper beanWrapper, Dialect dialect
            , final Visible visible) {
        ParentTableMeta<?> parentMeta = ((ChildTableMeta<?>) insert.tableMeta()).parentMeta();
        String primaryRouteSuffix = TableRouteUtils.valueInsertPrimaryRouteSuffix(parentMeta,dialect,beanWrapper);

        TableContext tableContext = TableContext.singleTable(parentMeta, "p",-1, primaryRouteSuffix);
        return new ValueInsertContext(dialect, visible, tableContext);
    }

    static ValueInsertContext buildChild(InnerStandardInsert insert, ReadonlyWrapper beanWrapper, Dialect dialect
            , final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.valueInsertPrimaryRouteSuffix(
                childMeta.parentMeta(),dialect,beanWrapper);

        TableContext tableContext = TableContext.singleTable(childMeta, "c",-1, primaryRouteSuffix);
        return new ValueInsertContext(dialect, visible, tableContext);
    }


    private final StringBuilder fieldBuilder = new StringBuilder();


    private final TableMeta<?> physicalTable;

    public ValueInsertContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible, tableContext);
        this.physicalTable = tableContext.singleTable();

    }


    @Override
    public final StringBuilder fieldsBuilder() {
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
        this.fieldBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    protected final void doAppendTableSuffix(TableMeta<?> actualTable, @Nullable String tableAlias
            , StringBuilder builder) {
        builder.append(this.tableContext.primaryRouteSuffix);
    }

    @Override
    public final SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.fieldBuilder.toString() + this.sqlBuilder.toString(), this.paramList);
    }
}
