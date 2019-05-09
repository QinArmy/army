package org.qinarmy.army.criteria;

/**
 * created  on 2018/10/21.
 */
public interface OrderAble extends LimitAble {


    LimitAble order(OrderElement<?>... orderElements);

}
