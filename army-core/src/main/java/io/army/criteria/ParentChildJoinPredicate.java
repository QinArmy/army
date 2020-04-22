package io.army.criteria;

import io.army.meta.ChildTableMeta;

public interface ParentChildJoinPredicate extends IPredicate {

    ChildTableMeta<?> childMeta();
}
