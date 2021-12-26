package io.army.criteria.impl;

import io.army.criteria.ScalarQueryExpression;
import io.army.criteria.ScalarSubQuery;
import io.army.criteria.SelectPart;
import io.army.criteria.Selection;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;

final class ScalarSubQueryExpression<E> extends OperationExpression<E> implements ScalarQueryExpression<E> {

    static <E> ScalarSubQueryExpression<E> create(ScalarSubQuery<E> subQuery) {
        return new ScalarSubQueryExpression<>(subQuery);
    }


    private final ScalarSubQuery<E> subQuery;

    private ScalarSubQueryExpression(ScalarSubQuery<E> subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public List<? extends SelectPart> selectPartList() {
        return this.subQuery.selectPartList();
    }

    @Override
    public Selection selection(String derivedFieldName) {
        return this.subQuery.selection(derivedFieldName);
    }

    @Override
    public boolean requiredBrackets() {
        return this.subQuery.requiredBrackets();
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
    public ParamMeta paramMeta() {
        return this.subQuery.paramMeta();
    }

    @Override
    public boolean containsSubQuery() {
        return true;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        context.dialect().subQuery(this.subQuery, context);
    }

    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return super.containsField(fieldMetas);
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        return super.containsFieldOf(tableMeta);
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        return super.containsFieldCount(tableMeta);
    }


}
