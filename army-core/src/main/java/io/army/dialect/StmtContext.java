package io.army.dialect;

import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;

import java.util.List;

/**
 * <p>
 * Package interface ,This interface representing a statement context,extends {@link _SqlContext}
 * and add method for the implementation of {@link _Dialect}.
 * </p>
 *
 * @since 1.0
 */
interface StmtContext extends _SqlContext {

    /**
     * for appending updateTime field in set clause.
     *
     * @return true : currently exists {@link  io.army.stmt.ParamValue} in context
     * @see #appendParam(ParamValue)
     */
    boolean hasParam();

    /**
     * for {@link  DmlContext#build(List)}
     *
     * @return true : currently exists {@link  io.army.criteria.NamedParam} in context
     * @see #appendParam(ParamValue)
     */
    boolean hasNamedParam();

    SimpleStmt build();

}
