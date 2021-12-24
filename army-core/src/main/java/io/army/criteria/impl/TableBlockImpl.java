package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner.TableBlock;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TableBlockImpl implements TableBlock {

    final TablePart tableAble;

    final String alias;

    final SQLModifier jointType;

    private List<IPredicate> onPredicateList = Collections.emptyList();

    private int dataSourceIndex = -1;

    private int tableIndex = -1;

    TableBlockImpl(TablePart tableAble, String alias, JoinType jointType) {
        this.tableAble = tableAble;
        this.alias = alias;
        this.jointType = jointType;
    }

    final void addOnPredicateList(List<IPredicate> predicateList) {
        _Assert.state(this.onPredicateList.isEmpty(), "on clause ended.");
        this.onPredicateList = Collections.unmodifiableList(new ArrayList<>(predicateList));
    }

    final void route(int dataSourceIndex, int tableIndex) {
        this.dataSourceIndex = dataSourceIndex;
        this.tableIndex = tableIndex;
    }

    final void route(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public final TablePart table() {
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

    @Override
    public final int databaseRoute() {
        return this.dataSourceIndex;
    }

    @Override
    public final int tableRoute() {
        return this.tableIndex;
    }
}
