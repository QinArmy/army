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

abstract class OnClauseTableBlock<C, OR> extends TableBlock implements Statement.OnClause<C, OR> {


    private List<_Predicate> onPredicates;

    OnClauseTableBlock(JoinType joinType, TablePart tablePart) {
        super(tablePart, joinType);
    }

    @Override
    public final OR on(final List<IPredicate> predicateList) {
        final int size = predicateList.size();
        final List<_Predicate> onPredicates;
        switch (size) {
            case 0:
                throw new CriteriaException("on clause must not empty.");
            case 1:
                onPredicates = Collections.singletonList((_Predicate) predicateList.get(0));
                break;
            default: {
                final List<_Predicate> temp = new ArrayList<>(size);
                for (IPredicate predicate : predicateList) {
                    temp.add((_Predicate) predicate);
                }
                onPredicates = Collections.unmodifiableList(temp);
            }
        }
        this.onPredicates = onPredicates;
        return this.endOnClause();
    }

    @Override
    public final OR on(IPredicate predicate) {
        this.onPredicates = Collections.singletonList((_Predicate) predicate);
        return this.endOnClause();
    }

    @Override
    public final OR on(IPredicate predicate1, IPredicate predicate2) {
        this.onPredicates = CriteriaUtils.onPredicates(predicate1, predicate2);
        return this.endOnClause();
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
        final List<_Predicate> predicateList = this.onPredicates;
        assert predicateList != null;
        return predicateList;
    }

    @Nullable
    abstract C getCriteria();

    abstract OR endOnClause();


}
