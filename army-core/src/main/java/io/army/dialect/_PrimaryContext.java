package io.army.dialect;

import io.army.criteria.SQLParam;
import io.army.stmt.Stmt;

/**
 * <p>
 * Package interface ,This interface representing a statement context,extends {@link _SqlContext}
 * and add method for the implementation of {@link DialectParser}.
 *
 * @since 0.6.0
 */
public interface _PrimaryContext extends _SqlContext {

    /**
     * for appending updateTime field in set clause.
     *
     * @return true : currently exists {@link  SQLParam} in context
     * @see #appendParam(SQLParam)
     */
    boolean hasParam();

    boolean hasNamedLiteral();

    boolean hasLiteral();


    boolean hasOptimistic();


    Stmt build();

}
