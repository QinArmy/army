package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerComposeQuery;
import io.army.criteria.impl.inner.InnerGeneralQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractComposeQuery<C> implements PartQuery, SelfDescribed
        , InnerComposeQuery {


    final C criteria;

    final CriteriaContext criteriaContext;

    private List<SortPart> orderPartList;

    private final InnerGeneralQuery generalQuery;

    private int offset = -1;

    private int rowCount = -1;

    private boolean prepared;

    AbstractComposeQuery(C criteria, QueryAble query) {
        this.criteria = criteria;
        this.generalQuery = (InnerGeneralQuery) query;
        Map<String, Selection> selectionMap = CriteriaUtils.createSelectionMap(this.generalQuery.selectPartList());
        this.criteriaContext = new CriteriaContextImpl<>(criteria, selectionMap);
        CriteriaContextHolder.setContext(this.criteriaContext);
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

    final void doIfLimit(Function<C, LimitOption> function) {
        LimitOption option = function.apply(this.criteria);
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
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

    final void asQuery() {
        if (this.orderPartList == null) {
            this.orderPartList = Collections.emptyList();
        } else {
            this.orderPartList = Collections.unmodifiableList(this.orderPartList);
        }
        this.prepared = true;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }

    @Override
    public List<SelectPart> selectPartList() {
        return this.generalQuery.selectPartList();
    }
    /*################################## blow InnerQueryAfterSet method ##################################*/

    @Override
    public final List<SortPart> orderByList() {
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


}
