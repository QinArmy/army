package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.criteria.impl.inner.InnerUpdate;

final class StandardUpdateContext extends AbstractSQLContext implements UpdateContext {

    private final InnerStandardUpdate innerUpdate;

    StandardUpdateContext(Dialect dialect, Visible visible, InnerStandardUpdate update) {
        super(dialect, visible);
        this.innerUpdate = update;
    }

    @Override
    public final InnerUpdate innerUpdate() {
        return this.innerUpdate;
    }


}
