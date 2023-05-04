package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.impl.inner._DoneFuncBlock;
import io.army.criteria.impl.inner._FunctionField;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.postgre.PostgreStatement;
import io.army.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class PostgreBlocks {

    private PostgreBlocks() {
        throw new UnsupportedOperationException();
    }


    static <R extends Item> PostgreStatement._FuncColumnDefinitionParensClause<R> fromUndoneFunc(
            final _JoinType joinType, final @Nullable Statement.DerivedModifier modifier, final UndoneFunction func,
            final String alias, final R stmt, final Consumer<_TabularBlock> blockConsumer) {
        final Function<PostgreUtils.DoneFunc, R> blockFunc;
        blockFunc = doneFunc -> {
            final FromClauseDoneFuncBlock block;
            block = new FromClauseDoneFuncBlock(joinType, modifier, doneFunc, alias);
            blockConsumer.accept(block);
            return stmt;
        };
        return PostgreUtils.undoneFunc(func, blockFunc);
    }

    static <R extends Item> PostgreStatement._FuncColumnDefinitionParensClause<Statement._OnClause<R>> joinUndoneFunc(
            final _JoinType joinType, final @Nullable Statement.DerivedModifier modifier, final UndoneFunction func,
            final String alias, final R stmt, final Consumer<_TabularBlock> blockConsumer) {
        final Function<PostgreUtils.DoneFunc, Statement._OnClause<R>> blockFunc;
        blockFunc = doneFunc -> {
            final JoinClauseDoneFuncBlock<R> block;
            block = new JoinClauseDoneFuncBlock<>(joinType, modifier, doneFunc, alias, stmt);
            blockConsumer.accept(block);
            return block;
        };
        return PostgreUtils.undoneFunc(func, blockFunc);
    }



    /*-------------------below inner class-------------------*/


    private static final class FromClauseDoneFuncBlock extends TabularBlocks.FromClauseBlock
            implements _DoneFuncBlock {

        private final Statement.DerivedModifier modifier;

        private final String alias;

        private final List<_FunctionField> fieldList;

        private final Map<String, _FunctionField> fieldMap;

        private FromClauseDoneFuncBlock(_JoinType joinType, @Nullable Statement.DerivedModifier modifier,
                                        PostgreUtils.DoneFunc func, String alias) {
            super(joinType, func.funcItem);
            this.modifier = modifier;
            this.alias = alias;
            this.fieldList = func.fieldList;
            this.fieldMap = func.fieldMap;
        }

        @Override
        public SQLWords modifier() {
            return this.modifier;
        }

        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public Selection refSelection(final String name) {
            return this.fieldMap.get(name);
        }

        @Override
        public List<_FunctionField> refAllSelection() {
            return this.fieldList;
        }

        @Override
        public List<_FunctionField> fieldList() {
            return this.fieldList;
        }


    }//FromClauseDoneFuncBloc

    private static final class JoinClauseDoneFuncBlock<R extends Item> extends TabularBlocks.JoinClauseBlock<R>
            implements _DoneFuncBlock {

        private final Statement.DerivedModifier modifier;

        private final UndoneFunction func;

        private final String alias;

        private final List<_FunctionField> fieldList;

        private final Map<String, _FunctionField> fieldMap;

        private JoinClauseDoneFuncBlock(_JoinType joinType, @Nullable Statement.DerivedModifier modifier,
                                        PostgreUtils.DoneFunc func, String alias, R clause) {
            super(joinType, clause);
            this.modifier = modifier;
            this.func = func.funcItem;
            this.alias = alias;
            this.fieldList = func.fieldList;

            this.fieldMap = func.fieldMap;
        }

        @Override
        public SQLWords modifier() {
            return this.modifier;
        }

        @Override
        public TabularItem tableItem() {
            return this.func;
        }

        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public Selection refSelection(final String name) {
            return this.fieldMap.get(name);
        }

        @Override
        public List<_FunctionField> refAllSelection() {
            return this.fieldList;
        }

        @Override
        public List<_FunctionField> fieldList() {
            return this.fieldList;
        }


    }//JoinClauseDoneFuncBloc


}
