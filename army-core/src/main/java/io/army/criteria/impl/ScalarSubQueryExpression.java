package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.stmt.Stmt;

import java.util.List;

final class ScalarSubQueryExpression extends OperationExpression implements ScalarExpression {

    static ScalarSubQueryExpression create(ScalarSubQuery subQuery) {
        return new ScalarSubQueryExpression(subQuery);
    }


    final ScalarSubQuery subQuery;

    private ScalarSubQueryExpression(ScalarSubQuery subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public List<SelectItem> selectItemList() {
        return this.subQuery.selectItemList();
    }

    @Override
    public Selection selection(String derivedFieldName) {
        return this.subQuery.selection(derivedFieldName);
    }

    @Override
    public Selection selection() {
        return this.subQuery.selection();
    }

    @Override
    public void prepared() {
        this.subQuery.prepared();
    }

    @Override
    public boolean isPrepared() {
        return this.subQuery.isPrepared();
    }

    @Override
    public ParamMeta paramMeta() {
        return this.subQuery.paramMeta();
    }


    @Override
    public void appendSql(final _SqlContext context) {
        context.dialect().rowSet(this.subQuery, context);
    }


    @Override
    public String mockAsString(Dialect dialect, Visible visible, boolean none) {
        throw new UnsupportedOperationException("dont support SubQuery.");
    }

    @Override
    public Stmt mockAsStmt(Dialect dialect, Visible visible) {
        throw new UnsupportedOperationException("dont support SubQuery.");
    }


}
