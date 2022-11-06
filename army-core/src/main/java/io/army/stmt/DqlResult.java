package io.army.stmt;

import io.army.criteria.Selection;

import java.util.List;

public interface DqlResult extends ResultItem {



    List<Selection> selectionList();


}
