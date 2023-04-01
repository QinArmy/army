package io.army.dialect;


import io.army.lang.Nullable;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _SingleUpdateContext}</li>
 *         <li>{@link _MultiUpdateContext}</li>
 *     </ul>
 * </p>
 */
public interface _UpdateContext extends _SqlContext, NarrowDmlContext, _DmlContext.ConditionFieldsSpec,
        _SetClauseContext {

    @Nullable
    @Override
    _UpdateContext parentContext();


}
