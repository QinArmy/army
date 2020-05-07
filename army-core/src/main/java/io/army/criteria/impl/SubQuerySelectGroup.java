package io.army.criteria.impl;

import io.army.criteria.SQLPart;
import io.army.criteria.SelectionGroup;
import io.army.criteria.SubQuery;

interface SubQuerySelectGroup extends SelectionGroup, SQLPart.SemiFinished {

    void finish(SubQuery subQuery, String subQueryAlias);
}
