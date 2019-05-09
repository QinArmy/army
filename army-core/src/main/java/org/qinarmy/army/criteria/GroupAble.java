package org.qinarmy.army.criteria;


/**
 * created  on 2018/10/21.
 */
public interface GroupAble extends HavingAble {


    HavingAble group(GroupElement<?>... groupElements);

}
