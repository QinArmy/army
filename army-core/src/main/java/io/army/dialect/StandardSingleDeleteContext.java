package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardDelete;

final class StandardSingleDeleteContext extends AbstractTableContextSQLContext implements DeleteContext {

    private final InnerStandardDelete innerDelete;

    StandardSingleDeleteContext(Dialect dialect, Visible visible
            , InnerStandardDelete innerDelete) {
        super(dialect, visible, null);
        this.innerDelete = innerDelete;
    }


}
