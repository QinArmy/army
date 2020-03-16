package io.army.criteria;

import java.util.List;

public interface Row extends SelfDescribed{

    List<Expression<?>> columnList();

    IPredicate eq(RowSubQuery rowSubQuery);

    IPredicate notEq(RowSubQuery rowSubQuery);

    IPredicate lt(RowSubQuery rowSubQuery);

    IPredicate le(RowSubQuery rowSubQuery);

    IPredicate gt(RowSubQuery rowSubQuery);

    IPredicate ge(RowSubQuery rowSubQuery);

    IPredicate in(RowSubQuery rowSubQuery);
}
