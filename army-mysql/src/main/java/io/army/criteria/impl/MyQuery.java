package io.army.criteria.impl;

import io.army.criteria.SQLModifier;
import io.army.criteria.SelectPart;
import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

abstract class MyQuery<C, Q extends io.army.criteria.mysql.MySQLQuery>
        extends MySQLPartQuery<C, Q> {

    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList;

    private List<TableBlock> tableBlockList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<SortPart> groupByList;

    private List<_Predicate> havingList;

    private TableOnSpec<Q, C> noActionBlock;

    MyQuery(@Nullable C criteria) {
        super(criteria);
    }


}
