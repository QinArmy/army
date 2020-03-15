package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.JoinType;
import io.army.criteria.TableAble;

import java.util.List;

public interface TableWrapper {

    TableAble getTableAble();

    String getAlias();

    JoinType getJointType();

    List<IPredicate> getPredicateList();
}
