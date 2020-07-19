package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.NotFoundRouteException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

final class ValueInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static ValueInsertContext build(InnerStandardInsert insert, ReadonlyWrapper beanWrapper
            , Dialect dialect, final Visible visible) {
        return new ValueInsertContext(dialect, visible
                , TableContext.singleTable(insert.tableMeta(), "t")
                , beanWrapper
        );
    }

    static ValueInsertContext buildParent(InnerStandardInsert insert, ReadonlyWrapper beanWrapper, Dialect dialect
            , final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        return new ValueInsertContext(dialect, visible
                , TableContext.singleTable(childMeta.parentMeta(), "t")
                , beanWrapper
        );
    }

    static ValueInsertContext buildChild(InnerStandardInsert insert, ReadonlyWrapper beanWrapper, Dialect dialect
            , final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        return new ValueInsertContext(dialect, visible
                , TableContext.singleTable(childMeta, "t")
                , beanWrapper
        );
    }

    private final StringBuilder fieldBuilder = new StringBuilder();

    private final ReadonlyWrapper beanWrapper;

    private final TableMeta<?> physicalTable;

    public ValueInsertContext(Dialect dialect, Visible visible, TableContext tableContext
            , ReadonlyWrapper beanWrapper) {
        super(dialect, visible, tableContext);
        this.beanWrapper = beanWrapper;
        this.physicalTable = tableContext.singleTable();

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
    protected void doAppendTableSuffix(TableMeta<?> actualTable, StringBuilder builder) {

        List<FieldMeta<?, ?>> routeFields = actualTable.routeFieldList(false);
        // obtain route key
        Object routeKey = null;
        for (FieldMeta<?, ?> routeField : routeFields) {
            Object value = beanWrapper.getPropertyType(routeField.propertyName());
            if (value != null) {
                routeKey = value;
                break;
            }
        }
        if (routeKey == null) {
            throw new NotFoundRouteException("Value insert ,TableMeta[%s] not found route.", this.physicalTable);
        }
        // route table suffix by route key
        String tableSuffix = this.dialect.sessionFactory()
                .tableRoute(actualTable)
                .tableSuffix(routeKey);

        builder.append(tableSuffix);
    }

    @Override
    public SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.fieldBuilder.toString() + this.sqlBuilder.toString(), this.paramList);
    }
}
