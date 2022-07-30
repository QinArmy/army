package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._SingleDml;

final class SingleDeleteContext extends SingleDmlContext implements _SingleDeleteContext {

    static SingleDeleteContext create(_SingleDelete delete, ArmyParser dialect, Visible visible) {
        return new SingleDeleteContext(delete, dialect, visible);
    }


    private SingleDeleteContext(_SingleDml dml, ArmyParser dialect, Visible visible) {
        super(dml, dialect, visible);
    }

    private SingleDeleteContext(_SingleDml dml, StatementContext outerContext) {
        super(dml, outerContext);
    }


}
