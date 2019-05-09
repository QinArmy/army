package org.qinarmy.army.criteria;

/**
 * 代表可分组的元素
 * created  on 2018/10/8.
 */
public interface GroupElement<E> {


    Expression<E> asc();

    Expression<E> desc();


}
