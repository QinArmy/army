package io.army.stmt;

import io.army.criteria.Selection;
import io.army.criteria.SqlParam;

import java.util.List;

public interface SimpleStmt extends GenericSimpleStmt {

    /**
     * @return a unmodifiable list
     */
    List<SqlParam> paramGroup();

    /**
     * @return a unmodifiable list
     */
    List<Selection> selectionList();


}
