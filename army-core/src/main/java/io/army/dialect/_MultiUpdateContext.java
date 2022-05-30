package io.army.dialect;

import io.army.meta.SingleTableMeta;

public interface _MultiUpdateContext extends UpdateContext, _MultiTableContext, _SetClauseContext {


    /**
     * <p>
     * This method for multi-table SET clause.
     * </p>
     */
    String tableAliasOf(SingleTableMeta<?> table);


}
