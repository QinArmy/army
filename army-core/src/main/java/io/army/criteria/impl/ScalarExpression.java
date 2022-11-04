package io.army.criteria.impl;

import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._RowSet;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import java.util.List;

final class ScalarExpression extends OperationExpression {

    static ScalarExpression from(final SubQuery subQuery) {
        final List<? extends SelectItem> selectItemList;
        selectItemList = ((_RowSet) subQuery).selectItemList();
        if (!(selectItemList.size() == 1 && selectItemList.get(0) instanceof Selection)) {
            throw ContextStack.criteriaError(ContextStack.peek(), _Exceptions::nonScalarSubQuery, subQuery);
        }
        return new ScalarExpression(subQuery);
    }



    private final SubQuery subQuery;

    private ScalarExpression(SubQuery subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public TypeMeta typeMeta() {
        return ((Selection) ((_Query) this.subQuery).selectItemList().get(0)).typeMeta();
    }


    @Override
    public void appendSql(final _SqlContext context) {
        context.parser().scalarSubQuery(this.subQuery, context);
    }


}
