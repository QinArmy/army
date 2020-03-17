package io.army.criteria;

import java.util.List;

public interface SelectionGroup extends SQLClause.SelectClause {

    String tableAlias();

    List<Selection> selectionList();

}
