package io.army.criteria.impl;

import io.army.criteria.DerivedTable;
import io.army.criteria.impl.inner._SelectionGroup;

interface DerivedGroup extends _SelectionGroup {

    String tableAlias();

    void finish(DerivedTable table, String alias);
}
