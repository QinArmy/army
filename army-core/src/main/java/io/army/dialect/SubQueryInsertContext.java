package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardSubQueryInsert;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

final class SubQueryInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static SubQueryInsertContext build(InnerStandardSubQueryInsert insert, Dialect dialect, final Visible visible) {

        String primaryRouteSuffix = TableRouteUtils.singleTablePrimaryRouteSuffix(insert, dialect);

        TableContext tableContext = TableContext.singleTable(insert.tableMeta()
                , "t", insert.tableIndex(), primaryRouteSuffix);
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
    protected final void doAppendTableSuffix(TableMeta<?> actualTable, @Nullable String tableAlias
            , StringBuilder builder) {
        GenericSessionFactory sessionFactory = this.dialect.sessionFactory();
        if (!sessionFactory.shardingSubQueryInsert()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Sub query insert isn't allowed by SessionFactory[%s]"
                    , sessionFactory);
        }

        builder.append(this.tableContext.primaryRouteSuffix);
    }
}
