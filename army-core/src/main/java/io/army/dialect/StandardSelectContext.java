package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerStandardSelect;

final class StandardSelectContext extends AbstractClauseContext implements SelectContext {

    private final InnerStandardSelect innerSelect;

    StandardSelectContext(Dialect dialect, Visible visible, InnerStandardSelect select) {
        super(dialect, visible);
        this.innerSelect = select;
    }


    @Override
    public final InnerSelect innerSelect() {
        return this.innerSelect;
    }
}
