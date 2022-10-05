package io.army.criteria.impl.inner;

import io.army.criteria.SelectItem;

import java.util.List;

public interface _Statement {

    void clear();

    interface _PredicateListSpec {

        List<_Predicate> predicateList();
    }

    interface _ReturningListSpec {

        List<? extends SelectItem> returningList();
    }


}
