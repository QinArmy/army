package io.army.dialect;


/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _SingleUpdateContext}</li>
 *         <li>{@link _MultiUpdateContext}</li>
 *     </ul>
 * </p>
 */
interface UpdateContext extends _SqlContext, DmlContext, _SetClauseContext {


    void appendConditionFields();

}
