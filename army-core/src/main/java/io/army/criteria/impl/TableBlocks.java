package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._ModifierTableBlock;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class TableBlocks implements _TableBlock {

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


    static _TableBlock fromNestedBlock(_JoinType joinType, NestedItems tableItems) {
        return new FromClauseNestedBlock(joinType, tableItems);
    }

    static FromClauseDerivedBlock fromCteBlock(_JoinType joinType, CteItem table, String alias) {
        return new FromClauseSimpleDerivedBlock(joinType, table, alias);
    }


    static <R extends Item> JoinClauseTableBlock<R> joinTableBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                                                   TableMeta<?> table, String alias, R clause) {
        final JoinClauseTableBlock<R> block;
        if (modifier == null) {
            block = new OnClauseSimpleTableBlock<>(joinType, table, alias, clause);
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

    static <R extends Item> JoinClauseCteBlock<R> joinCteBlock(_JoinType joinType, CteItem table,
                                                               String alias, R clause) {
        return new JoinClauseCteBlock<>(joinType, table, alias, clause);
    }

    static <R extends Item> JoinClauseNestedBlock<R> joinNestedBlock(_JoinType joinType, NestedItems nestedItems,
                                                                     R clause) {
        return new JoinClauseNestedBlock<>(joinType, (_NestedItems) nestedItems, clause);
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


    static abstract class FromClauseBlock implements _TableBlock {

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


    static abstract class FromClauseDerivedBlock extends FromClauseBlock {

        private final String alias;

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
            implements ArmyAliasDerivedBlock {

        private List<String> columnAliasList;

        private Function<String, Selection> selectionFunction;

        private Supplier<List<? extends Selection>> selectionsSupplier;

        private FromClauseAliasDerivedBlock(_JoinType joinType, DerivedTable table, String alias) {
            super(joinType, table, alias);
            this.selectionFunction = ((ArmyDerivedTable) table)::selection;
            this.selectionsSupplier = ((ArmyDerivedTable) table)::selectionList;
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

        final void parens(String first, String... rest) {
            this.onColumnAlias(_ArrayUtils.unmodifiableListOf(first, rest));
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

            final _Pair<List<Selection>, Map<String, Selection>> pair;
            pair = CriteriaUtils.forColumnAlias(columnAliasList, (ArmyDerivedTable) this.table);
            this.selectionsSupplier = () -> pair.first;
            this.selectionFunction = pair.second::get;
        }


    }//FromClauseAliasDerivedBock


    @SuppressWarnings("unchecked")
    static abstract class JoinClauseBlock<R extends Item> implements _TableBlock, Statement._OnClause<R> {

        private final _JoinType joinType;

        private final R clause;

        private List<_Predicate> predicateList;

        JoinClauseBlock(_JoinType joinType, R clause) {
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
            List<_Predicate> list = this.predicateList;
            if (list == null) {
                list = Collections.emptyList();
                this.predicateList = list;
            } else if (list instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            return list;
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
            implements _ModifierTableBlock {

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

        private final String alias;

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
            implements _ModifierTableBlock {

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
            implements ArmyAliasDerivedBlock, Statement._ParensOnSpec<R> {

        private List<String> columnAliasList;

        private Function<String, Selection> selectionFunction;

        private Supplier<List<? extends Selection>> selectionsSupplier;

        JoinClauseAliasDerivedBlock(_JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                    String alias, R clause) {
            super(joinType, modifier, table, alias, clause);
            this.selectionFunction = ((ArmyDerivedTable) table)::selection;
            this.selectionsSupplier = ((ArmyDerivedTable) table)::selectionList;
        }

        @Override
        public final Statement._OnClause<R> parens(String first, String... rest) {
            return this.onColumnAlias(_ArrayUtils.unmodifiableListOf(first, rest));
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
        public final Selection selection(String name) {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionFunction.apply(name);
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

        private Statement._OnClause<R> onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.columnAliasList = columnAliasList;

            final _Pair<List<Selection>, Map<String, Selection>> pair;
            pair = CriteriaUtils.forColumnAlias(columnAliasList, (ArmyDerivedTable) this.table);
            this.selectionsSupplier = () -> pair.first;
            this.selectionFunction = pair.second::get;
            return this;
        }


    }//JoinClauseModifierAliasDerivedBlock


    static final class JoinClauseCteBlock<R extends Item> extends JoinClauseDerivedBlock<R> {

        /**
         * private constructor
         *
         * @see #joinCteBlock(_JoinType, CteItem, String, Item)
         */
        private JoinClauseCteBlock(_JoinType joinType, CteItem table, String alias, R clause) {
            super(joinType, table, alias, clause);
        }

    }//JoinClauseCteBlock

    static final class JoinClauseNestedBlock<R extends Item> extends JoinClauseBlock<R> {

        private final _NestedItems items;

        /**
         * private constructor
         *
         * @see #joinNestedBlock(_JoinType, NestedItems, Item)
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


    @Deprecated
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



    private static final class FromClauseSimpleTableBlock extends FromClauseTableBlock {

        /**
         * @see #fromTableBlock(_JoinType, SQLWords, TableMeta, String)
         */
        private FromClauseSimpleTableBlock(_JoinType joinType, TableMeta<?> table, String alias) {
            super(joinType, table, alias);
        }


    }//FromClauseSimpleTableBlock


    static class FromClauseModifierTableBlock extends FromClauseTableBlock implements _ModifierTableBlock {

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


    private static final class FromClauseSimpleDerivedBlock extends FromClauseDerivedBlock {

        /**
         * @see #fromDerivedBlock(_JoinType, SQLWords, DerivedTable, String)
         */
        private FromClauseSimpleDerivedBlock(_JoinType joinType, DerivedTable table, String alias) {
            super(joinType, table, alias);
        }


    }//FromClauseSimpleDerivedBlock

    static class FromClauseModifierDerivedBlock extends FromClauseDerivedBlock implements _ModifierTableBlock {

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


    private static final class OnClauseSimpleTableBlock<R extends Item> extends JoinClauseTableBlock<R> {

        /**
         * @see #joinTableBlock(_JoinType, SQLWords, TableMeta, String, Item)
         */
        private OnClauseSimpleTableBlock(_JoinType joinType, TableMeta<?> table, String alias, R clause) {
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
            implements _ModifierTableBlock {

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
         * @see #fromNestedBlock(_JoinType, NestedItems)
         */
        private FromClauseNestedBlock(_JoinType joinType, NestedItems items) {
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
