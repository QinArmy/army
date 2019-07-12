package io.army.criteria.impl;

import io.army.criteria.LimitAble;
import io.army.criteria.OrderAble;
import io.army.criteria.OrderElement;
import io.army.util.ArrayUtils;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2019-01-31.
 */
abstract class AbstractOrderAble extends AbstractLimitAble implements OrderAble {

    private List<OrderElement<?>> orderElementList = Collections.emptyList();

    @Override
    public final LimitAble order(OrderElement<?>... orderElements) {
        orderElementList = ArrayUtils.asUnmodifiableList(orderElements);
        return this;
    }
}
