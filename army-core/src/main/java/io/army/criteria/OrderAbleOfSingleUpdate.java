package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;

public interface OrderAbleOfSingleUpdate<T extends IDomain> extends LimitAbleOfSingleUpdate {

    OrderItemAbleOfSingleUpdate<T> orderBy(Expression<?> orderExp);

    OrderItemAbleOfSingleUpdate<T> orderBy(Expression<?> orderExp,@Nullable Boolean asc);
}
