package io.army.criteria.impl;

import io.army.criteria.ScalarQueryExpression;
import io.army.criteria.ScalarSubQuery;
import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;

import java.util.List;

final class ScalarSubQueryExpression extends OperationExpression implements ScalarQueryExpression {

    static ScalarSubQueryExpression create(ScalarSubQuery subQuery) {
        return new ScalarSubQueryExpression(subQuery);
    }


    final ScalarSubQuery subQuery;

    private ScalarSubQueryExpression(ScalarSubQuery subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public List<? extends SelectItem> selectPartList() {
        return this.subQuery.selectPartList();
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
        context.dialect().subQuery(this.subQuery, context);
    }


}
