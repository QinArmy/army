package io.army.dialect;

import io.army.meta.FieldMeta;

/**
 * <p>
 * This interface representing dialect single table update syntax context.
 * </p>
 */
public interface _SingleUpdateContext extends _UpdateContext, _SingleTableContext, _SetClauseContext {

    void appendParentField(FieldMeta<?> parentField);

}
