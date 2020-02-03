package io.army.criteria;

import io.army.domain.IDomain;

public interface OrderAscAbleOfSingleUpdate<T extends IDomain> extends LimitAbleOfSingleUpdate {

    LimitAbleOfSingleUpdate asc();

    LimitAbleOfSingleUpdate desc();

}
