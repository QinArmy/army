package io.army.dialect;

import io.army.meta.FieldMeta;

/**
 * <p>
 * Package interface,this interface representing sub query context.
 * </p>
 *
 * @see _SelectContext
 * @see _UnionQueryContext
 * @see _LateralSubQueryContext
 * @see _SimpleQueryContext
 * @since 1.0
 */
interface _SubQueryContext extends _SqlContext {

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
