package io.army.criteria.impl;

import io.army.criteria.DerivedTable;
import io.army.criteria.SQLWords;
import io.army.criteria.Selection;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._ModifierTableBlock;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class TableBlock implements _TableBlock {

    final _JoinType joinType;

    final TabularItem tableItem;

    final String alias;

    TableBlock(_JoinType joinType, TabularItem tableItem, String alias) {
        Objects.requireNonNull(alias);
        this.joinType = joinType;
        this.tableItem = tableItem;
        this.alias = alias;

    }

    TableBlock(BlockParams params) {
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

    static TableBlock noneBlock(TabularItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new NoOnTableBlock(_JoinType.NONE, tableItem, alias);
    }

    static TableBlock crossBlock(TabularItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new NoOnTableBlock(_JoinType.CROSS_JOIN, tableItem, alias);
    }


    static class NoOnTableBlock extends TableBlock {

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


    static class ParensDerivedJoinBlock extends NoOnModifierTableBlock implements _DerivedTable {

        private List<String> columnAliasList;

        private Function<String, Selection> selectionFunction;

        private Supplier<List<Selection>> selectionsSupplier;

        ParensDerivedJoinBlock(_JoinType joinType, @Nullable SQLWords itemWord, DerivedTable tableItem,
                               String alias) {
            super(joinType, itemWord, tableItem, alias);
            this.selectionFunction = tableItem::selection;
            this.selectionsSupplier = tableItem::selectionList;
        }

        @Override
        public final Selection selection(String derivedAlias) {
            return this.selectionFunction.apply(derivedAlias);
        }

        @Override
        public final List<Selection> selectionList() {
            return this.selectionsSupplier.get();
        }


        @Override
        public List<String> columnAliasList() {
            List<String> list = this.columnAliasList;
            if (list == null) {
                list = Collections.emptyList();
                this.columnAliasList = list;
            }
            return list;
        }

        final void onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(ContextStack.peek());
            }
            this.columnAliasList = columnAliasList;

            final _Pair<List<Selection>, Map<String, Selection>> pair;
            pair = CriteriaUtils.forColumnAlias(columnAliasList, (_DerivedTable) this.tableItem);
            this.selectionsSupplier = () -> pair.first;
            this.selectionFunction = pair.second::get;
        }

    }//ParensDerivedJoinBlock


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
