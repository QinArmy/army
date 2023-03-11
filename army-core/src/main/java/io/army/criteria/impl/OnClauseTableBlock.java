package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._ModifierTableBlock;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util._ArrayUtils;
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
        this.predicateList = _ArrayUtils.asUnmodifiableList(
                (OperationPredicate) predicate1,
                (OperationPredicate) predicate2
        );
        return this.stmt;
    }

    @Override
    public final OR on(Function<Expression, IPredicate> operator, DataField operandField) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(this.getContext());
        }
        this.predicateList = Collections.singletonList((OperationPredicate) operator.apply(operandField));
        return this.stmt;
    }

    @Override
    public final OR on(Function<Expression, IPredicate> operator1, DataField operandField1
            , Function<Expression, IPredicate> operator2, DataField operandField2) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(this.getContext());
        }
        this.predicateList = _ArrayUtils.asUnmodifiableList(
                (OperationPredicate) operator1.apply(operandField1),
                (OperationPredicate) operator2.apply(operandField2)
        );
        return this.stmt;
    }

    @Override
    public final OR on(Consumer<Consumer<IPredicate>> consumer) {
        if (this.predicateList != null) {
            throw ContextStack.castCriteriaApi(this.getContext());
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
            throw ContextStack.castCriteriaApi(this.getContext());
        }
        return predicateList;
    }


    final CriteriaContext getContext() {
        return ((CriteriaContextSpec) this.stmt).getContext();
    }


    private void addPredicate(final IPredicate predicate) {
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.predicateList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.getContext());
        }
        predicateList.add((OperationPredicate) predicate);
    }

    private CriteriaException predicateListIsEmpty() {
        return ContextStack.criteriaError(this.getContext()
                , _Exceptions::predicateListIsEmpty);
    }


    static class OnItemTableBlock<OR> extends OnClauseTableBlock<OR> implements _ModifierTableBlock {

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

    static class OnModifierParensBlock<OR> extends OnItemTableBlock<OR> implements Statement._ParensOnSpec<OR> {

        OnModifierParensBlock(_JoinType joinType, @Nullable SQLWords modifier, DerivedTable tableItem, String alias,
                              OR stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


        @Override
        public final Statement._OnClause<OR> parens(String first, String... rest) {
            ((ArmyDerivedTable) this.tableItem).setColumnAliasList(_ArrayUtils.unmodifiableListOf(first, rest));
            return this;
        }

        @Override
        public final Statement._OnClause<OR> parens(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            ((ArmyDerivedTable) this.tableItem).setColumnAliasList(list);
            return this;
        }

        @Override
        public final Statement._OnClause<OR> ifParens(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                ((ArmyDerivedTable) this.tableItem).setColumnAliasList(list);
            } else {
                ((ArmyDerivedTable) this.tableItem).setColumnAliasList(CriteriaUtils.EMPTY_STRING_LIST);
            }
            return this;
        }


    }//OnModifierParensBlock


}
