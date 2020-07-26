package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardChildSubQueryInsert;
import io.army.criteria.impl.inner.InnerStandardSubQueryInsert;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

final class SubQueryInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static SubQueryInsertContext build(InnerStandardSubQueryInsert insert, Dialect dialect, final Visible visible) {

        String primaryRouteSuffix = TableRouteUtils.singleTablePrimaryRouteSuffix(insert, dialect);

        TableContext tableContext = TableContext.singleTable(insert, false, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }

    static SubQueryInsertContext buildParent(InnerStandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        String primaryRouteSuffix = TableRouteUtils.singleTablePrimaryRouteSuffix(insert, dialect);

        TableContext tableContext = TableContext.singleTable(insert, true, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }

    static SubQueryInsertContext buildChild(InnerStandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        String primaryRouteSuffix = TableRouteUtils.singleTablePrimaryRouteSuffix(insert, dialect);

        TableContext tableContext = TableContext.singleTable(insert, false, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }


    private SubQueryInsertContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible, tableContext);
    }

    @Override
    public final StringBuilder fieldsBuilder() {
        return this.sqlBuilder;
    }

    @Override
    protected final String parseTableSuffix(TableMeta<?> actualTable, @Nullable String tableAlias) {
        GenericSessionFactory sessionFactory = this.dialect.sessionFactory();
        if (!sessionFactory.shardingSubQueryInsert()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Sub query insert isn't allowed by SessionFactory[%s]"
                    , sessionFactory);
        }
        return this.tableContext.primaryRouteSuffix;
    }
}
