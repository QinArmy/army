package io.army.dialect;

import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Values;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class SimpleValuesContext extends StatementContext implements _ValuesContext {

    static SimpleValuesContext create(@Nullable _SqlContext outerContext, _Values stmt, ArmyParser0 dialect
            , Visible visible) {
        return new SimpleValuesContext(stmt, dialect, visible);
    }

    static SimpleValuesContext create(_SqlContext outerContext) {
        return new SimpleValuesContext(outerContext);
    }


    private final List<Selection> selectionList;

    @SuppressWarnings("unchecked")
    private SimpleValuesContext(_Values stmt, ArmyParser0 dialect, Visible visible) {
        super(dialect, visible);

        final List<? extends SelectItem> selectItemList;
        selectItemList = stmt.selectItemList();
        final List<List<_Expression>> rowList;
        rowList = stmt.rowList();

        assert rowList.size() > 0;
        assert selectItemList.size() == rowList.get(0).size();

        this.selectionList = (List<Selection>) selectItemList;

    }

    private SimpleValuesContext(_SqlContext outerContext) {
        super((StatementContext) outerContext);
        this.selectionList = null;
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
            throw nonTopContext();
        }
        return Stmts.queryStmt(this);
    }


    @Override
    public List<Selection> selectionList() {
        return this.selectionList;
    }


}
