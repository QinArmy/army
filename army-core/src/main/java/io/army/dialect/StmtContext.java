package io.army.dialect;

import io.army.stmt.SimpleStmt;
import io.army.stmt.SqlParam;

import java.util.List;

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

    /**
     * for {@link  DmlContext#build(List)}
     *
     * @return true : currently exists {@link  io.army.criteria.NamedParam} in context
     * @see #appendParam(SqlParam)
     */
    boolean hasNamedParam();

    SimpleStmt build();

}
