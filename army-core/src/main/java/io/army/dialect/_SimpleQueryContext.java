package io.army.dialect;


import io.army.stmt.SimpleStmt;

/**
 * <p>
 * This interface representing simple query context.
 *
 * @see _SelectContext
 * @see _SubQueryContext
 * @since 0.6.0
 */
public interface _SimpleQueryContext extends _PrimaryContext, _MultiTableStmtContext, SelectItemListContext {

    @Override
    SimpleStmt build();


}
