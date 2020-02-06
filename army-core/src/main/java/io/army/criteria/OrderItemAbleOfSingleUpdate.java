package io.army.criteria;

import io.army.domain.IDomain;

public interface OrderItemAbleOfSingleUpdate<T extends IDomain> extends LimitAbleOfSingleUpdate {

    OrderItemAbleOfSingleUpdate<T> then(Expression<?> orderExp);

    OrderItemAbleOfSingleUpdate<T> then(Expression<?> orderExp, Boolean asc);

}
