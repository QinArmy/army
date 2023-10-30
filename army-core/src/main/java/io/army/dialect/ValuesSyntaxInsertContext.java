package io.army.dialect;

import io.army.criteria.NullMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;

import javax.annotation.Nullable;

import io.army.meta.PrimaryFieldMeta;

abstract class ValuesSyntaxInsertContext extends InsertContext implements _ValueSyntaxInsertContext {


    final NullMode nullMode;


    ValuesSyntaxInsertContext(@Nullable StatementContext outerContext, final _Insert._ValuesSyntaxInsert stmt,
                              ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);

        final _Insert._ValuesSyntaxInsert targetStmt;
        if (stmt instanceof _Insert._ChildInsert) {
            targetStmt = (_Insert._ValuesSyntaxInsert) ((_Insert._ChildInsert) stmt).parentStmt();
        } else {
            targetStmt = stmt;
        }
        this.nullMode = targetStmt.nullHandle();

    }


    ValuesSyntaxInsertContext(@Nullable StatementContext outerContext, _Insert._ChildInsert stmt
            , ValuesSyntaxInsertContext parentContext) {
        super(outerContext, stmt, parentContext);
        this.nullMode = ((_Insert._ValuesSyntaxInsert) stmt).nullHandle();
        assert this.nullMode == parentContext.nullMode;

    }


    static IllegalStateException parentStmtDontExecute(PrimaryFieldMeta<?> filed) {
        String m = String.format("parent stmt don't execute so %s parameter value is null", filed);
        return new IllegalStateException(m);
    }


}
