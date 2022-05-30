package io.army.dialect;

import io.army.stmt.SimpleStmt;

/**
 * <p>
 * This interface representing a statement context,extends {@link _SqlContext}
 * and add method for the implementation of {@link _Dialect}.
 * </p>
 *
 * @since 1.0
 */
interface _StmtContext extends _SqlContext {

    /**
     * for append updateTime field in set clause.
     */
    boolean hasParam();

    boolean hasNamedParam();

    SimpleStmt build();

}
