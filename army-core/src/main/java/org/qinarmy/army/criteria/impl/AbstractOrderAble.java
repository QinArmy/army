package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.LimitAble;
import org.qinarmy.army.criteria.OrderAble;
import org.qinarmy.army.criteria.OrderElement;
import org.qinarmy.army.util.ArrayUtils;

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
