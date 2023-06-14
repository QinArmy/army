package io.army.stmt;

import io.army.criteria.SQLParam;
import io.army.criteria.Selection;

import java.util.List;

public interface BatchStmt extends GenericSimpleStmt {

    List<? extends Selection> selectionList();


    List<List<SQLParam>> groupList();

}
