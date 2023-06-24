package io.army.stmt;

import io.army.criteria.Selection;

import java.util.List;


public interface GenericSimpleStmt extends Stmt {

    String sqlText();

    /**
     * @return a unmodifiable list
     */
    List<? extends Selection> selectionList();


    interface TowStmtQuerySpec {

    }

    interface FirstQueryStmt extends TowStmtQuerySpec {

    }


}
