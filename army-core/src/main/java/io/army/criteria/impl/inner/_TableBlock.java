package io.army.criteria.impl.inner;

import io.army.criteria.TableItem;
import io.army.criteria.impl._JoinType;

import java.util.List;

public interface _TableBlock {

    TableItem tableItem();

    String alias();

    _JoinType jointType();

    List<_Predicate> predicates();

}
