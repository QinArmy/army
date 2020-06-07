package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.TableAble;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TableWrapperImpl implements TableWrapper {

    final TableAble tableAble;

    final String alias;

    final SQLModifier jointType;

    private List<IPredicate> onPredicateList = new ArrayList<>();

    TableWrapperImpl(TableAble tableAble, String alias, JoinType jointType) {
        this.tableAble = tableAble;
        this.alias = alias;
        this.jointType = jointType;
    }

    final void addOnPredicateList(List<IPredicate> predicateList) {
        Assert.state(this.onPredicateList.isEmpty(), "on clause ended.");
        this.onPredicateList.addAll(predicateList);
        this.onPredicateList = Collections.unmodifiableList(this.onPredicateList);
    }

    public final TableAble tableAble() {
        return tableAble;
    }

    public final String alias() {
        return alias;
    }

    public final SQLModifier jointType() {
        return jointType;
    }

    public final List<IPredicate> onPredicateList() {
        return onPredicateList;
    }
}
