package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.util._ArrayUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class TableBlocks implements _TableBlock {


    static _TableBlock fromNestedBlock(_JoinType joinType, NestedItems tableItems) {
        return new FromNestedBlock(joinType, (_NestedItems) tableItems);
    }

    static <R extends Item> JoinNestedBlock<R> joinNestedBlock(_JoinType joinType, NestedItems nestedItems, R clause) {
        return new JoinNestedBlock<>(joinType, (_NestedItems) nestedItems, clause);
    }


    final _JoinType joinType;

    final TabularItem tableItem;

    final String alias;

    TableBlocks(_JoinType joinType, TabularItem tableItem, String alias) {
        Objects.requireNonNull(alias);
        this.joinType = joinType;
        this.tableItem = tableItem;
        this.alias = alias;

    }

    TableBlocks(BlockParams params) {
        this.joinType = params.joinType();
        this.tableItem = params.tableItem();
        this.alias = params.alias();
    }

    @Override
    public final TabularItem tableItem() {
        return this.tableItem;
    }

    @Override
    public final _JoinType jointType() {
        return this.joinType;
    }

    @Override
    public final String alias() {
        return this.alias;
    }

    static TableBlocks noneBlock(TabularItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new NoOnTableBlock(_JoinType.NONE, tableItem, alias);
    }

    static TableBlocks crossBlock(TabularItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new NoOnTableBlock(_JoinType.CROSS_JOIN, tableItem, alias);
    }

    @SuppressWarnings("unchecked")
    static abstract class OnClauseBlock<R extends Item> implements _TableBlock, Statement._OnClause<R> {

        private final _JoinType joinType;

        private final R clause;

        private List<_Predicate> predicateList;

        OnClauseBlock(_JoinType joinType, R clause) {
            this.joinType = joinType;
            this.clause = clause;
        }

        @Override
        public final R on(IPredicate predicate) {
            this.predicateList = Collections.singletonList((OperationPredicate) predicate);
            return (R) this;
        }

        @Override
        public final R on(IPredicate predicate1, IPredicate predicate2) {

            this.predicateList = _ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) predicate1,
                    (OperationPredicate) predicate2
            );
            return (R) this;
        }

        @Override
        public final R on(Function<Expression, IPredicate> operator, DataField operandField) {
            this.predicateList = Collections.singletonList((OperationPredicate) operator.apply(operandField));
            return (R) this;
        }

        @Override
        public final R on(Function<Expression, IPredicate> operator1, DataField operandField1,
                          Function<Expression, IPredicate> operator2, DataField operandField2) {
            this.predicateList = _ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) operator1.apply(operandField1),
                    (OperationPredicate) operator2.apply(operandField2)
            );
            return (R) this;
        }

        @Override
        public final R on(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::addPredicate);
            if (this.predicateList == null) {
                throw ContextStack.criteriaError(this.getContext(), _Exceptions::predicateListIsEmpty);
            }
            return (R) this;
        }

        @Override
        public final _JoinType jointType() {
            return this.joinType;
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
            return ((CriteriaContextSpec) this.clause).getContext();
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


    }//OnClauseBlock


    static class NoOnTableBlock extends TableBlocks {

        NoOnTableBlock(_JoinType joinType, TabularItem tableItem, String alias) {
            super(joinType, tableItem, alias);
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    break;
                default:
                    throw _Exceptions.castCriteriaApi();
            }

        }

        NoOnTableBlock(BlockParams params) {
            super(params);
            switch (this.joinType) {
                case NONE:
                case CROSS_JOIN:
                    break;
                default:
                    throw _Exceptions.castCriteriaApi();
            }

        }


        @Override
        public final List<_Predicate> onClauseList() {
            return Collections.emptyList();
        }

    }//NoOnTableBlock

    static class NoOnModifierTableBlock extends NoOnTableBlock implements _ModifierTableBlock {

        private final SQLWords itemWord;

        NoOnModifierTableBlock(_JoinType joinType, @Nullable SQLWords itemWord, TabularItem tableItem, String alias) {
            super(joinType, tableItem, alias);
            this.itemWord = itemWord;
        }

        NoOnModifierTableBlock(DialectBlockParams params) {
            super(params);
            this.itemWord = params.modifier();
        }

        @Override
        public final SQLWords modifier() {
            return this.itemWord;
        }


    }//DialectNoOnTableBlock

    static class NoOnModifierDerivedBlock extends NoOnModifierTableBlock {

        NoOnModifierDerivedBlock(_JoinType joinType, @Nullable SQLWords itemWord, DerivedTable tableItem,
                                 String alias) {
            super(joinType, itemWord, tableItem, alias);
        }

    }//NoOnModifierDerivedBlock


    static class ParensDerivedJoinBlock extends NoOnModifierTableBlock implements _DerivedTable, ArmyDerivedBlock {

        private List<String> columnAliasList;

        private Function<String, Selection> selectionFunction;

        private Supplier<List<? extends Selection>> selectionsSupplier;

        ParensDerivedJoinBlock(_JoinType joinType, @Nullable SQLWords itemWord, DerivedTable tableItem,
                               String alias) {
            super(joinType, itemWord, tableItem, alias);
            this.selectionFunction = ((ArmyDerivedTable) tableItem)::selection;
            this.selectionsSupplier = ((ArmyDerivedTable) tableItem)::selectionList;
        }

        @Override
        public final Selection selection(final String derivedAlias) {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionFunction.apply(derivedAlias);
        }

        @Override
        public final List<? extends Selection> selectionList() {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionsSupplier.get();
        }


        @Override
        public final List<String> columnAliasList() {
            List<String> list = this.columnAliasList;
            if (list == null) {
                list = Collections.emptyList();
                this.columnAliasList = list;
            }
            return list;
        }

        final void onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.columnAliasList = columnAliasList;

            final _Pair<List<Selection>, Map<String, Selection>> pair;
            pair = CriteriaUtils.forColumnAlias(columnAliasList, (ArmyDerivedTable) this.tableItem);
            this.selectionsSupplier = () -> pair.first;
            this.selectionFunction = pair.second::get;
        }

    }//ParensDerivedJoinBlock


    private static final class FromNestedBlock implements _TableBlock {

        private final _JoinType joinType;

        private final _NestedItems tableItems;

        private FromNestedBlock(_JoinType joinType, _NestedItems tableItems) {
            assert joinType == _JoinType.NONE || joinType == _JoinType.CROSS_JOIN;
            this.joinType = joinType;
            this.tableItems = tableItems;
        }

        @Override
        public _JoinType jointType() {
            return this.joinType;
        }

        @Override
        public String alias() {
            return "";
        }

        @Override
        public _NestedItems tableItem() {
            return this.tableItems;
        }

        @Override
        public List<_Predicate> onClauseList() {
            return Collections.emptyList();
        }


    }//FromNestedBlock

    static final class JoinNestedBlock<R extends Item> extends OnClauseBlock<R> {

        private final _NestedItems nestedItems;

        private JoinNestedBlock(_JoinType joinType, _NestedItems nestedItems, R clause) {
            super(joinType, clause);
            assert joinType != _JoinType.NONE && joinType != _JoinType.CROSS_JOIN;
            this.nestedItems = nestedItems;
        }


        @Override
        public _NestedItems tableItem() {
            return this.nestedItems;
        }

        @Override
        public String alias() {
            return "";
        }


    }//JoinNestedBlock


    interface BlockParams {

        _JoinType joinType();

        TabularItem tableItem();

        String alias();

    }

    interface DialectBlockParams extends BlockParams {

        @Nullable
        SQLWords modifier();

    }


}
