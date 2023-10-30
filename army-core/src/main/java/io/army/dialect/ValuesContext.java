package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.Values;
import io.army.criteria.ValuesQuery;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._ValuesQuery;

import javax.annotation.Nullable;

import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class ValuesContext extends StatementContext implements _ValuesContext {

    static ValuesContext create(@Nullable _SqlContext outerContext, ValuesQuery stmt, ArmyParser dialect
            , Visible visible) {
        return new ValuesContext((StatementContext) outerContext, stmt, dialect, visible);
    }


    private final List<_Selection> selectionList;

    private ValuesContext(@Nullable StatementContext outerContext, ValuesQuery stmt, ArmyParser dialect
            , Visible visible) {
        super(outerContext, dialect, visible);
        if (outerContext == null && stmt instanceof Values) {
            this.selectionList = ((_ValuesQuery) stmt).selectItemList();
        } else {
            this.selectionList = null;
        }
    }


    @Override
    public boolean hasOptimistic() {
        return false;
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
    public SimpleStmt build() {
        if (this.selectionList == null) {
            //no bug,never here
            throw nonTopContext();
        }
        return Stmts.queryStmt(this);
    }


    @Override
    public List<? extends Selection> selectionList() {
        return this.selectionList;
    }


}
