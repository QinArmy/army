package io.army.criteria.postgre;

import io.army.criteria.SelectPart;
import io.army.criteria.Selection;
import io.army.criteria.SelfDescribed;

import java.util.List;

public interface PostgreWithQuery extends SelfDescribed {

    List<SelectPart> selectPartList();

    Selection selection(String selectionAlia);

    String withQueryName();
}
