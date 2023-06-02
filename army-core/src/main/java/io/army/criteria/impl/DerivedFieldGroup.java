package io.army.criteria.impl;

import io.army.criteria.RowElement;
import io.army.criteria.impl.inner._SelectionGroup;
import io.army.criteria.impl.inner._SelectionMap;

interface DerivedFieldGroup extends _SelectionGroup, RowElement.DelayElement {


    /**
     * @param table {@link io.army.criteria.impl.inner._DerivedTable} or {@link io.army.criteria.impl.inner._Cte}
     */
    void finish(_SelectionMap table, String alias);

}
