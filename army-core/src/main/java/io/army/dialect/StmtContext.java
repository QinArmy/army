package io.army.dialect;

import io.army.criteria.SqlParam;
import io.army.stmt.SimpleStmt;

/**
 * <p>
 * Package interface ,This interface representing a statement context,extends {@link _SqlContext}
 * and add method for the implementation of {@link DialectParser}.
 * </p>
 *
 * @since 1.0
 */
interface StmtContext extends _SqlContext {

    /**
     * for appending updateTime field in set clause.
     *
     * @return true : currently exists {@link  SqlParam} in context
     * @see #appendParam(SqlParam)
     */
    boolean hasParam();


    SimpleStmt build();

}
