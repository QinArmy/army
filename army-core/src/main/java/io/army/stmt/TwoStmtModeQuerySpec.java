package io.army.stmt;

/**
 * <p>This interface representing single sql statement that is a part of two sql statement.
 * <p>This interface is base interface of following :
 *     <ul>
 *         <li>{@link TwoStmtQueryStmt}</li>
 *         <li>{@link TwoStmtBatchQueryStmt}</li>
 *     </ul>
 * * @since 0.6.0
 */
public interface TwoStmtModeQuerySpec extends SingleSqlStmt.IdSelectionIndexSpec {

    int maxColumnSize();


}
