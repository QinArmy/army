package io.army.dialect;

import io.army.criteria.TableField;
import io.army.lang.Nullable;

public interface _SingleUpdateContext extends _UpdateContext, _SetBlock {

    /**
     * @return safe table alias of field, {@code null} representing field is unknown filed.
     */
    @Nullable
    String safeTableAlias(TableField<?> field);


}
