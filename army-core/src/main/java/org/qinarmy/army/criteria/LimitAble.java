package org.qinarmy.army.criteria;

/**
 * created  on 2018/10/21.
 */
public interface LimitAble extends QueryAble {


    QueryAble limit(int rowCount);

    QueryAble limit(int offset, int rowCount);

}
