package io.army.criteria.impl.inner;

import io.army.criteria.SelectPart;
import io.army.criteria.SortPart;

import java.util.List;

public interface _PartQuery extends _Statement {

    List<? extends SelectPart> selectPartList();

    List<SortPart> orderByList();

    long offset();

    long rowCount();

}
