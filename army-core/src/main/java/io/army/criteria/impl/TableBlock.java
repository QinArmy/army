package io.army.criteria.impl;

import io.army.criteria.SQLWords;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner._DialectTableBlock;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    static class NoOnModifierTableBlock extends NoOnTableBlock implements _DialectTableBlock {

        private final SQLWords itemWord;

        NoOnModifierTableBlock(_JoinType joinType, @Nullable SQLWords itemWord, TabularItem tableItem, String alias) {
            super(joinType, tableItem, alias);
            this.itemWord = itemWord;
        }

        NoOnModifierTableBlock(DialectBlockParams params) {
            super(params);
            this.itemWord = params.itemWord();
        }

        @Override
        public final SQLWords modifier() {
            return this.itemWord;
        }


    }//DialectNoOnTableBlock


    static class DynamicTableBlock extends TableBlock {

        private final List<_Predicate> predicateList;

        DynamicTableBlock(_JoinType joinType, DynamicBlock<?> block) {
            super(joinType, block.tableItem, block.alias);
            this.predicateList = block.onClause();
        }

        @Override
        public final List<_Predicate> onClauseList() {
            return this.predicateList;
        }


    }//DynamicTableBlock


    interface BlockParams {

        _JoinType joinType();

        TabularItem tableItem();

        String alias();

    }

    interface DialectBlockParams extends BlockParams {

        @Nullable
        SQLWords itemWord();

    }


}
