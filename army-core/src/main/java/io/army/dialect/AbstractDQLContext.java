package io.army.dialect;

import io.army.criteria.Visible;

public class AbstractDQLContext extends AbstractTableContextSQLContext implements TableContextSQLContext {


    public AbstractDQLContext(Dialect dialect, Visible visible, TableContext fromContext) {
        super(dialect, visible, fromContext);
    }

    public AbstractDQLContext(TableContextSQLContext original, TableContext fromContext) {
        super(original, fromContext);
    }
}
