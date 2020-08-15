package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractStandardSelect<C> extends AbstractSelect<C> implements
        Select.WhereAble<C>, Select.WhereAndAble<C>, Select.HavingAble<C>
        , Select.UnionClause<C>, Select.SelectPartAble<C>, Select.FromAble<C>
        , Select.TableRouteJoinAble<C>, Select.TableRouteOnAble<C> {


    private LockMode lockMode;


    AbstractStandardSelect(C criteria) {
        super(criteria);
    }


    /*################################## blow Select method ##################################*/


    /*################################## blow SelectPartAble method ##################################*/

    @Override
    public final <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelectClause(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public <S extends SelectPart> FromAble<C> select(Function<C, List<S>> function) {
        doSelectClause((Distinct) null, function.apply(this.criteria));
        return this;
    }

    @Override
    public final FromAble<C> select(Distinct distinct, SelectPart selectPart) {
        doSelectClause(distinct, selectPart);
        return this;
    }

    @Override
    public final FromAble<C> select(SelectPart selectPart) {
        doSelectClause((Distinct) null, selectPart);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList) {
        doSelectClause(distinct, selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromAble<C> select(List<S> selectPartList) {
        doSelectClause((Distinct) null, selectPartList);
        return this;
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public final TableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.NONE));
        return this;
    }

    @Override
    public final JoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.NONE));
        return this;
    }

    @Override
    public final JoinAble<C> fromRoute(int databaseIndex, int tableIndex) {
        doRouteClause(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final JoinAble<C> fromRoute(int tableIndex) {
        doRouteClause(-1, tableIndex);
        return this;
    }

    /*################################## blow JoinAble method ##################################*/

    @Override
    public final TableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.LEFT));
        return this;
    }

    @Override
    public final OnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.LEFT));
        return this;
    }

    @Override
    public final TableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.JOIN));
        return this;
    }

    @Override
    public final OnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.JOIN));
        return this;
    }

    @Override
    public final TableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.RIGHT));
        return this;
    }

    @Override
    public final OnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT));
        return this;
    }

    @Override
    public final OnAble<C> route(int databaseIndex, int tableIndex) {
        doRouteClause(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final OnAble<C> route(int tableIndex) {
        doRouteClause(-1, tableIndex);
        return this;
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public final Select.JoinAble<C> on(List<IPredicate> predicateList) {
        doOnClause(predicateList);
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(IPredicate predicate) {
        doOnClause(Collections.singletonList(predicate));
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(Function<C, List<IPredicate>> function) {
        doOnClause(function.apply(this.criteria));
        return this;
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public final Select.GroupByAble<C> where(List<IPredicate> predicateList) {
        addPredicateList(predicateList);
        return this;
    }

    @Override
    public final Select.GroupByAble<C> where(Function<C, List<IPredicate>> function) {
        addPredicateList(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> where(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public final WhereAndAble<C> and(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }


    @Override
    public final Select.WhereAndAble<C> ifAnd(Function<C, IPredicate> function) {
        IPredicate predicate = function.apply(this.criteria);
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }

    /*################################## blow GroupByAble method ##################################*/

    @Override
    public final HavingAble<C> groupBy(SortPart sortPart) {
        addGroupBy(sortPart);
        return this;
    }

    @Override
    public final HavingAble<C> groupBy(List<SortPart> sortPartList) {
        addGroupByList(sortPartList);
        return this;
    }

    @Override
    public final HavingAble<C> groupBy(Function<C, List<SortPart>> function) {
        addGroupByList(function.apply(this.criteria));
        return this;
    }

    /*################################## blow HavingAble method ##################################*/

    @Override
    public final Select.OrderByAble<C> having(Function<C, List<IPredicate>> function) {
        addHavingList(function.apply(this.criteria));
        return this;
    }

    @Override
    public final OrderByAble<C> having(List<IPredicate> predicateList) {
        addHavingList(predicateList);
        return this;
    }

    @Override
    public final Select.OrderByAble<C> having(IPredicate predicate) {
        addHaving(predicate);
        return this;
    }

    /*################################## blow OrderByAble method ##################################*/

    @Override
    public final LimitAble<C> orderBy(SortPart sortPart) {
        addOrderBy(sortPart);
        return this;
    }

    @Override
    public final LimitClause<C> orderBy(List<SortPart> sortPartList) {
        addOrderByList(sortPartList);
        return this;
    }

    @Override
    public final LimitAble<C> orderBy(Function<C, List<SortPart>> function) {
        addOrderByList(function.apply(this.criteria));
        return this;
    }

    /*################################## blow LimitAble method ##################################*/

    @Override
    public final Select.LockAble<C> limit(int rowCount) {
        doLimit(-1, rowCount);
        return this;
    }

    @Override
    public final Select.LockAble<C> limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Function<C, LimitOption> function) {
        LimitOption option = function.apply(this.criteria);
        if (option != null) {
            doLimit(option.offset(), option.rowCount());
        }
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(rowCount);
        }
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(offset, rowCount);
        }
        return this;
    }


}
