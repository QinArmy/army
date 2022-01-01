package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.IPredicate;
import io.army.criteria.Statement;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class OnClauses<C, OR> extends TableBlock implements Statement.OnClause<C, OR> {


    final TablePart tablePart;

    final JoinType joinType;

    final OR query;

    private List<_Predicate> onPredicates;

    OnClauses(TablePart tablePart, JoinType joinType, OR query) {
        super(tablePart, joinType);
        this.tablePart = tablePart;
        this.joinType = joinType;
        this.query = query;
    }

    @Override
    public final OR on(final List<IPredicate> predicateList) {
        final int size = predicateList.size();
        switch (size) {
            case 0:
                throw new CriteriaException("on clause must not empty.");
            case 1:
                this.onPredicates = Collections.singletonList((_Predicate) predicateList.get(0));
                break;
            default: {
                final List<_Predicate> temp = new ArrayList<>(size);
                for (IPredicate predicate : predicateList) {
                    temp.add((_Predicate) predicate);
                }
                this.onPredicates = Collections.unmodifiableList(temp);
            }
        }
        return this.query;
    }

    @Override
    public final OR on(IPredicate predicate) {
        this.onPredicates = Collections.singletonList((_Predicate) predicate);
        return this.query;
    }

    @Override
    public final OR on(IPredicate predicate1, IPredicate predicate2) {
        this.onPredicates = CriteriaUtils.onPredicates(predicate1, predicate2);
        return this.query;
    }

    @Override
    public final OR on(Function<C, List<IPredicate>> function) {
        return this.on(function.apply(this.getCriteria()));
    }

    @Override
    public final OR on(Supplier<List<IPredicate>> supplier) {
        return this.on(supplier.get());
    }

    @Override
    public final List<_Predicate> predicates() {
        final List<_Predicate> onPredicates = this.onPredicates;
        assert onPredicates != null;
        return onPredicates;
    }

    @Nullable
    abstract C getCriteria();


    static abstract class AliasOnClauses<C, OR> extends OnClauses<C, OR> {

        protected final String alias;

        protected AliasOnClauses(TablePart tablePart, String alias, JoinType joinType, OR query) {
            super(tablePart, joinType, query);
            this.alias = alias;
        }

        @Override
        public final String alias() {
            return this.alias;
        }


    }// AliasOnClauses


    static abstract class NoActionOnBlock<C, OR> implements Statement.OnClause<C, OR> {

        final OR query;

        NoActionOnBlock(OR query) {
            this.query = query;
        }

        @Override
        public final OR on(List<IPredicate> predicateList) {
            return this.query;
        }

        @Override
        public final OR on(IPredicate predicate) {
            return this.query;
        }

        @Override
        public final OR on(IPredicate predicate1, IPredicate predicate2) {
            return this.query;
        }

        @Override
        public final OR on(Function<C, List<IPredicate>> function) {
            return this.query;
        }

        @Override
        public final OR on(Supplier<List<IPredicate>> supplier) {
            return this.query;
        }

    }// NoActionOnBlock


}// OnClauses
