package io.army.dialect;

import io.army.stmt.SimpleStmt;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link  _SelectContext}</li>
 *         <li>{@link  _ValuesContext}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
interface PrimaryQueryContext extends _StmtContext {

    @Override
    SimpleStmt build();

}
