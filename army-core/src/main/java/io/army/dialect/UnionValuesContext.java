package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.Values;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._UnionRowSet0;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;

final class UnionValuesContext extends StatementContext implements _ValuesContext, _UnionQueryContext {


    static UnionValuesContext create(_UnionRowSet0 stmt, ArmyParser dialect, Visible visible) {
        assert stmt instanceof Values;
        return new UnionValuesContext(stmt, dialect, visible);
    }


    static UnionValuesContext create(_SqlContext outerContext) {
        return new UnionValuesContext(outerContext);
    }


    private final List<Selection> selectionList;
    private final _SqlContext outerContext;

    private UnionValuesContext(_UnionRowSet0 stmt, ArmyParser dialect, Visible visible) {
        super(dialect, visible);
        this.outerContext = null;
        this.selectionList = _DialectUtils.flatSelectItem(stmt.selectItemList());
        assert this.selectionList.size() > 0;
    }

    private UnionValuesContext(_SqlContext outerContext) {
        super((StatementContext) outerContext);
        this.outerContext = outerContext;
        this.selectionList = Collections.emptyList();
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
    public List<Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public SimpleStmt build() {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext != null) {
            throw new IllegalStateException("This context is inner context, don't support create Stmt.");
        }
        return Stmts.queryStmt(this);
    }


}
