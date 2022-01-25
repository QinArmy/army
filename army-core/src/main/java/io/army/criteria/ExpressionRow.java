package io.army.criteria;


public interface ExpressionRow extends Row {

    IPredicate eq(RowSubQuery rowSubQuery);

    IPredicate notEq(RowSubQuery rowSubQuery);

    IPredicate lt(RowSubQuery rowSubQuery);

    IPredicate le(RowSubQuery rowSubQuery);

    IPredicate gt(RowSubQuery rowSubQuery);

    IPredicate ge(RowSubQuery rowSubQuery);

    IPredicate in(RowSubQuery rowSubQuery);

    IPredicate notIn(RowSubQuery rowSubQuery);
}
