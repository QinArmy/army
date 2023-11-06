package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import javax.annotation.Nullable;

final class ParenSubRowSetContext extends StatementContext implements _ParenRowSetContext {

    static ParenSubRowSetContext forSimple(@Nullable _SqlContext outerContext, ArmyParser parser, Visible visible) {
        return new ParenSubRowSetContext((StatementContext) outerContext, parser, visible);
    }


    static ParenSubRowSetContext forSimple(_SqlContext outerContext) {
        return new ParenSubRowSetContext((StatementContext) outerContext);
    }


    private final StatementContext outerContext;


    ParenSubRowSetContext(@Nullable StatementContext outerContext, ArmyParser parser, Visible visible) {
        super(outerContext, parser, visible);
        this.outerContext = outerContext;
    }

    ParenSubRowSetContext(StatementContext outerContext) {
        super(outerContext);
        this.outerContext = outerContext;
    }

    @Override
    public boolean hasOptimistic() {
        return false;
    }

    @Override
    public StmtType stmtType() {
        return StmtType.QUERY;
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

    @Override
    public void appendFieldOnly(final FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(field);
    }


    @Override
    public void appendOuterField(final String tableAlias, final FieldMeta<?> field) {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext == null) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        } else if (outerContext instanceof _ParenRowSetContext) {
            ((_ParenRowSetContext) outerContext).appendOuterField(tableAlias, field);
        } else {
            outerContext.appendField(tableAlias, field);
        }
    }

    @Override
    public void appendOuterField(final FieldMeta<?> field) {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext == null) {
            throw _Exceptions.unknownColumn(field);
        } else if (outerContext instanceof _ParenRowSetContext) {
            ((_ParenRowSetContext) outerContext).appendOuterField(field);
        } else if (outerContext instanceof _DmlContext._SingleTableContextSpec) {
            ((_DmlContext._SingleTableContextSpec) outerContext).appendFieldFromSub(field);
        } else {
            outerContext.appendField(field);
        }
    }


    @Override
    public void appendOuterFieldOnly(final FieldMeta<?> field) {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext == null) {
            throw _Exceptions.unknownColumn(field);
        } else if (outerContext instanceof _ParenRowSetContext) {
            ((_ParenRowSetContext) outerContext).appendOuterFieldOnly(field);
        } else if (outerContext instanceof _DmlContext._SingleTableContextSpec) {
            ((_DmlContext._SingleTableContextSpec) outerContext).appendFieldOnlyFromSub(field);
        } else {
            outerContext.appendFieldOnly(field);
        }
    }


}
