package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardUpdate;

final class StandardUpdateContext extends AbstractClauseContext implements UpdateContext {

    private final InnerStandardUpdate innerUpdate;

    StandardUpdateContext(Dialect dialect, Visible visible, InnerStandardUpdate update) {
        super(dialect, visible);
        this.innerUpdate = update;
    }



}
