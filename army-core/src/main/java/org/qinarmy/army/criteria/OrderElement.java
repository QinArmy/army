package org.qinarmy.army.criteria;

/**
 * 代表可排序的元素
 * created  on 2018/10/8.
 */
public interface OrderElement<E> {

    Expression<E> asc();

    Expression<E> desc();

}
