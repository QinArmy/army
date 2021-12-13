package io.army.criteria;


import io.army.criteria.impl.inner._SelfDescribed;
import io.army.domain.IDomain;

public interface ExpressionRow<T extends IDomain> extends _SelfDescribed, Row<T> {

    IPredicate eq(RowSubQuery rowSubQuery);

    IPredicate notEq(RowSubQuery rowSubQuery);

    IPredicate lt(RowSubQuery rowSubQuery);

    IPredicate le(RowSubQuery rowSubQuery);

    IPredicate gt(RowSubQuery rowSubQuery);

    IPredicate ge(RowSubQuery rowSubQuery);

    IPredicate in(RowSubQuery rowSubQuery);

    IPredicate notIn(RowSubQuery rowSubQuery);
}
