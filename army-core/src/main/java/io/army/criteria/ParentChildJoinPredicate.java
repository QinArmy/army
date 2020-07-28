package io.army.criteria;

import io.army.meta.ChildTableMeta;

/**
 * design for select visible predicate
 */
public interface ParentChildJoinPredicate extends IPredicate {

    ChildTableMeta<?> childMeta();
}
