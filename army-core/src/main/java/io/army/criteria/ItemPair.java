package io.army.criteria;

import io.army.criteria.impl.SQLs;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>
 * This interface representing pair in SET clause for UPDATE statement.
 * </p>
 *
 * @see SQLs#itemPair(DataField, Object)
 * @see SQLs#itemPair(List, SubQuery)
 * @see Update._SimpleSetClause#setPairs(Consumer)
 * @see Update._SimpleSetClause#setPairs(BiConsumer)
 * @since 1.0
 */
public interface ItemPair {


}
