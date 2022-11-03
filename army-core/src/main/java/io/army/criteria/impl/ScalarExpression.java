package io.army.criteria.impl;

import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Query;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;

import java.util.List;

final class ScalarExpression extends OperationExpression {

    static ScalarExpression from(final SubQuery subQuery) {
        final List<? extends SelectItem> selectItemList;
        selectItemList = ((_Query) subQuery).selectItemList();
        if (!(selectItemList.size() == 1 && selectItemList.get(0) instanceof Selection)) {
            String m = String.format("Scalar sub query must only one %s .", Selection.class.getName());
            throw ContextStack.criteriaError(ContextStack.peek(), m);
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
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_LEFT_PAREN);

        context.parser().rowSet(this.subQuery, context);

        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

    }


}
