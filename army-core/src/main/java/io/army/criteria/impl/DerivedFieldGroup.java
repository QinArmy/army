package io.army.criteria.impl;

import io.army.criteria.DerivedTable;
import io.army.criteria.impl.inner._SelectionGroup;

interface DerivedFieldGroup extends _SelectionGroup {


    void finish(DerivedTable table, String alias);
}
