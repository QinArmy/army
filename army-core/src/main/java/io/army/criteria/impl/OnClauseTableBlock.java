package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.IPredicate;
import io.army.criteria.Statement;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class OnClauseTableBlock<C, OR> extends TableBlock implements Statement.OnClause<C, OR> {

    final String alias;

    private List<_Predicate> onPredicates;

    OnClauseTableBlock(_JoinType joinType, TablePart tablePart, String alias) {
        super(joinType, tablePart);
        this.alias = alias;
    }

    @Override
    public final OR on(final List<IPredicate> predicateList) {
        final int size = predicateList.size();
        final List<_Predicate> onPredicates;
        switch (size) {
            case 0:
                throw new CriteriaException("on clause must not empty.");
            case 1:
                onPredicates = Collections.singletonList((OperationPredicate) predicateList.get(0));
                break;
            default: {
                final List<_Predicate> temp = new ArrayList<>(size);
                for (IPredicate predicate : predicateList) {
                    temp.add((OperationPredicate) predicate);
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
        return this.on(function.apply(this.getCriteriaContext().criteria()));
    }

    @Override
    public final OR on(Supplier<List<IPredicate>> supplier) {
        return this.on(supplier.get());
    }

    @Override
    public final OR on(Consumer<List<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list);
        return this.on(list);
    }

    @Override
    public final List<_Predicate> predicates() {
        final List<_Predicate> predicateList = this.onPredicates;
        assert predicateList != null;
        return predicateList;
    }

    @Override
    public final String alias() {
        return this.alias;
    }

    abstract CriteriaContext getCriteriaContext();

    abstract OR endOnClause();


}
