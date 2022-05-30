package io.army.dialect;

import io.army.meta.SingleTableMeta;

public interface _MultiUpdateContext extends _UpdateContext, _MultiTableContext, _SetClauseContext {


    /**
     * <p>
     * This method for multi-table SET clause.
     * </p>
     */
    String tableAliasOf(SingleTableMeta<?> table);


}
