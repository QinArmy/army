package io.army.dialect;

import io.army.meta.FieldMeta;

public interface _SubQueryContext extends _StmtContext {

    /**
     * <p>
     * Just append this context field,don't contain outer context field.
     * </p>
     */
    void appendThisField(String tableAlias, FieldMeta<?> field);

    /**
     * <p>
     * Just append this context field,don't contain outer context field.
     * </p>
     */
    void appendThisField(FieldMeta<?> field);

}
