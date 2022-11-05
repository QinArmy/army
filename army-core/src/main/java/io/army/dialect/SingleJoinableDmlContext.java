package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

abstract class SingleJoinableDmlContext extends SingleDmlStmtContext {


    SingleJoinableDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt
            , ArmyParser0 parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
    }

    SingleJoinableDmlContext(_SingleDml stmt, SingleDmlStmtContext parentContext) {
        super(stmt, parentContext);
    }


    @Override
    public final void appendField(String tableAlias, FieldMeta<?> field) {

    }

    @Override
    public final void appendField(FieldMeta<?> field) {

    }


}
