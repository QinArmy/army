package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;

final class SingleDeleteContext extends SingleDmlContext implements _SingleDeleteContext {

    static SingleDeleteContext create(@Nullable _SqlContext outerContext, _SingleDelete delete
            , ArmyParser0 dialect, Visible visible) {
        return new SingleDeleteContext((StatementContext) outerContext, delete, dialect, visible);
    }


    private SingleDeleteContext(@Nullable StatementContext outerContext, _SingleDml dml
            , ArmyParser0 dialect, Visible visible) {
        super(outerContext, dml, dialect, visible);
    }


}
