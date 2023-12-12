package io.army.dialect;


import javax.annotation.Nullable;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _SingleUpdateContext}</li>
 *         <li>{@link _MultiUpdateContext}</li>
 *     </ul>
*/
public interface _UpdateContext extends _SqlContext, NarrowDmlContext, _DmlContext.ConditionFieldsSpec,
        _SetClauseContext {

    @Nullable
    @Override
    _UpdateContext parentContext();


}
