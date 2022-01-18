package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class DmlWhereClause<C, WR, WA> implements Statement, Statement.WhereClause<C, WR, WA>
        , Statement.WhereAndClause<C, WA>, _Statement {


    final C criteria;

    List<_Predicate> predicateList = new ArrayList<>();

    DmlWhereClause(@Nullable C criteria) {
        this.criteria = criteria;
    }


    @Override
    public final WR where(List<IPredicate> predicateList) {
        final List<_Predicate> predicates = this.predicateList;
        for (IPredicate predicate : predicateList) {
            if (!(predicate instanceof OperationPredicate)) {
                throw CriteriaUtils.nonArmyExpression(predicate);
            }
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
    public final WA where(final @Nullable IPredicate predicate) {
        if (predicate != null) {
            if (!(predicate instanceof OperationPredicate)) {
                throw CriteriaUtils.nonArmyExpression(predicate);
            }
            this.predicateList.add((_Predicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA and(IPredicate predicate) {
        if (!(predicate instanceof OperationPredicate)) {
            throw CriteriaUtils.nonArmyExpression(predicate);
        }
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
            if (!(predicate instanceof OperationPredicate)) {
                throw CriteriaUtils.nonArmyExpression(predicate);
            }
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


    public final List<_Predicate> predicateList() {
        prepared();
        return this.predicateList;
    }


}
