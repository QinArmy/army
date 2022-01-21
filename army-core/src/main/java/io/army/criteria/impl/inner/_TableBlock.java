package io.army.criteria.impl.inner;

import io.army.criteria.TablePart;
import io.army.criteria.impl._JoinType;

import java.util.List;

public interface _TableBlock {

    TablePart table();

    String alias();

    _JoinType jointType();

    List<_Predicate> predicates();

}
