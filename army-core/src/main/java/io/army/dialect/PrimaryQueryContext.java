package io.army.dialect;

import io.army.stmt.SimpleStmt;

/**
 * <p>
 * Package interface, this interface is base interface of below:
 *     <ul>
 *         <li>{@link  _SelectContext}</li>
 *         <li>{@link  _ValuesContext}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
interface PrimaryQueryContext extends StmtContext {

    @Override
    SimpleStmt build();


}
