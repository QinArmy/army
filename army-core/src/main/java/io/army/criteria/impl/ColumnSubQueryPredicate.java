package io.army.criteria.impl;

import io.army.criteria.ColumnSubQuery;
import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;

import java.util.function.Function;

class ColumnSubQueryPredicate extends OperationPredicate {

    static <C> ColumnSubQueryPredicate create(
            Expression<?> operand, DualOperator operator
            , SubQueryOperator subQueryOperator, Function<C, ColumnSubQuery> function) {
        final ColumnSubQuery functionResult;
        functionResult = function.apply(CriteriaContextStack.getCriteria());
        assert functionResult != null;
        return ColumnSubQueryPredicate.create(operand, operator, subQueryOperator, functionResult);
    }

    static ColumnSubQueryPredicate create(Expression<?> operand, DualOperator operator
            , SubQueryOperator subQueryOperator, ColumnSubQuery subQuery) {
        switch (subQueryOperator) {
            case ALL:
            case ANY:
            case SOME:
                return new RelationColumnSubQueryPredicate(operand, operator, subQueryOperator, subQuery);
            default:
                throw new IllegalArgumentException(String.format("SubQueryOperator[%s] error.", subQueryOperator));
        }
    }

    static <C> ColumnSubQueryPredicate create(Expression<?> operand, DualOperator operator
            , Function<C, ColumnSubQuery> function) {
        final ColumnSubQuery functionResult;
        functionResult = function.apply(CriteriaContextStack.getCriteria());
        assert functionResult != null;
        return create(operand, operator, functionResult);
    }

    static ColumnSubQueryPredicate create(Expression<?> operand, DualOperator operator, ColumnSubQuery subQuery) {
        switch (operator) {
            case IN:
            case NOT_IN:
                return new ColumnSubQueryPredicate(operand, operator, subQuery);
            default:
                throw new IllegalArgumentException(String.format("operator[%s] error.", operator));
        }
    }

    private final _Expression<?> operand;

    private final DualOperator operator;

    private final ColumnSubQuery subQuery;

    private ColumnSubQueryPredicate(Expression<?> operand, DualOperator operator, ColumnSubQuery subQuery) {
        this.operand = (_Expression<?>) operand;
        this.operator = operator;
        this.subQuery = subQuery;
    }


    @Override
    public final void appendSql(final _SqlContext context) {

        this.operand.appendSql(context);

        final StringBuilder builder = context.sqlBuilder()
                .append(this.operator.rendered());

        if (this instanceof RelationColumnSubQueryPredicate) {
            builder.append(((RelationColumnSubQueryPredicate) this).subQueryOperator.rendered());
        }
        context.dialect().subQuery(this.subQuery, context);
    }

    @SuppressWarnings("all")
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.operand)
                .append(Constant.SPACE)
                .append(this.operator.rendered());
        if (this instanceof RelationColumnSubQueryPredicate) {
            builder.append(Constant.SPACE)
                    .append(((RelationColumnSubQueryPredicate) this).subQueryOperator.rendered());
        }
        return builder
                .append(this.subQuery)
                .toString();
    }




    private static final class RelationColumnSubQueryPredicate extends ColumnSubQueryPredicate {

        private final SubQueryOperator subQueryOperator;

        private RelationColumnSubQueryPredicate(Expression<?> operand, DualOperator operator
                , SubQueryOperator subQueryOperator, ColumnSubQuery subQuery) {
            super(operand, operator, subQuery);
            this.subQueryOperator = subQueryOperator;
        }

    }


}
