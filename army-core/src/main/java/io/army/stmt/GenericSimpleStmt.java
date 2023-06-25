package io.army.stmt;

import io.army.criteria.Selection;

import java.util.List;


public interface GenericSimpleStmt extends Stmt {

    String sqlText();

    /**
     * @return a unmodifiable list
     */
    List<? extends Selection> selectionList();


    interface IdSelectionIndexSpec {

        /**
         * @see GeneratedKeyStmt#idSelectionIndex()
         * @see TwoStmtModeQueryStmt#idSelectionIndex()
         */
        int idSelectionIndex();
    }


    interface TwoStmtQuerySpec {

    }

    interface FirstQueryStmt extends TwoStmtQuerySpec {

    }


}
