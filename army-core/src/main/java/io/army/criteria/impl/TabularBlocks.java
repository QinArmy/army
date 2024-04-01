/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class TabularBlocks {

    private TabularBlocks() {
        throw new UnsupportedOperationException();
    }

    static FromClauseTableBlock fromTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table,
                                               String alias) {
        final FromClauseTableBlock bock;
        if (modifier == null) {
            bock = new FromClauseSimpleTableBlock(joinType, table, alias);
        } else {
            bock = new FromClauseModifierTableBlock(joinType, modifier, table, alias);
        }
        return bock;
    }


    static FromClauseDerivedBlock fromDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                                   String alias) {
        final FromClauseDerivedBlock bock;
        if (modifier == null) {
            bock = new FromClauseSimpleDerivedBlock(joinType, table, alias);
        } else {
            bock = new FromClauseModifierDerivedBlock(joinType, modifier, table, alias);
        }
        return bock;
    }

    static FromClauseAliasDerivedBlock fromAliasDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                                             DerivedTable table, String alias) {
        final FromClauseAliasDerivedBlock bock;
        if (modifier == null) {
            bock = new FromClauseAliasDerivedBlock(joinType, table, alias);
        } else {
            bock = new FromClauseModifierAliasDerivedBlock(joinType, modifier, table, alias);
        }
        return bock;
    }


    static _TabularBlock fromNestedBlock(_JoinType joinType, _NestedItems tableItems) {
        return new FromClauseNestedBlock(joinType, tableItems);
    }

    static FromClauseCteBlock fromCteBlock(_JoinType joinType, _Cte cte, String alias) {
        return new FromClauseCteBlock(joinType, cte, alias);
    }


    static <R extends Item> JoinClauseTableBlock<R> joinTableBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                                                   TableMeta<?> table, String alias, R clause) {
        final JoinClauseTableBlock<R> block;
        if (modifier == null) {
            block = new JoinClauseSimpleTableBlock<>(joinType, table, alias, clause);
        } else {
            block = new JoinClauseSimpleModifierTableBlock<>(joinType, modifier, table, alias, clause);
        }
        return block;
    }

    static <R extends Item> JoinClauseDerivedBlock<R> joinDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                                                       DerivedTable table, String alias, R clause) {
        final JoinClauseDerivedBlock<R> block;
        if (modifier == null) {
            block = new JoinClauseSimpleDerivedBlock<>(joinType, table, alias, clause);
        } else {
            block = new JoinClauseSimpleModifierDerivedBlock<>(joinType, modifier, table, alias, clause);
        }
        return block;
    }

    static <R extends Item> JoinClauseSimpleModifierAliasDerivedBlock<R> joinAliasDerivedBlock(
            _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table, String alias, R clause) {
        return new JoinClauseSimpleModifierAliasDerivedBlock<>(joinType, modifier, table, alias, clause);
    }

    static <R extends Item> JoinClauseCteBlock<R> joinCteBlock(_JoinType joinType, _Cte table,
                                                               String alias, R clause) {
        return new JoinClauseCteBlock<>(joinType, table, alias, clause);
    }

    static <R extends Item> JoinClauseNestedBlock<R> joinNestedBlock(_JoinType joinType, _NestedItems nestedItems,
                                                                     R clause) {
        return new JoinClauseNestedBlock<>(joinType, nestedItems, clause);
    }


    static abstract class FromClauseBlock implements _TabularBlock {

        private final _JoinType joinType;

        final TabularItem table;

        FromClauseBlock(_JoinType joinType, TabularItem table) {
            assert joinType == _JoinType.NONE || joinType == _JoinType.CROSS_JOIN;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public final _JoinType jointType() {
            return this.joinType;
        }

        @Override
        public final TabularItem tableItem() {
            return this.table;
        }

        @Override
        public final List<_Predicate> onClauseList() {
            return Collections.emptyList();
        }


    }//FromClauseBlock


    static abstract class FromClauseTableBlock extends FromClauseBlock {

        private final String alias;

        FromClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias) {
            super(joinType, table);
            this.alias = alias;
        }

        @Override
        public final String alias() {
            return this.alias;
        }


    }//FromClauseTableBlock


    static class FromClauseModifierTableBlock extends FromClauseTableBlock implements _ModifierTabularBlock {

        private final SQLWords modifier;

        /**
         * @see #fromTableBlock(_JoinType, SQLWords, TableMeta, String)
         */
        FromClauseModifierTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table,
                                     String alias) {
            super(joinType, table, alias);
            this.modifier = modifier;
        }

        @Override
        public final SQLWords modifier() {
            return this.modifier;
        }


    }//FromClauseModifierTableBlock


    static abstract class FromClauseDerivedBlock extends FromClauseBlock {

        final String alias;

        FromClauseDerivedBlock(_JoinType joinType, DerivedTable table, String alias) {
            super(joinType, table);
            this.alias = alias;
        }

        @Override
        public final String alias() {
            return this.alias;
        }


    }//FromClauseDerivedBock


    static class FromClauseAliasDerivedBlock extends FromClauseDerivedBlock
            implements _AliasDerivedBlock {

        private List<String> columnAliasList;

        private _SelectionMap selectionMap;

        private FromClauseAliasDerivedBlock(_JoinType joinType, DerivedTable table, String alias) {
            super(joinType, table, alias);
            this.selectionMap = (_SelectionMap) table;
        }


        @Override
        public final Selection refSelection(final String derivedAlias) {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refSelection(derivedAlias);
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refAllSelection();
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

        final void parens(String first, String... rest) {
            this.onColumnAlias(ArrayUtils.unmodifiableListOf(first, rest));
        }


        final void parens(CriteriaContext context, Consumer<Consumer<String>> consumer) {
            this.onColumnAlias(CriteriaUtils.stringList(context, true, consumer));
        }


        final void ifParens(CriteriaContext context, Consumer<Consumer<String>> consumer) {
            this.onColumnAlias(CriteriaUtils.stringList(context, false, consumer));
        }

        private void onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.columnAliasList = columnAliasList;
            this.selectionMap = CriteriaUtils.createAliasSelectionMap(columnAliasList,
                    ((_DerivedTable) this.table).refAllSelection(),
                    this.alias);
        }


    }//FromClauseAliasDerivedBock


    static class FromClauseCteBlock extends FromClauseBlock {

        private final String alias;

        private FromClauseCteBlock(_JoinType joinType, _Cte cte, String alias) {
            super(joinType, cte);
            this.alias = alias;
        }


        @Override
        public final String alias() {
            return this.alias;
        }

    }//FromClauseCteBlock


    static abstract class JoinClauseBlock<R extends Item> implements _TabularBlock, Statement._OnClause<R> {

        private final _JoinType joinType;

        private final R clause;

        private List<_Predicate> predicateList;

        JoinClauseBlock(_JoinType joinType, R clause) {
            // don't check joinType
            this.joinType = joinType;
            this.clause = clause;
        }

        @Override
        public final R on(IPredicate predicate) {
            this.predicateList = Collections.singletonList((OperationPredicate) predicate);
            return this.clause;
        }

        @Override
        public final R on(IPredicate predicate1, IPredicate predicate2) {

            this.predicateList = ArrayUtils.of(
                    (OperationPredicate) predicate1,
                    (OperationPredicate) predicate2
            );
            return this.clause;
        }

        @Override
        public final R on(Function<Expression, IPredicate> operator, Expression operandField) {
            this.predicateList = Collections.singletonList((OperationPredicate) operator.apply(operandField));
            return this.clause;
        }

        @Override
        public final R on(Function<Expression, IPredicate> operator1, Expression operandField1,
                          Function<Expression, IPredicate> operator2, Expression operandField2) {
            this.predicateList = ArrayUtils.of(
                    (OperationPredicate) operator1.apply(operandField1),
                    (OperationPredicate) operator2.apply(operandField2)
            );
            return this.clause;
        }

        @Override
        public final R on(Consumer<Consumer<IPredicate>> consumer) {
            final Consumer<IPredicate> func = this::addPredicate;
            ClauseUtils.invokeConsumer(func, consumer);

            final List<_Predicate> list = this.predicateList;
            if (list == null) {
                throw ContextStack.criteriaError(this.getContext(), _Exceptions::predicateListIsEmpty);
            }
            this.predicateList = _Collections.unmodifiableList(list);
            return this.clause;
        }

        @Override
        public final _JoinType jointType() {
            return this.joinType;
        }

        @Override
        public final List<_Predicate> onClauseList() {
            List<_Predicate> list = this.predicateList;
            if (list == null) {
                list = Collections.emptyList();
                this.predicateList = list;
            } else if (list instanceof ArrayList) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return list;
        }

        final CriteriaContext getContext() {
            return ((CriteriaContextSpec) this.clause).getContext();
        }

        private void addPredicate(final IPredicate predicate) {
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                predicateList = _Collections.arrayList();
                this.predicateList = predicateList;
            } else if (!(predicateList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            predicateList.add((OperationPredicate) predicate);
        }


    }//JoinClauseBlock

    static abstract class JoinClauseTableBlock<R extends Item> extends JoinClauseBlock<R> {

        private final TableMeta<?> table;

        private final String alias;

        JoinClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias, R clause) {
            super(joinType, clause);
            this.table = table;
            this.alias = alias;
        }

        @Override
        public final TabularItem tableItem() {
            return this.table;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

    }//JoinClauseTableBlock

    static abstract class JoinClauseModifierTableBlock<R extends Item> extends JoinClauseTableBlock<R>
            implements _ModifierTabularBlock {

        private final SQLWords modifier;

        JoinClauseModifierTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table,
                                     String alias, R clause) {
            super(joinType, table, alias, clause);
            this.modifier = modifier;
        }

        @Override
        public final SQLWords modifier() {
            return this.modifier;
        }


    }//JoinClauseModifierTableBlock


    static abstract class JoinClauseDerivedBlock<R extends Item> extends JoinClauseBlock<R> {

        final DerivedTable table;

        final String alias;

        JoinClauseDerivedBlock(_JoinType joinType, DerivedTable table, String alias, R clause) {
            super(joinType, clause);
            this.table = table;
            this.alias = alias;
        }

        @Override
        public final TabularItem tableItem() {
            return this.table;
        }

        @Override
        public final String alias() {
            return this.alias;
        }


    }//JoinClauseDerivedBlock

    static abstract class JoinClauseModifierDerivedBlock<R extends Item> extends JoinClauseDerivedBlock<R>
            implements _ModifierTabularBlock {

        private final SQLWords modifier;


        JoinClauseModifierDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                       String alias, R clause) {
            super(joinType, table, alias, clause);
            this.modifier = modifier;
        }

        @Override
        public final SQLWords modifier() {
            return this.modifier;
        }


    }//JoinClauseModifierDerivedBlock

    static abstract class JoinClauseAliasDerivedBlock<R extends Item>
            extends JoinClauseModifierDerivedBlock<R>
            implements _AliasDerivedBlock,
            Statement._ParensOnSpec<R> {

        private List<String> columnAliasList;

        private _SelectionMap selectionMap;

        JoinClauseAliasDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                    String alias, R clause) {
            super(joinType, modifier, table, alias, clause);
            this.selectionMap = (_SelectionMap) table;
        }

        @Override
        public final Statement._OnClause<R> parens(String first, String... rest) {
            return this.onColumnAlias(ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public final Statement._OnClause<R> parens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.getContext(), true, consumer));
        }

        @Override
        public final Statement._OnClause<R> ifParens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.getContext(), false, consumer));
        }

        @Override
        public final Selection refSelection(String name) {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refSelection(name);
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refAllSelection();
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

        private Statement._OnClause<R> onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.columnAliasList = columnAliasList;
            this.selectionMap = CriteriaUtils.createAliasSelectionMap(columnAliasList,
                    ((_DerivedTable) this.table).refAllSelection(),
                    this.alias);
            return this;
        }


    }//JoinClauseModifierAliasDerivedBlock


    static final class JoinClauseCteBlock<R extends Item> extends JoinClauseBlock<R> {

        private final _Cte cte;

        private final String alias;

        /**
         * private constructor
         *
         * @see #joinCteBlock(_JoinType, _Cte, String, Item)
         */
        private JoinClauseCteBlock(_JoinType joinType, _Cte cte, String alias, R clause) {
            super(joinType, clause);
            this.cte = cte;
            this.alias = alias;
        }

        @Override
        public TabularItem tableItem() {
            return this.cte;
        }

        @Override
        public String alias() {
            return this.alias;
        }


    }//JoinClauseCteBlock

    static final class JoinClauseNestedBlock<R extends Item> extends JoinClauseBlock<R> {

        private final _NestedItems items;

        /**
         * private constructor
         *
         * @see #joinNestedBlock(_JoinType, _NestedItems, Item)
         */
        private JoinClauseNestedBlock(_JoinType joinType, _NestedItems items, R clause) {
            super(joinType, clause);
            this.items = items;
        }

        @Override
        public TabularItem tableItem() {
            return this.items;
        }

        @Override
        public String alias() {
            return "";
        }

    }//JoinClauseNestedBlock


    private static final class FromClauseSimpleTableBlock extends FromClauseTableBlock {

        /**
         * @see #fromTableBlock(_JoinType, SQLWords, TableMeta, String)
         */
        private FromClauseSimpleTableBlock(_JoinType joinType, TableMeta<?> table, String alias) {
            super(joinType, table, alias);
        }


    }//FromClauseSimpleTableBlock


    private static final class FromClauseSimpleDerivedBlock extends FromClauseDerivedBlock {

        /**
         * @see #fromDerivedBlock(_JoinType, SQLWords, DerivedTable, String)
         */
        private FromClauseSimpleDerivedBlock(_JoinType joinType, DerivedTable table, String alias) {
            super(joinType, table, alias);
        }


    }//FromClauseSimpleDerivedBlock

    static class FromClauseModifierDerivedBlock extends FromClauseDerivedBlock implements _ModifierTabularBlock {

        private final SQLWords modifier;

        /**
         * @see #fromDerivedBlock(_JoinType, SQLWords, DerivedTable, String)
         */
        private FromClauseModifierDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                               String alias) {
            super(joinType, table, alias);
            this.modifier = modifier;
        }

        @Override
        public SQLWords modifier() {
            return this.modifier;
        }


    }//FromClauseModifierDerivedBlock


    private static final class JoinClauseSimpleTableBlock<R extends Item> extends JoinClauseTableBlock<R> {

        /**
         * @see #joinTableBlock(_JoinType, SQLWords, TableMeta, String, Item)
         */
        private JoinClauseSimpleTableBlock(_JoinType joinType, TableMeta<?> table, String alias, R clause) {
            super(joinType, table, alias, clause);
        }


    }//OnClauseSimpleTableBlock

    private static final class JoinClauseSimpleModifierTableBlock<R extends Item> extends JoinClauseModifierTableBlock<R> {

        /**
         * @see #joinTableBlock(_JoinType, SQLWords, TableMeta, String, Item)
         */
        private JoinClauseSimpleModifierTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table,
                                                   String alias, R clause) {
            super(joinType, modifier, table, alias, clause);
        }


    }//OnClauseSimpleModifierTableBlock

    private static final class JoinClauseSimpleDerivedBlock<R extends Item> extends JoinClauseDerivedBlock<R> {

        /**
         * @see #joinDerivedBlock(_JoinType, SQLWords, DerivedTable, String, Item)
         */
        JoinClauseSimpleDerivedBlock(_JoinType joinType, DerivedTable table, String alias, R clause) {
            super(joinType, table, alias, clause);
        }


    }//JoinClauseSimpleDerivedBlock

    private static final class JoinClauseSimpleModifierDerivedBlock<R extends Item>
            extends JoinClauseModifierDerivedBlock<R> {

        /**
         * @see #joinDerivedBlock(_JoinType, SQLWords, DerivedTable, String, Item)
         */
        JoinClauseSimpleModifierDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                             DerivedTable table, String alias, R clause) {
            super(joinType, modifier, table, alias, clause);
        }


    }//JoinClauseSimpleModifierDerivedBlock

    private static final class JoinClauseSimpleModifierAliasDerivedBlock<R extends Item>
            extends JoinClauseAliasDerivedBlock<R> {

        /**
         * @see #joinAliasDerivedBlock(_JoinType, SQLWords, DerivedTable, String, Item)
         */
        private JoinClauseSimpleModifierAliasDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                                          DerivedTable table, String alias, R clause) {
            super(joinType, modifier, table, alias, clause);
        }

    }//JoinClauseSimpleModifierAliasDerivedBlock


    private static final class FromClauseModifierAliasDerivedBlock extends FromClauseAliasDerivedBlock
            implements _ModifierTabularBlock {

        private final SQLWords modifier;

        /**
         * @see #fromAliasDerivedBlock(_JoinType, SQLWords, DerivedTable, String)
         */
        private FromClauseModifierAliasDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                                    String alias) {
            super(joinType, table, alias);
            this.modifier = modifier;
        }

        @Override
        public SQLWords modifier() {
            return this.modifier;
        }


    }//FromClauseModifierAliasDerivedBock


    private static final class FromClauseNestedBlock extends FromClauseBlock {

        /**
         * @see #fromNestedBlock(_JoinType, _NestedItems)
         */
        private FromClauseNestedBlock(_JoinType joinType, _NestedItems items) {
            super(joinType, items);
        }

        @Override
        public String alias() {
            return "";
        }


    }//FromClauseNestedBlock


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
