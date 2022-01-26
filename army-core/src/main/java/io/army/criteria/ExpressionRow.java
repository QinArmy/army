package io.army.criteria;


public interface ExpressionRow extends Row {

    IPredicate eq(SubQuery rowSubQuery);

    IPredicate notEq(SubQuery rowSubQuery);

    IPredicate lt(SubQuery rowSubQuery);

    IPredicate le(SubQuery rowSubQuery);

    IPredicate gt(SubQuery rowSubQuery);

    IPredicate ge(SubQuery rowSubQuery);

    IPredicate in(SubQuery rowSubQuery);

    IPredicate notIn(SubQuery rowSubQuery);
}
