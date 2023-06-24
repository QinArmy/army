package io.army.stmt;

import io.army.criteria.SQLParam;

import java.util.List;

public interface SimpleStmt extends GenericSimpleStmt {

    /**
     * @return a unmodifiable list
     */
    List<SQLParam> paramGroup();


}
