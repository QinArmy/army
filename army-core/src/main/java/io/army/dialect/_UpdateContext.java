package io.army.dialect;


import io.army.stmt.SimpleStmt;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _DomainUpdateContext}</li>
 *         <li>{@link _MultiUpdateContext}</li>
 *     </ul>
 * </p>
 */
interface _UpdateContext extends _DmlContext {


    @Override
    SimpleStmt build();

}
