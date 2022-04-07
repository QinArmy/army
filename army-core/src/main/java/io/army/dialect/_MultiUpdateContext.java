package io.army.dialect;

import io.army.criteria.TableField;
import io.army.meta.SingleTableMeta;

public interface _MultiUpdateContext extends _UpdateContext, _SetClause {


    /**
     * @return table alias(not safe table alias)
     */
    String parentTableAlias(TableField<?> field);

    /**
     * @return the table that tableAlias(not safe table alias) representing.
     */
    SingleTableMeta<?> tableOf(String tableAlias);

    String safeTableAlias(String tableAlias);

}
