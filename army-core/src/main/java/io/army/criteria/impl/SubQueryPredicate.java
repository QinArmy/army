package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;

import java.util.List;

final class SubQueryPredicate extends OperationPredicate {


    static SubQueryPredicate create(Expression operand, DualOperator operator
            , SubQueryOperator subQueryOperator, SubQuery subQuery) {
        final List<? extends SelectItem> selectItemList;
        selectItemList = subQuery.selectItemList();
        if (selectItemList.size() != 1 || !(selectItemList.get(0) instanceof Selection)) {
            String m = String.format("Operator %s %s only support column sub query."
                    , operator.signText, subQueryOperator.name());
            throw new CriteriaException(m);
        }
        switch (subQueryOperator) {
            case ALL:
            case ANY:
            case SOME:
                return new SubQueryPredicate(operand, operator, subQueryOperator, subQuery);
            default:
                throw new IllegalArgumentException(String.format("SubQueryOperator[%s] error.", subQueryOperator));
        }
    }


    private final _Expression operand;

    private final DualOperator operator;

    private final SubQueryOperator subQueryOperator;

    private final SubQuery subQuery;

    private SubQueryPredicate(Expression operand, DualOperator operator, SubQueryOperator subQueryOperator, SubQuery subQuery) {
        this.operand = (_Expression) operand;
        this.operator = operator;
        this.subQueryOperator = subQueryOperator;
        this.subQuery = subQuery;
    }


    @Override
    public void appendSql(final _SqlContext context) {

        this.operand.appendSql(context);

        context.sqlBuilder()
                .append(this.operator.signText)
                .append(this.subQueryOperator.rendered());

        context.parser().rowSet(this.subQuery, context);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(this.operand)
                .append(this.operator.signText)
                .append(this.subQueryOperator.rendered());

        builder.append(this.subQuery);
        return builder.toString();
    }


}
