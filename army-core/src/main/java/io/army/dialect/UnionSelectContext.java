package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class UnionSelectContext extends StatementContext implements _UnionQueryContext, _SelectContext {

    static UnionSelectContext create(Select select, ArmyParser0 dialect, Visible visible) {
        return new UnionSelectContext(select, dialect, visible);
    }

    static UnionSelectContext create(Select select, _SelectContext outerContext) {
        return new UnionSelectContext(select, outerContext);
    }


    private final List<Selection> selectionList;

    private final _SelectContext outerContext;

    private UnionSelectContext(Select select, ArmyParser0 dialect, Visible visible) {
        super(dialect, visible);
        this.outerContext = null;
        this.selectionList = _DialectUtils.flatSelectItem(((_PartRowSet) select).selectItemList());
    }


    private UnionSelectContext(Select select, _SelectContext outerContext) {
        super((StatementContext) outerContext);
        this.selectionList = _DialectUtils.flatSelectItem(((_PartRowSet) select).selectItemList());
        this.outerContext = outerContext;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(null, field);
    }


    @Override
    public SimpleStmt build() {
        final _SelectContext outerContext = this.outerContext;
        if (outerContext != null) {
            throw new IllegalStateException("This context is inner context, don't support create Stmt.");
        }
        return Stmts.queryStmt(this);
    }

    @Override
    public List<Selection> selectionList() {
        return this.selectionList;
    }


}
