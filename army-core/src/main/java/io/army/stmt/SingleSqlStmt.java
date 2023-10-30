package io.army.stmt;

import io.army.criteria.Selection;

import java.util.List;


/**
 * <p>This interface representing  single sql statement.
 * <p>This interface is base interface of following :
 *     <ul>
 *         <li>{@link SimpleStmt}</li>
 *         <li>{@link BatchStmt}</li>
 *     </ul>
 * </p>
 *
 * @see PairStmt
 * @since 1.0
 */
public interface SingleSqlStmt extends Stmt {

    String sqlText();

    /**
     * @return a unmodifiable list
     */
    List<? extends Selection> selectionList();


    interface IdSelectionIndexSpec {

        /**
         * @see GeneratedKeyStmt#idSelectionIndex()
         * @see TwoStmtQueryStmt#idSelectionIndex()
         */
        int idSelectionIndex();
    }


}
