package io.army.stmt;

import io.army.criteria.SQLParam;

import java.util.List;


/**
 * <p>
 * This interface representing a simple sql statement.
 * This interface have two important sub interfaces :
 *     <ul>
 *         <li>{@link GeneratedKeyStmt}</li>
 *         <li>{@link TwoStmtModeQueryStmt}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SimpleStmt extends GenericSimpleStmt {

    /**
     * @return a unmodifiable list
     */
    List<SQLParam> paramGroup();


}
