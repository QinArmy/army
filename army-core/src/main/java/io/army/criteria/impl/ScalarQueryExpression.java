package io.army.criteria.impl;

import io.army.criteria.ScalarExpression;
import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.SubQuery;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;

import java.util.List;

final class ScalarQueryExpression extends OperationExpression implements ScalarExpression {

    static ScalarQueryExpression from(SubQuery subQuery) {
        return new ScalarQueryExpression(subQuery);
    }


    final SubQuery subQuery;

    private ScalarQueryExpression(SubQuery subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public List<? extends SelectItem> selectItemList() {
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
    public TypeMeta typeMeta() {
        return this.subQuery.typeMeta();
    }


    @Override
    public void appendSql(final _SqlContext context) {
        context.parser().rowSet(this.subQuery, context);
    }


}
