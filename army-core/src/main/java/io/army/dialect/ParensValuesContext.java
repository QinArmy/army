package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.Values;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._PrimaryRowSet;
import io.army.criteria.impl.inner._Selection;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;

final class ParensValuesContext extends StatementContext implements _ValuesContext, _ParenRowSetContext {

    static ParensValuesContext create(@Nullable _SqlContext outerContext, Values values, ArmyParser parser,
                                      Visible visible) {
        return new ParensValuesContext((StatementContext) outerContext, values, parser, visible);
    }

    static ParensValuesContext forParens(_SqlContext original) {
        return new ParensValuesContext((StatementContext) original);
    }

    private final List<_Selection> selectionList;


    @SuppressWarnings("unchecked")
    private ParensValuesContext(@Nullable StatementContext parentOrOuterContext, Values values, ArmyParser parser,
                                Visible visible) {
        super(parentOrOuterContext, parser, visible);
        this.selectionList = (List<_Selection>) ((_PrimaryRowSet) values).selectItemList();
    }

    private ParensValuesContext(StatementContext original) {
        super(original, original.parser, original.visible);
        this.selectionList = null;
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
    public void appendOuterFieldOnly(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(field);
    }

    @Override
    public List<? extends Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public SimpleStmt build() {
        if (this.selectionList == null) {
            throw new IllegalStateException("non outer primary statement");
        }
        return Stmts.queryStmt(this);
    }


}
