package io.army.criteria.impl;

import io.army.criteria.QueryAfterSet;
import io.army.criteria.SQLContext;
import io.army.criteria.SelfDescribed;
import io.army.criteria.SortPart;
import io.army.criteria.impl.inner.InnerQueryAfterSet;
import io.army.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractComposeQuery<C> implements QueryAfterSet, InnerQueryAfterSet, SelfDescribed {


    final C criteria;

    private List<SortPart> orderPartList;

    private int offset = -1;

    private int rowCount = -1;

    AbstractComposeQuery(C criteria) {
        this.criteria = criteria;
    }

    final void doOrderBy(SortPart sortPart) {
        if (this.orderPartList == null) {
            this.orderPartList = new ArrayList<>(1);
        }
        this.orderPartList.add(sortPart);
    }

    final void doOrderBy(List<SortPart> sortPartList) {
        if (this.orderPartList == null) {
            this.orderPartList = new ArrayList<>(sortPartList.size());
        }
        this.orderPartList.addAll(sortPartList);
    }

    final void doOrderBy(Function<C, List<SortPart>> function) {
        doOrderBy(function.apply(this.criteria));
    }

    final void doOrderBy(Predicate<C> test, SortPart sortPart) {
        if (test.test(this.criteria)) {
            doOrderBy(sortPart);
        }
    }

    final void doOrderBy(Predicate<C> test, Function<C, List<SortPart>> function) {
        if (test.test(this.criteria)) {
            doOrderBy(function.apply(this.criteria));
        }
    }

    final void doLimit(int rowCount) {
        this.rowCount = rowCount;
    }

    final void doLimit(int offset, int rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
    }

    final void doLimit(Function<C, Pair<Integer, Integer>> function) {

        Pair<Integer, Integer> pair = function.apply(this.criteria);
        int offset = -1, rowCount = -1;
        if (pair.getFirst() != null) {
            offset = pair.getFirst();
        }
        if (pair.getSecond() != null) {
            rowCount = pair.getSecond();
        }
        doLimit(offset, rowCount);
    }

    final void doLimit(Predicate<C> test, int rowCount) {
        if (test.test(this.criteria)) {
            doLimit(rowCount);
        }
    }

    final void doLimit(Predicate<C> test, int offset, int rowCount) {
        if (test.test(this.criteria)) {
            doLimit(offset, rowCount);
        }
    }

    final void doLimit(Predicate<C> test, Function<C, Pair<Integer, Integer>> function) {
        if (test.test(this.criteria)) {
            doLimit(function);
        }
    }

    final void asQuery() {
        if (this.orderPartList == null) {
            this.orderPartList = Collections.emptyList();
        } else {
            this.orderPartList = Collections.unmodifiableList(this.orderPartList);
        }
    }

    /*################################## blow SelfDescribed method ##################################*/

    @Override
    public final void appendSQL(SQLContext context) {
        beforePart(context);
        context.dql().partQuery(this, context);
    }

    /*################################## blow InnerQueryAfterSet method ##################################*/

    @Override
    public final List<SortPart> orderPartList() {
        return this.orderPartList;
    }

    @Override
    public final int offset() {
        return this.offset;
    }

    @Override
    public final int rowCount() {
        return this.rowCount;
    }

    /*################################## blow package template method ##################################*/

    abstract void beforePart(SQLContext context);

}
