package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.SimpleSQLWrapper;

final class StandardInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static StandardInsertContext build(InnerStandardInsert insert, Dialect dialect, Visible visible) {
        return new StandardInsertContext(dialect, visible
                , TableContext.singleTable(insert.tableMeta(), "t"), insert.tableMeta());
    }


    static StandardInsertContext buildParent(InnerStandardInsert insert, Dialect dialect, Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        return new StandardInsertContext(dialect, visible
                , TableContext.singleTable(childMeta.parentMeta(), "t"), childMeta.parentMeta());
    }

    static StandardInsertContext buildChild(InnerStandardInsert insert, Dialect dialect, Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        return new StandardInsertContext(dialect, visible
                , TableContext.singleTable(childMeta, "t"), childMeta);
    }

    private final StringBuilder fieldBuilder = new StringBuilder();

    private final TableMeta<?> physicalTable;

    public StandardInsertContext(Dialect dialect, Visible visible, TableContext tableContext
            , TableMeta<?> physicalTable) {
        super(dialect, visible, tableContext);
        this.physicalTable = physicalTable;
    }


    @Override
    public final StringBuilder fieldsBuilder() {
        return this.fieldBuilder;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
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
    public SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.fieldBuilder.toString() + this.sqlBuilder.toString(), this.paramList);
    }
}
