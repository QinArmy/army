package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._DialectTableBlock;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

class OnClauseTableBlock<OR> extends TableBlock implements Statement._OnClause<OR> {

    private List<_Predicate> predicateList;

    private final OR stmt;

    OnClauseTableBlock(_JoinType joinType, TabularItem tableItem, String alias, OR stmt) {
        super(joinType, tableItem, alias);
        this.stmt = stmt;
    }

    @SuppressWarnings("unchecked")
    OnClauseTableBlock(_JoinType joinType, TabularItem tableItem, String alias) {
        super(joinType, tableItem, alias);
        assert joinType == _JoinType.CROSS_JOIN || joinType == _JoinType.NONE;
        this.stmt = (OR) this;
    }

    OnClauseTableBlock(BlockParams params, OR stmt) {
        super(params);
        this.stmt = stmt;
    }


    @Override
    public final OR on(IPredicate predicate) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(CriteriaUtils.getCriteriaContext(this.stmt));
        }
        this.predicateList = Collections.singletonList((_Predicate) predicate);
        return this.stmt;
    }

    @Override
    public final OR on(IPredicate predicate1, IPredicate predicate2) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(CriteriaUtils.getCriteriaContext(this.stmt));
        }
        this.predicateList = ArrayUtils.asUnmodifiableList(
                (OperationPredicate) predicate1,
                (OperationPredicate) predicate2
        );
        return this.stmt;
    }

    @Override
    public final OR on(Function<Expression, IPredicate> operator, DataField operandField) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        this.predicateList = Collections.singletonList((OperationPredicate) operator.apply(operandField));
        return this.stmt;
    }

    @Override
    public final OR on(Function<Expression, IPredicate> operator1, DataField operandField1
            , Function<Expression, IPredicate> operator2, DataField operandField2) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        this.predicateList = ArrayUtils.asUnmodifiableList(
                (OperationPredicate) operator1.apply(operandField1),
                (OperationPredicate) operator2.apply(operandField2)
        );
        return this.stmt;
    }

    @Override
    public final OR on(Consumer<Consumer<IPredicate>> consumer) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        consumer.accept(this::addPredicate);
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            throw predicateListIsEmpty();
        }
        this.predicateList = _CollectionUtils.unmodifiableList(predicateList);
        return this.stmt;
    }


    @Override
    public final List<_Predicate> onClauseList() {
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null | predicateList instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        return predicateList;
    }



    final CriteriaContext getCriteriaContext() {
        return ((CriteriaContextSpec) this.stmt).getContext();
    }


    private void addPredicate(final IPredicate predicate) {
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.predicateList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        predicateList.add((OperationPredicate) predicate);
    }

    private CriteriaException predicateListIsEmpty() {
        return ContextStack.criteriaError(this.getCriteriaContext()
                , _Exceptions::predicateListIsEmpty);
    }


    static class OnItemTableBlock<OR> extends OnClauseTableBlock<OR> implements _DialectTableBlock {

        private final SQLWords modifier;

        OnItemTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TabularItem tableItem, String alias, OR stmt) {
            super(joinType, tableItem, alias, stmt);
            this.modifier = modifier;
        }

        OnItemTableBlock(TableBlock.DialectBlockParams params, OR stmt) {
            super(params, stmt);
            this.modifier = params.modifier();
        }

        @Override
        public final SQLWords modifier() {
            return this.modifier;
        }


    }//OnItemTableBlock


}
