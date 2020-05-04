package io.army.dialect;

import io.army.criteria.Visible;

final class StandardSelectContext extends AbstractStandardDQLContext implements SelectContext {

    static StandardSelectContext build(Dialect dialect, Visible visible) {
        return new StandardSelectContext(dialect, visible);
    }

    static StandardSelectContext build(ClauseSQLContext original) {
        return new StandardSelectContext(original);
    }


    StandardSelectContext(Dialect dialect, Visible visible) {
        super(dialect, visible);
    }

    StandardSelectContext(ClauseSQLContext original) {
        super(original);
    }


}
