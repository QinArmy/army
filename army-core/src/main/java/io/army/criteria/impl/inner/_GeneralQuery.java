package io.army.criteria.impl.inner;

import io.army.criteria.SelectPart;

import java.util.List;

public interface _GeneralQuery extends _Statement {

    List<? extends SelectPart> selectPartList();

}
