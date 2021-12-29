package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class QueryDmlStatement<C, WR, WA> implements Statement, Statement.WhereClause<C, WR, WA>
        , Statement.WhereAndClause<C, WA> {


    final C criteria;

    List<_Predicate> predicateList = new ArrayList<>();

    boolean prepared;

    QueryDmlStatement(@Nullable C criteria) {
        this.criteria = criteria;
    }


    @Override
    public final WR where(List<IPredicate> predicateList) {
        final List<_Predicate> predicates = this.predicateList;
        for (IPredicate predicate : predicateList) {
            predicates.add((_Predicate) predicate);
        }
        return (WR) this;
    }

    @Override
    public final WR where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public final WR where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public final WA where(IPredicate predicate) {
        Objects.requireNonNull(predicate);
        this.predicateList.add((_Predicate) predicate);
        return (WA) this;
    }

    @Override
    public final WA and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return (WA) this;
    }

    @Override
    public final WA and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final WA and(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }

    @Override
    public final WA ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Supplier<IPredicate> supplier) {
        return this.ifAnd(supplier.get());
    }

    @Override
    public final WA ifAnd(Function<C, IPredicate> function) {
        return this.ifAnd(function.apply(this.criteria));
    }


    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }


    public final List<_Predicate> predicateList() {
        _Assert.prepared(this.prepared);
        return this.predicateList;
    }


    void onClear() {

    }


}
