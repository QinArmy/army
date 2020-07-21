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

final class StandardValueInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static StandardValueInsertContext build(InnerStandardInsert insert, ReadonlyWrapper beanWrapper
            , Dialect dialect, final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.valueInsertPrimaryRouteSuffix(tableMeta, dialect, beanWrapper);

        TableContext tableContext = TableContext.singleTable(insert, primaryRouteSuffix);
        return new StandardValueInsertContext(dialect, visible, tableContext);
    }

    static StandardValueInsertContext buildParent(InnerStandardInsert insert, ReadonlyWrapper beanWrapper, Dialect dialect
            , final Visible visible) {
        ParentTableMeta<?> parentMeta = ((ChildTableMeta<?>) insert.tableMeta()).parentMeta();
        String primaryRouteSuffix = TableRouteUtils.valueInsertPrimaryRouteSuffix(parentMeta, dialect, beanWrapper);

        TableContext tableContext = TableContext.singleTable(insert, primaryRouteSuffix);
        return new StandardValueInsertContext(dialect, visible, tableContext);
    }

    static StandardValueInsertContext buildChild(InnerStandardInsert insert, ReadonlyWrapper beanWrapper, Dialect dialect
            , final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        String primaryRouteSuffix = TableRouteUtils.valueInsertPrimaryRouteSuffix(
                childMeta.parentMeta(), dialect, beanWrapper);

        TableContext tableContext = TableContext.singleTable(insert, primaryRouteSuffix);
        return new StandardValueInsertContext(dialect, visible, tableContext);
    }


    private final StringBuilder fieldBuilder = new StringBuilder();

    private StandardValueInsertContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible, tableContext);
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
        if (!this.tableContext.tableCountMap.containsKey(fieldMeta.tableMeta())) {
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
    protected final boolean canAppendTableAlias(TableMeta<?> tableMeta) {
        return false;
    }

    @Override
    public final SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.fieldBuilder.toString() + this.sqlBuilder.toString(), this.paramList);
    }
}
