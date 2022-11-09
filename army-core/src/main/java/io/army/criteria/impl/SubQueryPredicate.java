package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.SubQuery;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.List;

final class SubQueryPredicate<I extends Item> extends OperationPredicate<I> {


    static <I extends Item> SubQueryPredicate<I> create(OperationExpression<I> left, DualOperator operator
            , SubQueryOperator queryOperator, SubQuery subQuery) {

        final SubQueryPredicate<I> predicate;
        switch (operator) {
            case LESS:
            case LESS_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
            case GREAT:
            case GREAT_EQUAL: {
                switch (queryOperator) {
                    case ALL:
                    case ANY:
                    case SOME:
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum(queryOperator);
                }
                assertColumnSubQuery(operator, queryOperator, subQuery);
                predicate = new SubQueryPredicate<>(left, operator, queryOperator, subQuery);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return predicate;
    }

    static <I extends Item> SubQueryPredicate<I> inOperator(final OperationExpression<I> left
            , final DualOperator operator, final SubQuery subQuery) {
        final SubQueryPredicate<I> predicate;
        switch (operator) {
            case IN:
            case NOT_IN: {
                assertColumnSubQuery(operator, null, subQuery);
                predicate = new SubQueryPredicate<>(left, operator, null, subQuery);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return predicate;

    }

    private static void assertColumnSubQuery(final DualOperator operator, final @Nullable SubQueryOperator queryOperator
            , final SubQuery subQuery) {
        final List<? extends SelectItem> selectItemList;
        selectItemList = subQuery.selectItemList();
        if (selectItemList.size() != 1 || !(selectItemList.get(0) instanceof Selection)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Operator ")
                    .append(operator.name());
            if (queryOperator != null) {
                builder.append(_Constant.SPACE)
                        .append(queryOperator.name());
            }
            builder.append(" only support column sub query.");
            throw ContextStack.criteriaError(ContextStack.peek(), builder.toString());
        }
    }


    private final ArmyExpression left;

    private final DualOperator operator;

    private final SubQueryOperator queryOperator;

    private final SubQuery subQuery;

    private SubQueryPredicate(OperationExpression<I> left, DualOperator operator
            , @Nullable SubQueryOperator subQueryOperator, SubQuery subQuery) {
        super(left.function);
        this.left = left;
        this.operator = operator;
        this.queryOperator = subQueryOperator;
        this.subQuery = subQuery;
    }


    @Override
    public void appendSql(final _SqlContext context) {

        this.left.appendSql(context);

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(this.operator.spaceOperator);

        final SubQueryOperator queryOperator = this.queryOperator;
        if (queryOperator != null) {
            sqlBuilder.append(this.queryOperator.rendered());
        }
        context.parser().scalarSubQuery(this.subQuery, context);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(this.left)
                .append(this.operator.spaceOperator);

        final SubQueryOperator queryOperator = this.queryOperator;
        if (queryOperator != null) {
            builder.append(this.queryOperator.rendered());
        }

        return builder.append(this.subQuery)
                .toString();
    }


}
