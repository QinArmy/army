package io.army.criteria.impl;

import io.army.criteria.SelectionGroup;
import io.army.criteria.SubQuery;

interface SubQuerySelectGroup extends SelectionGroup {

    void finish(SubQuery subQuery, String subQueryAlias);
}
