package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardChildSubQueryInsert;
import io.army.criteria.impl.inner.InnerStandardSubQueryInsert;
import io.army.meta.TableMeta;

final class SubQueryInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static SubQueryInsertContext build(InnerStandardSubQueryInsert insert, Dialect dialect, Visible visible) {
        return new SubQueryInsertContext(dialect, visible, TableContext.singleTable(insert.tableMeta(), "t")
                , insert.tableIndex());
    }

    static SubQueryInsertContext buildParent(InnerStandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        return new SubQueryInsertContext(dialect, visible
                , TableContext.singleTable(insert.tableMeta().parentMeta(), "t")
                , insert.tableIndex());
    }

    static SubQueryInsertContext buildChild(InnerStandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        return new SubQueryInsertContext(dialect, visible, TableContext.singleTable(insert.tableMeta(), "t")
                , insert.tableIndex());
    }

    private final int tableIndex;

    private SubQueryInsertContext(Dialect dialect, Visible visible, TableContext tableContext, int tableIndex) {
        super(dialect, visible, tableContext);
        this.tableIndex = tableIndex;
    }

    @Override
    public StringBuilder fieldsBuilder() {
        return this.sqlBuilder;
    }


    @Override
    protected void doAppendTableSuffix(TableMeta<?> actualTable, StringBuilder builder) {

        GenericSessionFactory sessionFactory = this.dialect.sessionFactory();
        if (!sessionFactory.shardingSubQueryInsert()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Sub query insert isn't allowed by SessionFactory[%s]"
                    , sessionFactory);
        }

        if (this.tableIndex < 0) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "SessionFactory[%s] Sub query insert,TableMeta[%s],not found route."
                    , sessionFactory.name(), this.tableContext.singleTable());
        } else {
            builder.append(sessionFactory.tableRoute(actualTable).convertToSuffix(this.tableIndex));
        }


    }
}
