package io.army.criteria;

import io.army.criteria.impl.inner._Statement;

import java.util.List;

public interface _PartQuery extends _Statement {

    List<SortPart> orderByList();

    long offset();

    long rowCount();

}
