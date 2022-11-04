package io.army.dialect;

import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

final class ParenRowSetContext extends StatementContext implements _ParenRowSetContext {

    static ParenRowSetContext create(@Nullable _SqlContext outerContext, ArmyParser parser, Visible visible) {
        return new ParenRowSetContext((StatementContext) outerContext, parser, visible);
    }


    static ParenRowSetContext create(_SqlContext outerContext) {
        return new ParenRowSetContext((StatementContext) outerContext);
    }

    private final StatementContext outerContext;


    ParenRowSetContext(StatementContext outerContext, ArmyParser parser, Visible visible) {
        super(outerContext, parser, visible);
        this.outerContext = null;
    }

    ParenRowSetContext(StatementContext outerContext) {
        super(outerContext);
        this.outerContext = outerContext;
    }

    @Override
    public SimpleStmt build() {
        if (this.outerContext != null) {
            throw new UnsupportedOperationException();
        }
        return Stmts.queryStmt(this);
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(field);
    }


}
