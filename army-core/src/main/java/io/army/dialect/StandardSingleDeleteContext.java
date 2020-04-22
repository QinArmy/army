package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerDelete;
import io.army.criteria.impl.inner.InnerStandardDelete;

final class StandardSingleDeleteContext extends AbstractSQLContext implements DeleteContext {

    private final InnerStandardDelete innerDelete;

    StandardSingleDeleteContext(Dialect dialect, Visible visible
            , InnerStandardDelete innerDelete) {
        super(dialect, visible);
        this.innerDelete = innerDelete;
    }

    @Override
    public final InnerDelete innerDelete() {
        return this.innerDelete;
    }
}
