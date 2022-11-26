package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._RowSet;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class ParensSelectContext extends StatementContext implements _SelectContext, _ParenRowSetContext {

    static ParensSelectContext create(@Nullable _SqlContext outerContext, Select select, ArmyParser dialect
            , Visible visible) {
        return new ParensSelectContext((StatementContext) outerContext, select, dialect, visible);
    }

    private final List<Selection> selectionList;

    private final _SqlContext outerContext;

    private ParensSelectContext(@Nullable StatementContext outerContext, Select select, ArmyParser dialect
            , Visible visible) {
        super(dialect, visible);
        this.outerContext = outerContext;
        this.selectionList = _DialectUtils.flatSelectItem(((_RowSet) select).selectItemList());
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
    public void appendOuterField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendOuterField(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(field);
    }

    @Override
    public SimpleStmt build() {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext != null) {
            //no bug,never here
            throw new UnsupportedOperationException();
        }
        return Stmts.queryStmt(this);
    }

    @Override
    public List<Selection> selectionList() {
        return this.selectionList;
    }


}
