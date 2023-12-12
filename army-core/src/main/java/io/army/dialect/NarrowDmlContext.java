package io.army.dialect;


/**
 * <p>
 * Packet interface,this interface is base interface of below:
 *     <ul>
 *         <li>{@link _UpdateContext}</li>
 *         <li>{@link _DeleteContext}</li>
 *     </ul>
*
 * @since 1.0
 */
interface NarrowDmlContext extends _DmlContext, BatchSpecContext {


}
