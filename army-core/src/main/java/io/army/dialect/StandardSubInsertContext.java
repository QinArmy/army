package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardChildSubQueryInsert;
import io.army.criteria.impl.inner.InnerStandardSubQueryInsert;

final class StandardSubInsertContext extends AbstractTableContextSQLContext {

    static StandardSubInsertContext build(InnerStandardSubQueryInsert insert, Dialect dialect, Visible visible) {
        return new StandardSubInsertContext(dialect, visible, TableContext.singleTable(insert.tableMeta(), "t"));
    }

    static StandardSubInsertContext buildParent(InnerStandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        return new StandardSubInsertContext(dialect, visible
                , TableContext.singleTable(insert.tableMeta().parentMeta(), "t"));
    }

    static StandardSubInsertContext buildChild(InnerStandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        return new StandardSubInsertContext(dialect, visible, TableContext.singleTable(insert.tableMeta(), "t"));
    }


    private StandardSubInsertContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible, tableContext);
    }

}
