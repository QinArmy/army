package io.army.dialect;


import io.army.stmt.SimpleStmt;

/**
 * <p>
 * This interface representing simple query context.
 * </p>
 *
 * @see _SelectContext
 * @see _SubQueryContext
 * @since 1.0
 */
public interface _SimpleQueryContext extends _PrimaryContext, _MultiTableStmtContext, SelectionListContext {

    @Override
    SimpleStmt build();


}
