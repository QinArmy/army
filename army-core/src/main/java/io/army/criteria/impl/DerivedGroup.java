package io.army.criteria.impl;

import io.army.criteria.DerivedTable;
import io.army.criteria.SelectionGroup;

interface DerivedGroup extends SelectionGroup {

    String tableAlias();

    void finish(DerivedTable table, String alias);
}
