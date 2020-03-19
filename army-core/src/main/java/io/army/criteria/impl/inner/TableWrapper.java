package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.JoinType;
import io.army.criteria.TableAble;

import java.util.List;

@DeveloperForbid
public interface TableWrapper {

    TableAble tableAble();

    String alias();

    JoinType jointType();

    List<IPredicate> onPredicateList();
}
