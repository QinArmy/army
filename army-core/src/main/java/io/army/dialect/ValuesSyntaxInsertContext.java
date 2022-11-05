package io.army.dialect;

import io.army.criteria.NullMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;

abstract class ValuesSyntaxInsertContext extends InsertContext implements _ValueInsertContext {


    final NullMode nullMode;


    ValuesSyntaxInsertContext(@Nullable StatementContext outerContext, _Insert._ValuesSyntaxInsert stmt
            , ArmyParser0 parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
        if (stmt instanceof _Insert._ChildInsert) {
            this.nullMode = ((_Insert._ValuesSyntaxInsert) ((_Insert._ChildInsert) stmt).parentStmt()).nullHandle();
        } else {
            this.nullMode = stmt.nullHandle();
        }
    }


    ValuesSyntaxInsertContext(_Insert._ChildInsert stmt, ValuesSyntaxInsertContext parentContext) {
        super(null, stmt, parentContext.parser, parentContext.visible);
        this.nullMode = ((_Insert._ValuesSyntaxInsert) stmt).nullHandle();
        assert this.nullMode == parentContext.nullMode;
    }


    static IllegalStateException parentStmtDontExecute(PrimaryFieldMeta<?> filed) {
        String m = String.format("parent stmt don't execute so %s parameter value is null", filed);
        return new IllegalStateException(m);
    }


}
