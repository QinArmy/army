package io.army.criteria.impl;

import io.army.criteria.SelectionGroup;
import io.army.criteria.SubQuery;

interface DerivedGroup extends SelectionGroup {

    String tableAlias();

    void finish(SubQuery subQuery, String subQueryAlias);
}
