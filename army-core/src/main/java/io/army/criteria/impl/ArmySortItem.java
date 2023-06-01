package io.army.criteria.impl;

import io.army.criteria.SortItem;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.inner._SelfDescribed;

/**
 * package interface
 */
interface ArmySortItem extends SortItem, _SelfDescribed, TypeInfer {

    /**
     * @return always return this
     */
    @Override
    SortItem asSortItem();

}
