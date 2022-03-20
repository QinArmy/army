package io.army.stmt;

import io.army.criteria.Selection;

import java.util.List;

public interface SimpleStmt extends GenericSimpleStmt {

    /**
     * @return a unmodifiable list
     */
    List<ParamValue> paramGroup();

    /**
     * @return a unmodifiable list
     */
    List<Selection> selectionList();


}
