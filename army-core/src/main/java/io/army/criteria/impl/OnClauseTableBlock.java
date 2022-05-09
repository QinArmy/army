package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.Statement;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class OnClauseTableBlock<C, OR> extends TableBlock implements Statement._OnClause<C, OR> {

    private List<_Predicate> predicateList;

    final OR stmt;

    OnClauseTableBlock(_JoinType joinType, TableItem tableItem, String alias, OR stmt) {
        super(joinType, tableItem, alias);
        this.stmt = stmt;
    }


    @Override
    public final OR on(IPredicate predicate) {
        if (this.predicateList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.predicateList = Collections.singletonList((_Predicate) predicate);
        return this.stmt;
    }

    @Override
    public final OR on(IPredicate predicate1, IPredicate predicate2) {
        if (this.predicateList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.predicateList = ArrayUtils.asUnmodifiableList(
                (OperationPredicate) predicate1,
                (OperationPredicate) predicate2
        );
        return this.stmt;
    }

    @Override
    public final OR on(Function<C, List<IPredicate>> function) {
        if (this.predicateList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.predicateList = CriteriaUtils.asPredicateList(function.apply(this.getCriteria())
                , _Exceptions::predicateListIsEmpty);
        return this.stmt;
    }

    @Override
    public final OR on(Supplier<List<IPredicate>> supplier) {
        if (this.predicateList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.predicateList = CriteriaUtils.asPredicateList(supplier.get(), _Exceptions::predicateListIsEmpty);
        return this.stmt;
    }

    @Override
    public final OR on(Consumer<List<IPredicate>> consumer) {
        if (this.predicateList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list);
        this.predicateList = CriteriaUtils.asPredicateList(list, _Exceptions::predicateListIsEmpty);
        return this.stmt;
    }

    @Override
    public final List<_Predicate> predicates() {
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = Collections.emptyList();
        }
        return predicateList;
    }


    @Nullable
    @SuppressWarnings("unchecked")
    final C getCriteria() {
        return ((CriteriaSpec<C>) this.stmt).getCriteria();
    }


}
