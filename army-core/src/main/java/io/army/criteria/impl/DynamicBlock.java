package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.standard.StandardQuery;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated
abstract class DynamicBlock<C> implements JoinItemBlock<C>, ItemBlock<C>, Statement._OnClause<C, JoinItemBlock<C>> {

    static <C> StandardQuery._IfOnClause<C> standard(@Nullable C criteria, TabularItem tableItem, String alias) {
        return new StandardDynamicBlock<>(criteria, tableItem, alias);
    }

    final CriteriaContext criteriaContext;

    final TabularItem tableItem;

    final String alias;

    private List<_Predicate> predicateList;

    DynamicBlock(@Nullable C criteria, TabularItem tableItem, String alias) {
        this.criteriaContext = ContextStack.getCurrentContext(criteria);
        this.tableItem = tableItem;
        this.alias = alias;
    }

    DynamicBlock(TabularItem tableItem, String alias, CriteriaContext criteriaContext) {
        this.criteriaContext = criteriaContext;
        this.tableItem = tableItem;
        this.alias = alias;
    }


    @Override
    public final JoinItemBlock<C> on(IPredicate predicate) {
        return this.onClauseEnd(Collections.singletonList((OperationPredicate) predicate));
    }

    @Override
    public final JoinItemBlock<C> on(IPredicate predicate1, IPredicate predicate2) {
        final List<_Predicate> list;
        list = ArrayUtils.asUnmodifiableList(
                (OperationPredicate) predicate1,
                (OperationPredicate) predicate2
        );
        return this.onClauseEnd(list);
    }

    @Override
    public final JoinItemBlock<C> on(Function<Expression, IPredicate> operator, DataField operandField) {
        final OperationPredicate predicate;
        predicate = (OperationPredicate) operator.apply(operandField);
        return this.onClauseEnd(Collections.singletonList(predicate));
    }

    @Override
    public final JoinItemBlock<C> on(Function<Object, IPredicate> operator1, DataField operandField1
            , Function<Object, IPredicate> operator2, DataField operandField2) {
        final List<_Predicate> list;
        list = ArrayUtils.asUnmodifiableList(
                (OperationPredicate) operator1.apply(operandField1),
                (OperationPredicate) operator2.apply(operandField2)
        );
        return this.onClauseEnd(list);
    }

    @Override
    public final JoinItemBlock<C> on(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::addPredicate);
        return this.endOnClause();
    }

    @Override
    public final JoinItemBlock<C> on(BiConsumer<C, Consumer<IPredicate>> consumer) {
        consumer.accept(this.criteriaContext.criteria(), this::addPredicate);
        return this.endOnClause();
    }

    final boolean hasOnClause() {
        final List<_Predicate> predicateList = this.predicateList;
        return predicateList != null && predicateList.size() > 0;
    }

    final List<_Predicate> onClause() {
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = Collections.emptyList();
        }
        return predicateList;
    }


    private JoinItemBlock<C> onClauseEnd(final List<_Predicate> predicateList) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.predicateList = predicateList;
        return this;
    }

    private void addPredicate(final IPredicate predicate) {
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.predicateList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        predicateList.add((OperationPredicate) predicate);
    }

    private JoinItemBlock<C> endOnClause() {
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            throw ContextStack.criteriaError(this.criteriaContext, _Exceptions::predicateListIsEmpty);
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.predicateList = _CollectionUtils.unmodifiableList(predicateList);
        return this;
    }


}
