package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.SelectList;

import java.util.List;

/**
 * created  on 2018/10/21.
 */
public abstract class DLS extends StandardFunc {


    public static SelectList select(Object... selections) {
        return new SelectedListImpl(selections);
    }


    public static SelectList select(List<?> selectionList) {
        return new SelectedListImpl(selectionList);
    }


}
