package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of all implementation of below:
 * <ul>
 *     <li>{@link io.army.criteria.Query}</li>
 *     <li>{@link io.army.criteria.Update}</li>
 *     <li>{@link io.army.criteria.Delete}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class JoinableClause<C, FT, FS, FP, JT, JS, JP>
        implements Statement._JoinClause<C, JT, JS>, Statement._CrossJoinClause<C, FT, FS>
        , DialectStatement._StraightJoinClause<C, JT, JS>, DialectStatement._DialectJoinClause<C, JP>
        , DialectStatement._DialectStraightJoinClause<C, JP>, DialectStatement._DialectCrossJoinClause<C, FP>
        , DialectStatement._JoinCteClause<JS>, DialectStatement._StraightJoinCteClause<JS>
        , DialectStatement._CrossJoinCteClause<FS>, CriteriaSpec<C> {

    final ClauseSupplier clauseSupplier;

    final C criteria;

    JoinableClause(ClauseSupplier clauseSupplier, @Nullable C criteria) {
        this.clauseSupplier = clauseSupplier;
        this.criteria = criteria;
    }

    JoinableClause(@Nullable C criteria) {
        if (!(this instanceof ClauseSupplier)) {
            throw new IllegalStateException();
        }
        this.clauseSupplier = (ClauseSupplier) this;
        this.criteria = criteria;
    }


    /*################################## blow JoinSpec method ##################################*/


    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return (JP) this.clauseSupplier.createClause(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        return (JT) this.clauseSupplier.createAndAddBlock(_JoinType.LEFT_JOIN, table, tableAlias);
    }


    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JS leftJoin(String cteName) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.LEFT_JOIN, SQLs.refCte(cteName), "");
    }

    @Override
    public final JS leftJoin(String cteName, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.LEFT_JOIN, SQLs.refCte(cteName), alias);
    }

    @Override
    public final JP ifLeftJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJoinTable(predicate, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinItem(_JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Function<C, T> function, String alias) {
        return this.ifJoinItem(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JP join(TableMeta<?> table) {
        return (JP) this.clauseSupplier.createClause(_JoinType.JOIN, table);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        return (JT) this.clauseSupplier.createAndAddBlock(_JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JS join(String cteName) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.JOIN, SQLs.refCte(cteName), "");
    }

    @Override
    public final JS join(String cteName, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.JOIN, SQLs.refCte(cteName), alias);
    }


    @Override
    public final JP ifJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJoinTable(predicate, _JoinType.JOIN, table);
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinItem(_JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Function<C, T> function, String alias) {
        return this.ifJoinItem(_JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return (JP) this.clauseSupplier.createClause(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        return (JT) this.clauseSupplier.createAndAddBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JS rightJoin(String cteName) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.RIGHT_JOIN, SQLs.refCte(cteName), "");
    }

    @Override
    public final JS rightJoin(String cteName, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.RIGHT_JOIN, SQLs.refCte(cteName), alias);
    }

    @Override
    public final JP ifRightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJoinTable(predicate, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinItem(_JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Function<C, T> function, String alias) {
        return this.ifJoinItem(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return (JP) this.clauseSupplier.createClause(_JoinType.FULL_JOIN, table);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        return (JT) this.clauseSupplier.createAndAddBlock(_JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JS fullJoin(String cteName) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.FULL_JOIN, SQLs.refCte(cteName), "");
    }

    @Override
    public final JS fullJoin(String cteName, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.FULL_JOIN, SQLs.refCte(cteName), alias);
    }


    @Override
    public final JP ifFullJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJoinTable(predicate, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinItem(_JoinType.FULL_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Function<C, T> function, String alias) {
        return this.ifJoinItem(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JP straightJoin(TableMeta<?> table) {
        return (JP) this.clauseSupplier.createClause(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        return (JT) this.clauseSupplier.createAndAddBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JS straightJoin(String cteName) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.STRAIGHT_JOIN, SQLs.refCte(cteName), "");
    }

    @Override
    public final JS straightJoin(String cteName, String alias) {
        return (JS) this.clauseSupplier.createAndAddBlock(_JoinType.STRAIGHT_JOIN, SQLs.refCte(cteName), alias);
    }


    @Override
    public final JP ifStraightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJoinTable(predicate, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.STRAIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinItem(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifJoinItem(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }


    @Override
    public final FP crossJoin(TableMeta<?> table) {
        final FP clause;
        clause = (FP) this.clauseSupplier.createClause(_JoinType.CROSS_JOIN, table);
        if (this instanceof Statement) {
            this.crossJoinEvent(true);
        }//nested join couldn't invoking crossJoinEvent(true),because throw CriteriaException
        return clause;
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        block = this.clauseSupplier.createAndAddBlock(_JoinType.CROSS_JOIN, table, tableAlias);
        final Object clause;
        if (block instanceof JoinableClause) {
            //here nested join items,//nested join couldn't invoking crossJoinEvent(true),because throw CriteriaException
            clause = block;
        } else {
            this.crossJoinEvent(true);
            clause = this;
        }
        return (FT) clause;
    }


    @Override
    public final <T extends TableItem> FS crossJoin(Supplier<T> supplier, String alias) {
        final _TableBlock block;
        block = this.clauseSupplier.createAndAddBlock(_JoinType.CROSS_JOIN, supplier.get(), alias);
        final Object clause;
        if (block instanceof JoinableClause) {
            //here nested join items,//nested join couldn't invoking crossJoinEvent(true),because throw CriteriaException
            clause = block;
        } else {
            this.crossJoinEvent(true);
            clause = this;
        }
        return (FS) clause;
    }


    @Override
    public final <T extends TableItem> FS crossJoin(Function<C, T> function, String alias) {
        final _TableBlock block;
        block = this.clauseSupplier.createAndAddBlock(_JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
        final Object clause;
        if (block instanceof JoinableClause) {
            //here nested join items,//nested join couldn't invoking crossJoinEvent(true),because throw CriteriaException
            clause = block;
        } else {
            this.crossJoinEvent(true);
            clause = this;
        }
        return (FS) clause;
    }

    @Override
    public final FS crossJoin(String cteName) {
        return this.crossJoin(cteName, "");
    }

    @Override
    public final FS crossJoin(String cteName, String alias) {
        final _TableBlock block;
        block = this.clauseSupplier.createAndAddBlock(_JoinType.CROSS_JOIN, SQLs.refCte(cteName), alias);
        final Object clause;
        if (block instanceof JoinableClause) {
            //here nested join items,//nested join couldn't invoking crossJoinEvent(true),because throw CriteriaException
            clause = block;
        } else {
            this.crossJoinEvent(true);
            clause = this;
        }
        return (FS) clause;
    }

    @Override
    public final FP ifCrossJoin(Predicate<C> predicate, TableMeta<?> table) {
        final Object clause;
        if (predicate.test(this.criteria)) {
            clause = this.crossJoin(table);
        } else {
            clause = this.clauseSupplier.getNoActionClauseBeforeAs(_JoinType.CROSS_JOIN);
            this.crossJoinEvent(false);
        }
        return (FP) clause;
    }

    @Override
    public final FT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        final FT clause;
        if (predicate.test(this.criteria)) {
            clause = this.crossJoin(table, tableAlias);
        } else {
            clause = (FT) this.clauseSupplier.getNoActionClause(_JoinType.CROSS_JOIN);
            this.crossJoinEvent(false);
        }
        return clause;
    }


    @Override
    public final <T extends TableItem> FS ifCrossJoin(Supplier<T> supplier, String alias) {
        return this.doIfCrossJoin(supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> FS ifCrossJoin(Function<C, T> function, String alias) {
        return this.doIfCrossJoin(function.apply(this.criteria), alias);
    }


    @Override
    public final C getCriteria() {
        return this.criteria;
    }


    abstract void crossJoinEvent(boolean success);


    private JP ifJoinTable(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        final Object clause;
        if (predicate.test(this.criteria)) {
            clause = this.clauseSupplier.createClause(joinType, table);
        } else {
            clause = this.clauseSupplier.getNoActionClauseBeforeAs(joinType);
        }
        return (JP) clause;
    }


    private JT ifJoinTable(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table, String tableAlias) {
        final Object clause;
        if (predicate.test(this.criteria)) {
            clause = this.clauseSupplier.createAndAddBlock(joinType, table, tableAlias);
        } else {
            clause = this.clauseSupplier.getNoActionClause(joinType);
        }
        return (JT) clause;
    }


    private JS ifJoinItem(_JoinType joinType, @Nullable TableItem item, String alias) {
        final Object clause;
        if (item == null) {
            clause = this.clauseSupplier.getNoActionClause(joinType);
        } else {
            clause = this.clauseSupplier.createAndAddBlock(joinType, item, alias);
        }
        return (JS) clause;
    }

    private FS doIfCrossJoin(@Nullable TableItem item, String alias) {
        final FS clause;
        if (item == null) {
            clause = (FS) this.clauseSupplier.getNoActionClause(_JoinType.CROSS_JOIN);
            this.crossJoinEvent(false);
        } else {
            final _TableBlock block;
            block = this.clauseSupplier.createAndAddBlock(_JoinType.CROSS_JOIN, item, alias);
            if (block instanceof JoinableClause) {
                //here nested join items,//nested join couldn't invoking crossJoinEvent(true),because throw CriteriaException
                clause = (FS) block;
            } else {
                this.crossJoinEvent(true);
                clause = (FS) this;
            }
        }
        return clause;
    }

    static ClauseSupplier voidClauseSuppler() {
        return VoidClauseSuppler.INSTANCE;
    }

    interface ClauseSupplier {

        _TableBlock createAndAddBlock(_JoinType joinType, TableItem item, String alias);

        Object createClause(_JoinType joinType, TableMeta<?> table);

        Object getNoActionClause(_JoinType joinType);

        Object getNoActionClauseBeforeAs(_JoinType joinType);

    }

    interface NestedClauseSupplier extends ClauseSupplier {

        NestedItems endNested();
    }


    private static final class VoidClauseSuppler implements ClauseSupplier {

        private static final VoidClauseSuppler INSTANCE = new VoidClauseSuppler();

        private VoidClauseSuppler() {
        }


        @Override
        public _TableBlock createAndAddBlock(_JoinType joinType, TableItem item, String alias) {
            throw _Exceptions.castCriteriaApi();
        }

        @Override
        public Object createClause(_JoinType joinType, TableMeta<?> table) {
            throw _Exceptions.castCriteriaApi();
        }

        @Override
        public Object getNoActionClause(_JoinType joinType) {
            throw _Exceptions.castCriteriaApi();
        }

        @Override
        public Object getNoActionClauseBeforeAs(_JoinType joinType) {
            throw _Exceptions.castCriteriaApi();
        }

    }//VoidClauseSuppler


    static abstract class LeftBracketNestedItem<C, LT, LS, LP> implements Statement._LeftBracketClause<C, LT, LS>
            , DialectStatement._DialectLeftBracketClause<LP>, DialectStatement._LeftBracketCteClause<LS>
            , NestedItems, NestedClauseSupplier, _NestedItems {

        final C criteria;


        final Consumer<_TableBlock> blockConsumer;

        private List<_TableBlock> blockList = new ArrayList<>();

        private boolean nestedEnd;

        LeftBracketNestedItem(@Nullable C criteria) {
            this.criteria = criteria;
            this.blockConsumer = this::innerAddBlock;
        }

        @Override
        public final LP leftBracket(TableMeta<?> table) {
            if (this.blockList.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            return (LP) this.createClause(_JoinType.NONE, table);
        }

        @Override
        public final LT leftBracket(TableMeta<?> table, String tableAlias) {
            if (this.blockList.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            return (LT) this.createAndAddBlock(_JoinType.NONE, table, tableAlias);
        }

        @Override
        public final <T extends TableItem> LS leftBracket(Supplier<T> supplier, String alias) {
            if (this.blockList.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            return (LS) this.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
        }

        @Override
        public final <T extends TableItem> LS leftBracket(Function<C, T> function, String alias) {
            if (this.blockList.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            return (LS) this.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
        }

        @Override
        public final LS leftBracket(String cteName) {
            if (this.blockList.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            return (LS) this.createAndAddBlock(_JoinType.NONE, SQLs.refCte(cteName), "");
        }

        @Override
        public final LS leftBracket(String cteName, String alias) {
            if (this.blockList.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            return (LS) this.createAndAddBlock(_JoinType.NONE, SQLs.refCte(cteName), alias);
        }

        @Override
        public final NestedItems endNested() {
            final List<_TableBlock> blockList = this.blockList;
            if (this.nestedEnd || blockList == null || blockList.size() == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            this.blockList = _CollectionUtils.unmodifiableList(blockList);
            this.nestedEnd = true;
            return this;
        }

        @Override
        public final List<? extends _TableBlock> tableBlockList() {
            if (!this.nestedEnd) {
                throw _Exceptions.castCriteriaApi();
            }
            final List<_TableBlock> blockList = this.blockList;
            assert blockList != null;
            return blockList;
        }


        private void innerAddBlock(_TableBlock block) {
            if (this.nestedEnd) {
                throw _Exceptions.castCriteriaApi();
            }
            this.blockList.add(block);
        }


    }//LeftBracketNestedItem


    static abstract class NoActionOnOrJoinBlock<C, FT, FS, FP, JT, JS, JP>
            extends JoinableClause<C, FT, FS, FP, JT, JS, JP>
            implements Statement._OnClause<C, FS>, Statement._RightBracketClause<NestedItems> {


        protected NoActionOnOrJoinBlock(NestedClauseSupplier suppler, @Nullable C criteria) {
            super(suppler, criteria);
        }

        @Override
        public final FS on(IPredicate predicate) {
            return (FS) this;
        }

        @Override
        public final FS on(IPredicate predicate1, IPredicate predicate2) {
            return (FS) this;
        }

        @Override
        public final FS on(Function<Object, IPredicate> operator, DataField operandField) {
            return (FS) this;
        }

        @Override
        public final FS on(Function<Object, IPredicate> operator1, DataField operandField1
                , Function<Object, IPredicate> operator2, DataField operandField2) {
            return (FS) this;
        }

        @Override
        public final FS on(Function<C, List<IPredicate>> function) {
            return (FS) this;
        }

        @Override
        public final FS on(Supplier<List<IPredicate>> supplier) {
            return (FS) this;
        }

        @Override
        public final FS on(Consumer<List<IPredicate>> consumer) {
            return (FS) this;
        }

        @Override
        public final NestedItems rightBracket() {
            return ((NestedClauseSupplier) this.clauseSupplier).endNested();
        }

        @Override
        final void crossJoinEvent(boolean success) {
            //no-op
        }


    }//NoActionOnOrJoinBlock


    static abstract class OnOrJoinBlock<C, FT, FS, FP, JT, JS, JP>
            extends JoinableClause<C, FT, FS, FP, JT, JS, JP>
            implements Statement._OnClause<C, FS>, _TableBlock, Statement._RightBracketClause<NestedItems> {

        final _JoinType joinType;

        final TableItem tableItem;

        final String alias;

        private List<_Predicate> predicateList;

        protected OnOrJoinBlock(NestedClauseSupplier suppler, @Nullable C criteria
                , _JoinType joinType, TableItem tableItem
                , String alias) {
            super(suppler, criteria);
            this.joinType = joinType;
            this.tableItem = tableItem;
            this.alias = alias;
        }

        @Override
        public final _JoinType jointType() {
            return this.joinType;
        }

        @Override
        public final TableItem tableItem() {
            return this.tableItem;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final FS on(IPredicate predicate) {
            this.assertForOn();
            this.predicateList = Collections.singletonList((OperationPredicate) predicate);
            return (FS) this;
        }


        @Override
        public final FS on(IPredicate predicate1, IPredicate predicate2) {
            this.assertForOn();
            this.predicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) predicate1,
                    (OperationPredicate) predicate2
            );
            return (FS) this;
        }

        @Override
        public final FS on(Function<Object, IPredicate> operator, DataField operandField) {
            this.assertForOn();
            final IPredicate predicate;
            predicate = operator.apply(operandField);
            assert predicate instanceof OperationPredicate;
            this.predicateList = Collections.singletonList((OperationPredicate) predicate);
            return (FS) this;
        }

        @Override
        public final FS on(Function<Object, IPredicate> operator1, DataField operandField1
                , Function<Object, IPredicate> operator2, DataField operandField2) {
            this.predicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) operator1.apply(operandField1),
                    (OperationPredicate) operator2.apply(operandField2)
            );
            return (FS) this;
        }

        @Override
        public final FS on(Function<C, List<IPredicate>> function) {
            this.assertForOn();
            this.predicateList = CriteriaUtils.asPredicateList(function.apply(this.criteria)
                    , _Exceptions::predicateListIsEmpty);
            return (FS) this;
        }

        @Override
        public final FS on(Supplier<List<IPredicate>> supplier) {
            this.assertForOn();
            this.predicateList = CriteriaUtils.asPredicateList(supplier.get(), _Exceptions::predicateListIsEmpty);
            return (FS) this;
        }

        @Override
        public final FS on(Consumer<List<IPredicate>> consumer) {
            this.assertForOn();
            final List<IPredicate> list = new ArrayList<>();
            consumer.accept(list);
            this.predicateList = CriteriaUtils.asPredicateList(list, _Exceptions::predicateListIsEmpty);
            return (FS) this;
        }

        @Override
        public final NestedItems rightBracket() {
            return ((NestedClauseSupplier) this.clauseSupplier).endNested();
        }

        @Override
        public final List<_Predicate> predicateList() {
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                predicateList = Collections.emptyList();
            }
            return predicateList;
        }

        private void assertForOn() {
            if (this.predicateList != null) {
                throw _Exceptions.castCriteriaApi();
            }
            switch (this.joinType) {
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN:
                    break;
                case NONE:
                case CROSS_JOIN:
                    throw _Exceptions.castCriteriaApi();
                default:
                    throw _Exceptions.unexpectedEnum(this.joinType);

            }
        }


    }//OnOrJoinBlock


}
