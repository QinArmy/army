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
public interface _UpdateContext extends _SqlContext, NarrowDmlContext, DmlContext.ConditionFieldsSpec
        , _SetClauseContext {


    @Override
    _UpdateContext parentContext();


}
