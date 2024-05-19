package io.army.dialect.impl;

import io.army.criteria.Selection;
import io.army.criteria.impl.inner._DeclareCursor;
import io.army.criteria.impl.inner._RowSet;
import io.army.meta.FieldMeta;
import io.army.session.SessionSpec;
import io.army.stmt.CursorStmtParams;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;

final class DeclareCursorContext extends StatementContext implements _CursorStmtContext, CursorStmtParams {


    static DeclareCursorContext create(@Nullable _SqlContext outerContext, _DeclareCursor stmt,
                                       ArmyParser parser, SessionSpec sessionSpec) {
        return new DeclareCursorContext((StatementContext) outerContext, stmt, parser, sessionSpec);
    }


    private final String name;

    private final String safeName;

    private final List<? extends Selection> selectionList;


    private DeclareCursorContext(@Nullable StatementContext parentOrOuterContext, _DeclareCursor stmt,
                                 ArmyParser parser, SessionSpec sessionSpec) {
        super(parentOrOuterContext, parser, sessionSpec);
        this.name = stmt.cursorName();
        this.safeName = parser.identifier(this.name);
        this.selectionList = _DialectUtils.flatSelectItem(((_RowSet._SelectItemListSpec) stmt.forQuery()).selectItemList());
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
    public boolean hasOptimistic() {
        // false
        return false;
    }


    @Override
    public List<? extends Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public StmtType stmtType() {
        return StmtType.UPDATE;
    }

    @Override
    public String cursorName() {
        return this.name;
    }

    @Override
    public String safeCursorName() {
        return this.safeName;
    }

    @Override
    public SimpleStmt build() {
        return Stmts.declareCursorStmt(this);
    }


}
