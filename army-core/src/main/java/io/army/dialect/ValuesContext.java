package io.army.dialect;

import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Values;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class ValuesContext extends StatementContext implements _ValuesContext {

    static ValuesContext create(_Values stmt, ArmyDialect dialect, Visible visible) {
        return new ValuesContext(stmt, dialect, visible);
    }

    static ValuesContext create(_SqlContext outerContext) {
        return new ValuesContext(outerContext);
    }


    private final List<Selection> selectionList;

    @SuppressWarnings("unchecked")
    private ValuesContext(_Values stmt, ArmyDialect dialect, Visible visible) {
        super(dialect, visible);

        final List<? extends SelectItem> selectItemList;
        selectItemList = stmt.selectItemList();
        final List<List<_Expression>> rowList;
        rowList = stmt.rowList();

        assert rowList.size() > 0;
        assert selectItemList.size() == rowList.get(0).size();

        this.selectionList = (List<Selection>) selectItemList;

    }

    private ValuesContext(_SqlContext outerContext) {
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
