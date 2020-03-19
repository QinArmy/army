package io.army.criteria.impl;

import io.army.criteria.Select;
import io.army.criteria.impl.inner.InnerSubQueryAble;

interface SubQuerySelect<C> extends InnerSubQueryAble, OuterQueryAble
        , Select.SelectPartAble<C>, Select.FromAble<C>, Select.JoinAble<C>
        , Select.OnAble<C>, Select.WhereAndAble<C>, Select.LimitAble<C>
        , Select.HavingAble<C> {


    static <C> SubQuerySelect<C> build(C criteria) {
        return new SubQueryMultiSelect<>(criteria);
    }

}
