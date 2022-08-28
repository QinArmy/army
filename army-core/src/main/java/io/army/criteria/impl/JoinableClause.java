package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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
abstract class JoinableClause<C, FT, FS, FP, FJ, JT, JS, JP>
        implements Statement._JoinClause<C, JT, JS>, Statement._CrossJoinClause<C, FT, FS>
        , DialectStatement._StraightJoinClause<C, JT, JS>, DialectStatement._DialectJoinClause<JP>
        , DialectStatement._DialectStraightJoinClause<JP>, DialectStatement._DialectCrossJoinClause<FP>
        , DialectStatement._JoinCteClause<JS>, DialectStatement._StraightJoinCteClause<JS>
        , DialectStatement._JoinLateralClause<C, JS>, DialectStatement._CrossJoinLateralClause<C, FS>
        , DialectStatement._StraightJoinLateralClause<C, JS>, Statement._IfJoinClause<C, FJ>
        , DialectStatement._CrossJoinCteClause<FS>, DialectStatement._IfStraightJoinClause<C, FJ>
        , CriteriaSpec<C>, Statement.StatementMockSpec
        , CriteriaContextSpec {


    final CriteriaContext context;
    final C criteria;

    final ClauseCreator<FP, JT, JS, JP> clauseCreator;


    final Consumer<_TableBlock> blockConsumer;

    /**
     * <p>
     * private constructor for {@link  LeftParenNestedItem}
     * </p>
     */
    private JoinableClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
            , ClauseCreator<FP, JT, JS, JP> clauseCreator) {

        this.context = context;
        this.criteria = context.criteria();
        this.blockConsumer = blockConsumer;
        this.clauseCreator = clauseCreator;
    }

    /**
     * <p>
     * private constructor for {@link  LeftParenNestedItem#LeftParenNestedItem(CriteriaContext, List)}
     * </p>
     */
    private JoinableClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        this.context = context;
        this.criteria = context.criteria();
        this.blockConsumer = blockConsumer;
        this.clauseCreator = (ClauseCreator<FP, JT, JS, JP>) this;
    }

    /**
     * <p>
     * package constructor for {@link  Statement}
     * </p>
     */
    JoinableClause(CriteriaContext context) {
        this.context = context;
        this.criteria = context.criteria();
        this.blockConsumer = context::onAddBlock;
        this.clauseCreator = (ClauseCreator<FP, JT, JS, JP>) this;
    }

    /**
     * <p>
     * package constructor for {@link  Statement}
     * </p>
     */
    JoinableClause(CriteriaContext context, ClauseCreator<FP, JT, JS, JP> clauseCreator) {
        this.context = context;
        this.criteria = context.criteria();
        this.blockConsumer = context::onAddBlock;
        this.clauseCreator = clauseCreator;
    }


    @Override
    public final C getCriteria() {
        return this.criteria;
    }

    @Override
    public final CriteriaContext getContext() {
        return this.context;
    }

    /*################################## blow JoinSpec method ##################################*/


    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.clauseCreator.createTableClause(_JoinType.LEFT_JOIN, null, table);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.clauseCreator.createTableBlock(_JoinType.LEFT_JOIN, null, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TabularItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.LEFT_JOIN, null, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.LEFT_JOIN, null, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS leftJoin(String cteName) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.LEFT_JOIN, null, this.context.refCte(cteName), "");
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS leftJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.LEFT_JOIN, null, this.context.refCte(cteName), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TabularItem> JS leftJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.LEFT_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS leftJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.LEFT_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP join(TableMeta<?> table) {
        return this.clauseCreator.createTableClause(_JoinType.JOIN, null, table);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.clauseCreator.createTableBlock(_JoinType.JOIN, null, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS join(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.JOIN, null, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS join(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.JOIN, null, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS join(String cteName) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.JOIN, null, this.context.refCte(cteName), "");
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS join(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.JOIN, null, this.context.refCte(cteName), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TabularItem> JS joinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS joinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return this.clauseCreator.createTableClause(_JoinType.RIGHT_JOIN, null, table);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.clauseCreator.createTableBlock(_JoinType.RIGHT_JOIN, null, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.RIGHT_JOIN, null, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.RIGHT_JOIN, null, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS rightJoin(String cteName) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.RIGHT_JOIN, null, this.context.refCte(cteName), "");
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS rightJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.RIGHT_JOIN, null, this.context.refCte(cteName), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS rightJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.RIGHT_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS rightJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.RIGHT_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.clauseCreator.createTableClause(_JoinType.FULL_JOIN, null, table);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.clauseCreator.createTableBlock(_JoinType.FULL_JOIN, null, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.FULL_JOIN, null, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.FULL_JOIN, null, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS fullJoin(String cteName) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.FULL_JOIN, null, this.context.refCte(cteName), "");
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS fullJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.FULL_JOIN, null, this.context.refCte(cteName), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TabularItem> JS fullJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.FULL_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS fullJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.FULL_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP straightJoin(TableMeta<?> table) {
        return this.clauseCreator.createTableClause(_JoinType.STRAIGHT_JOIN, null, table);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.clauseCreator.createTableBlock(_JoinType.STRAIGHT_JOIN, null, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.STRAIGHT_JOIN, null, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.STRAIGHT_JOIN, null, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS straightJoin(String cteName) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.STRAIGHT_JOIN, null, this.context.refCte(cteName), "");
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS straightJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.STRAIGHT_JOIN, null, this.context.refCte(cteName), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TabularItem> JS straightJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.STRAIGHT_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TabularItem> JS straightJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.clauseCreator.createItemBlock(_JoinType.STRAIGHT_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final FP crossJoin(TableMeta<?> table) {
        return this.clauseCreator.createNoOnTableClause(_JoinType.CROSS_JOIN, null, table);
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        block = this.clauseCreator.createNoOnTableBlock(_JoinType.CROSS_JOIN, null, table, tableAlias);
        this.blockConsumer.accept(block);
        return (FT) this;
    }


    @Override
    public final <T extends TabularItem> FS crossJoin(Supplier<T> supplier, String alias) {
        final _TableBlock block;
        block = this.clauseCreator.createNoOnItemBlock(_JoinType.CROSS_JOIN, null, supplier.get(), alias);
        this.blockConsumer.accept(block);
        return (FS) this;
    }


    @Override
    public final <T extends TabularItem> FS crossJoin(Function<C, T> function, String alias) {
        final _TableBlock block;
        block = this.clauseCreator.createNoOnItemBlock(_JoinType.CROSS_JOIN, null, function.apply(this.criteria), alias);
        this.blockConsumer.accept(block);
        return (FS) this;
    }

    @Override
    public final FS crossJoin(String cteName) {
        final _TableBlock block;
        block = this.clauseCreator.createNoOnItemBlock(_JoinType.CROSS_JOIN, null, this.context.refCte(cteName), "");
        this.blockConsumer.accept(block);
        return (FS) this;
    }

    @Override
    public final FS crossJoin(String cteName, String alias) {
        final _TableBlock block;
        block = this.clauseCreator.createNoOnItemBlock(_JoinType.CROSS_JOIN, null, this.context.refCte(cteName), alias);
        this.blockConsumer.accept(block);
        return (FS) this;
    }


    @Override
    public final <T extends TabularItem> FS crossJoinLateral(Supplier<T> supplier, String alias) {
        final _TableBlock block;
        block = this.clauseCreator.createNoOnItemBlock(_JoinType.CROSS_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.blockConsumer.accept(block);
        return (FS) this;
    }

    @Override
    public final <T extends TabularItem> FS crossJoinLateral(Function<C, T> function, String alias) {
        final _TableBlock block;
        block = this.clauseCreator.createNoOnItemBlock(_JoinType.CROSS_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.blockConsumer.accept(block);
        return (FS) this;
    }


    @Override
    public final <B extends JoinItemBlock<C>> FJ ifLeftJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.LEFT_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifLeftJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.LEFT_JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifRightJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.RIGHT_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifRightJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.RIGHT_JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifFullJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.FULL_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifFullJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.FULL_JOIN, function.apply(this.criteria));
    }


    @Override
    public final <B extends JoinItemBlock<C>> FJ ifStraightJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.STRAIGHT_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock<C>> FJ ifStraightJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends ItemBlock<C>> FJ ifCrossJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.CROSS_JOIN, supplier.get());
    }

    @Override
    public final <B extends ItemBlock<C>> FJ ifCrossJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.CROSS_JOIN, function.apply(this.criteria));
    }


    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
        final DialectParser d;
        d = _MockDialects.from(dialect);
        final Stmt stmt;
        if (this instanceof Select) {
            stmt = d.select((Select) this, visible);
        } else if (this instanceof Update) {
            stmt = d.update((Update) this, visible);
        } else if (this instanceof Delete) {
            stmt = d.delete((Delete) this, visible);
        } else if (this instanceof Values) {
            stmt = d.dialectStmt((Values) this, visible);
        } else {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return d.printStmt(stmt, none);
    }

    @Override
    public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
        final Stmt stmt;
        if (this instanceof Select) {
            stmt = _MockDialects.from(dialect).select((Select) this, visible);
        } else if (this instanceof Update) {
            stmt = _MockDialects.from(dialect).update((Update) this, visible);
        } else if (this instanceof Delete) {
            stmt = _MockDialects.from(dialect).delete((Delete) this, visible);
        } else if (this instanceof Values) {
            stmt = _MockDialects.from(dialect).dialectStmt((Values) this, visible);
        } else {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return stmt;
    }

    @Deprecated
    void crossJoinEvent(boolean success) {
        throw CriteriaContextStack.castCriteriaApi(this.context);
    }


    private FJ innerCreateBlockForDynamic(final _JoinType joinType, final @Nullable ItemBlock<C> block) {
        if (block == null) {
            return (FJ) this;
        }
        if (!(block instanceof DynamicBlock)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        final DynamicBlock<?> dynamicBlock = (DynamicBlock<?>) block;
        if (dynamicBlock.criteriaContext != this.context) {
            throw CriteriaUtils.criteriaContextNotMatch(this.context);
        }
        if (dynamicBlock.hasOnClause() == (joinType == _JoinType.CROSS_JOIN)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        this.blockConsumer.accept(this.clauseCreator.createDynamicBlock(joinType, dynamicBlock));
        return (FJ) this;
    }


    interface ClauseCreator<FP, JT, JS, JP> {

        FP createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table);

        _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias);

        _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias);

        _TableBlock createDynamicBlock(_JoinType joinType, DynamicBlock<?> block);

        JP createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table);


        JT createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias);

        JS createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias);

    }

    static ClauseCreator<Void, Void, Void, Void> voidClauseCreator() {
        return VoidClauseCreator.INSTANCE;
    }


    private static final class VoidClauseCreator implements ClauseCreator<Void, Void, Void, Void> {

        private static final VoidClauseCreator INSTANCE = new VoidClauseCreator();

        @Override
        public Void createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaContextStack.peek());
        }

        @Override
        public _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaContextStack.peek());
        }

        @Override
        public _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaContextStack.peek());
        }

        @Override
        public _TableBlock createDynamicBlock(_JoinType joinType, DynamicBlock<?> block) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaContextStack.peek());
        }

        @Override
        public Void createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaContextStack.peek());
        }

        @Override
        public Void createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaContextStack.peek());
        }

        @Override
        public Void createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaContextStack.peek());
        }


    }//VoidClauseCreator


    static abstract class LeftParenNestedItem<C, LT, LS, LP, LJ, JT, JS, JP>
            extends JoinableClause<C, LT, LS, LP, LJ, JT, JS, JP>
            implements Statement._LeftParenClause<C, LT, LS>
            , DialectStatement._DialectLeftParenClause<LP>
            , DialectStatement._LeftParenCteClause<LS>
            , DialectStatement._LeftParenLateralClause<C, LS>
            , NestedItems
            , _NestedItems
            , ClauseCreator<LP, JT, JS, JP> {


        final CriteriaContext criteriaContext;

        private List<_TableBlock> blockList;

        /**
         * <p>
         * private constructor for {@link  #LeftParenNestedItem(CriteriaContext)}
         * </p>
         */
        private LeftParenNestedItem(CriteriaContext criteriaContext, List<_TableBlock> blockList) {
            super(criteriaContext, blockList::add);
            this.criteriaContext = criteriaContext;
            this.blockList = blockList;
        }

        /**
         * <p>
         * package constructor for sub class
         * </p>
         */
        LeftParenNestedItem(CriteriaContext criteriaContext) {
            this(criteriaContext, new ArrayList<>());
        }

        @Override
        public final LP leftParen(TableMeta<?> table) {
            if (this.blockList.size() != 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this.createNoOnTableClause(_JoinType.NONE, null, table);
        }

        @Override
        public final LT leftParen(TableMeta<?> table, String tableAlias) {
            final _TableBlock block;
            block = this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias);
            this.addFirstBlock(block);
            return (LT) this;
        }

        @Override
        public final <T extends TabularItem> LS leftParen(Supplier<T> supplier, String alias) {
            final _TableBlock block;
            block = this.createNoOnItemBlock(_JoinType.NONE, null, supplier.get(), alias);
            this.addFirstBlock(block);
            return (LS) this;
        }

        @Override
        public final <T extends TabularItem> LS leftParen(Function<C, T> function, String alias) {
            final _TableBlock block;
            block = this.createNoOnItemBlock(_JoinType.NONE, null, function.apply(this.criteria), alias);
            this.addFirstBlock(block);
            return (LS) this;
        }

        @Override
        public final <T extends TabularItem> LS leftParenLateral(Supplier<T> supplier, String alias) {
            final _TableBlock block;
            block = this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, supplier.get(), alias);
            this.addFirstBlock(block);
            return (LS) this;
        }

        @Override
        public final <T extends TabularItem> LS leftParenLateral(Function<C, T> function, String alias) {
            final _TableBlock block;
            block = this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, function.apply(this.criteria), alias);
            this.addFirstBlock(block);
            return (LS) this;
        }

        @Override
        public final LS leftParen(String cteName) {
            final _TableBlock block;
            block = this.createNoOnItemBlock(_JoinType.NONE, null, this.criteriaContext.refCte(cteName), "");
            this.addFirstBlock(block);
            return (LS) this;
        }

        @Override
        public final LS leftParen(String cteName, String alias) {
            final _TableBlock block;
            block = this.createNoOnItemBlock(_JoinType.NONE, null, this.criteriaContext.refCte(cteName), alias);
            this.addFirstBlock(block);
            return (LS) this;
        }

        @Override
        public LP createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public JP createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public final List<_TableBlock> tableBlockList() {
            final List<_TableBlock> blockList = this.blockList;
            if (blockList == null || blockList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return blockList;
        }


        final NestedItems endNestedItems() {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList) || blockList.size() == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.blockList = _CollectionUtils.unmodifiableList(blockList);
            return this;
        }


        final _TableBlock getFirstBlock() {
            final List<_TableBlock> blockList = this.blockList;
            if (blockList.size() == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return blockList.get(0);
        }

        private void addFirstBlock(_TableBlock block) {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            blockList.add(block);
        }


    }//LeftBracketNestedItem


    static abstract class OnOrJoinBlock<C, FT, FS, FP, FJ, JT, JS, JP>
            extends JoinableClause<C, FT, FS, FP, FJ, JT, JS, JP>
            implements Statement._OnClause<C, FJ>, _TableBlock, Statement._RightParenClause<NestedItems> {

        private final _JoinType joinType;

        private final TabularItem tableItem;

        private final String alias;

        private final Supplier<NestedItems> endNestedClause;

        private List<_Predicate> predicateList;

        OnOrJoinBlock(LeftParenNestedItem<C, ?, ?, FP, ?, JT, JS, JP> clause
                , _JoinType joinType, TabularItem tableItem, String alias) {
            super(clause.criteriaContext, clause.blockList::add, clause);
            this.joinType = joinType;
            this.tableItem = tableItem;
            this.alias = alias;
            this.endNestedClause = clause::endNestedItems;
        }

        OnOrJoinBlock(LeftParenNestedItem<C, ?, ?, FP, ?, JT, JS, JP> clause
                , TableBlock.BlockParams params) {
            super(clause.criteriaContext, clause.blockList::add, clause);
            this.joinType = params.joinType();
            this.tableItem = params.tableItem();
            this.alias = params.alias();
            this.endNestedClause = clause::endNestedItems;
        }

        @Override
        public final _JoinType jointType() {
            return this.joinType;
        }

        @Override
        public final TabularItem tableItem() {
            return this.tableItem;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final FJ on(IPredicate predicate) {
            return this.onClauseEnd(Collections.singletonList((OperationPredicate) predicate));
        }

        @Override
        public final FJ on(IPredicate predicate1, IPredicate predicate2) {
            final List<_Predicate> predicateList;
            predicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) predicate1,
                    (OperationPredicate) predicate2
            );
            return this.onClauseEnd(predicateList);
        }

        @Override
        public final FJ on(Function<Object, IPredicate> operator, DataField operandField) {
            final OperationPredicate predicate;
            predicate = (OperationPredicate) operator.apply(operandField);
            return this.onClauseEnd(Collections.singletonList(predicate));
        }

        @Override
        public final FJ on(Function<Object, IPredicate> operator1, DataField operandField1
                , Function<Object, IPredicate> operator2, DataField operandField2) {
            final List<_Predicate> predicateList;
            predicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) operator1.apply(operandField1),
                    (OperationPredicate) operator2.apply(operandField2)
            );
            return this.onClauseEnd(predicateList);
        }

        @Override
        public final FJ on(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::addPredicate);
            return this.endOnClause();
        }

        @Override
        public final FJ on(BiConsumer<C, Consumer<IPredicate>> consumer) {
            consumer.accept(this.criteria, this::addPredicate);
            return this.endOnClause();
        }


        @Override
        public final NestedItems rightParen() {
            return this.endNestedClause.get();
        }


        @Override
        public final List<_Predicate> predicateList() {
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                predicateList = Collections.emptyList();
                this.predicateList = predicateList;
            } else if (predicateList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return predicateList;
        }

        private FJ onClauseEnd(final List<_Predicate> predicateList) {
            if (this.predicateList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.predicateList = predicateList;
            return (FJ) this;
        }

        private void addPredicate(final IPredicate predicate) {
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                predicateList = new ArrayList<>();
                this.predicateList = predicateList;
            } else if (!(predicateList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }

            predicateList.add((OperationPredicate) predicate);
        }

        private FJ endOnClause() {
            final List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                throw CriteriaContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
            } else if (!(predicateList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.predicateList = _CollectionUtils.unmodifiableList(predicateList);
            return (FJ) this;
        }


    }//OnOrJoinBlock


}
