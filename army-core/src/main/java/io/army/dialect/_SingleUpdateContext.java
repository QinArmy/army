package io.army.dialect;

import io.army.criteria.impl.inner._SingleDml;

/**
 * <p>
 * This interface representing dialect single table update syntax context.
 * </p>
 */
public interface _SingleUpdateContext extends _UpdateContext, _SingleSetClause {


    String safeTableAlias();

    _SingleDml statement();

}
