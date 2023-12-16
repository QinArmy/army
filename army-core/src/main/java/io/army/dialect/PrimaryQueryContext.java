package io.army.dialect;

import io.army.stmt.SimpleStmt;

/**
 * <p>
 * Package interface, this interface is base interface of below:
 * <ul>
 *     <li>{@link  _SelectContext}</li>
 *     <li>{@link  _ValuesContext}</li>
 * </ul>
 *
 * @since 0.6.0
 */
interface PrimaryQueryContext extends _PrimaryContext {

    @Override
    SimpleStmt build();


}
