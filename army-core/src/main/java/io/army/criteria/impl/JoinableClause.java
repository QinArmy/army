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
        , CriteriaSpec<C>, Statement.StatementMockSpec {

    ClauseSupplier clauseSupplier;

    final CriteriaContext criteriaContext;

    final C criteria;

    JoinableClause(ClauseSupplier clauseSupplier, @Nullable C criteria) {
        this.criteriaContext = null;
        this.clauseSupplier = clauseSupplier;
        this.criteria = criteria;
    }

    JoinableClause(@Nullable C criteria) {
        if (!(this instanceof ClauseSupplier)) {
            throw new IllegalStateException();
        }
        this.criteriaContext = null;
        this.clauseSupplier = (ClauseSupplier) this;
        this.criteria = criteria;
    }

    JoinableClause(CriteriaContext criteriaContext) {
        this.criteriaContext = criteriaContext;
        this.criteria = criteriaContext.criteria();
    }


    /*################################## blow JoinSpec method ##################################*/


    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.createTableClause(_JoinType.LEFT_JOIN, null, table);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.createTableBlock(_JoinType.LEFT_JOIN, null, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.LEFT_JOIN, null, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.LEFT_JOIN, null, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS leftJoin(String cteName) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.LEFT_JOIN, null, this.criteriaContext.refCte(cteName), "");
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS leftJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.LEFT_JOIN, null, this.criteriaContext.refCte(cteName), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TableItem> JS leftJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.LEFT_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS leftJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.LEFT_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP join(TableMeta<?> table) {
        return this.createTableClause(_JoinType.JOIN, null, table);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.createTableBlock(_JoinType.JOIN, null, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.JOIN, null, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.JOIN, null, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS join(String cteName) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.JOIN, null, this.criteriaContext.refCte(cteName), "");
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS join(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.JOIN, null, this.criteriaContext.refCte(cteName), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TableItem> JS joinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS joinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return this.createTableClause(_JoinType.RIGHT_JOIN, null, table);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.createTableBlock(_JoinType.RIGHT_JOIN, null, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.RIGHT_JOIN, null, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.RIGHT_JOIN, null, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS rightJoin(String cteName) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.RIGHT_JOIN, null, this.criteriaContext.refCte(cteName), "");
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS rightJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.RIGHT_JOIN, null, this.criteriaContext.refCte(cteName), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS rightJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.RIGHT_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS rightJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.RIGHT_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.createTableClause(_JoinType.FULL_JOIN, null, table);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.createTableBlock(_JoinType.FULL_JOIN, null, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.FULL_JOIN, null, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.FULL_JOIN, null, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS fullJoin(String cteName) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.FULL_JOIN, null, this.criteriaContext.refCte(cteName), "");
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS fullJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.FULL_JOIN, null, this.criteriaContext.refCte(cteName), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TableItem> JS fullJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.FULL_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS fullJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.FULL_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JP straightJoin(TableMeta<?> table) {
        return this.createTableClause(_JoinType.STRAIGHT_JOIN, null, table);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT blockClause;
        blockClause = this.createTableBlock(_JoinType.STRAIGHT_JOIN, null, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.STRAIGHT_JOIN, null, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.STRAIGHT_JOIN, null, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS straightJoin(String cteName) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.STRAIGHT_JOIN, null, this.criteriaContext.refCte(cteName), "");
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final JS straightJoin(String cteName, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.STRAIGHT_JOIN, null, this.criteriaContext.refCte(cteName), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }


    @Override
    public final <T extends TableItem> JS straightJoinLateral(Supplier<T> supplier, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.STRAIGHT_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final <T extends TableItem> JS straightJoinLateral(Function<C, T> function, String alias) {
        final JS blockClause;
        blockClause = this.createItemBlock(_JoinType.STRAIGHT_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) blockClause);
        return blockClause;
    }

    @Override
    public final FP crossJoin(TableMeta<?> table) {
        return this.createNoOnTableClause(_JoinType.CROSS_JOIN, null, table);
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        block = this.createNoOnTableBlock(_JoinType.CROSS_JOIN, null, table, tableAlias);
        this.criteriaContext.onAddBlock(block);
        return (FT) this;
    }


    @Override
    public final <T extends TableItem> FS crossJoin(Supplier<T> supplier, String alias) {
        final _TableBlock block;
        block = this.creatNoOnItemBlock(_JoinType.CROSS_JOIN, null, supplier.get(), alias);
        this.criteriaContext.onAddBlock(block);
        return (FS) this;
    }


    @Override
    public final <T extends TableItem> FS crossJoin(Function<C, T> function, String alias) {
        final _TableBlock block;
        block = this.creatNoOnItemBlock(_JoinType.CROSS_JOIN, null, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock(block);
        return (FS) this;
    }

    @Override
    public final FS crossJoin(String cteName) {
        final _TableBlock block;
        block = this.creatNoOnItemBlock(_JoinType.CROSS_JOIN, null, this.criteriaContext.refCte(cteName), "");
        this.criteriaContext.onAddBlock(block);
        return (FS) this;
    }

    @Override
    public final FS crossJoin(String cteName, String alias) {
        final _TableBlock block;
        block = this.creatNoOnItemBlock(_JoinType.CROSS_JOIN, null, this.criteriaContext.refCte(cteName), alias);
        this.criteriaContext.onAddBlock(block);
        return (FS) this;
    }


    @Override
    public final <T extends TableItem> FS crossJoinLateral(Supplier<T> supplier, String alias) {
        final _TableBlock block;
        block = this.creatNoOnItemBlock(_JoinType.CROSS_JOIN, ItemWord.LATERAL, supplier.get(), alias);
        this.criteriaContext.onAddBlock(block);
        return (FS) this;
    }

    @Override
    public final <T extends TableItem> FS crossJoinLateral(Function<C, T> function, String alias) {
        final _TableBlock block;
        block = this.creatNoOnItemBlock(_JoinType.CROSS_JOIN, ItemWord.LATERAL, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock(block);
        return (FS) this;
    }


    @Override
    public final <B extends JoinItemBlock> FJ ifLeftJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.LEFT_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifLeftJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.LEFT_JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifRightJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.RIGHT_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifRightJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.RIGHT_JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifFullJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.FULL_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifFullJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.FULL_JOIN, function.apply(this.criteria));
    }


    @Override
    public final <B extends JoinItemBlock> FJ ifStraightJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.STRAIGHT_JOIN, supplier.get());
    }

    @Override
    public final <B extends JoinItemBlock> FJ ifStraightJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria));
    }

    @Override
    public final <B extends CrossItemBlock> FJ ifCrossJoin(Supplier<B> supplier) {
        return this.innerCreateBlockForDynamic(_JoinType.CROSS_JOIN, supplier.get());
    }

    @Override
    public final <B extends CrossItemBlock> FJ ifCrossJoin(Function<C, B> function) {
        return this.innerCreateBlockForDynamic(_JoinType.CROSS_JOIN, function.apply(this.criteria));
    }

    @Override
    public final C getCriteria() {
        return this.criteria;
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
            throw _Exceptions.castCriteriaApi();
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
            throw _Exceptions.castCriteriaApi();
        }
        return stmt;
    }

    @Deprecated
    void crossJoinEvent(boolean success) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    FP createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    _TableBlock creatNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    _TableBlock createBlockForDynamic(_JoinType joinType, DynamicBlock block) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }


    JP createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }


    JT createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    JS createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }


    private FJ innerCreateBlockForDynamic(final _JoinType joinType, final @Nullable Object block) {
        if (block == null) {
            return (FJ) this;
        }
        if (!(block instanceof DynamicBlock)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.criteriaContext.onAddBlock(this.createBlockForDynamic(joinType, (DynamicBlock) block));
        return (FJ) this;
    }


    static ClauseSupplier voidClauseSuppler() {
        return VoidClauseSuppler.INSTANCE;
    }

    interface ClauseSupplier {

        default _TableBlock createAndAddBlock(_JoinType joinType, TableItem item, String alias) {
            throw new UnsupportedOperationException();
        }

        default Object createClause(_JoinType joinType, TableMeta<?> table) {
            throw new UnsupportedOperationException();
        }

        default Object getNoActionClause(_JoinType joinType) {
            throw new UnsupportedOperationException();
        }

        default Object getNoActionClauseBeforeAs(_JoinType joinType) {
            throw new UnsupportedOperationException();
        }

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
            throw new UnsupportedOperationException();
        }

        @Override
        public final LS leftBracket(String cteName, String alias) {
            if (this.blockList.size() != 0) {
                throw _Exceptions.castCriteriaApi();
            }
            throw new UnsupportedOperationException();
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
        public final List<_TableBlock> tableBlockList() {
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


    static abstract class NoActionOnOrJoinBlock<C, FT, FS, FP, FJ, JT, JS, JP>
            extends JoinableClause<C, FT, FS, FP, FJ, JT, JS, JP>
            implements Statement._OnClause<C, FS>, Statement._RightParenClause<NestedItems> {


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
        public final FS on(Consumer<Consumer<IPredicate>> consumer) {
            return (FS) this;
        }

        @Override
        public final FS on(BiConsumer<C, Consumer<IPredicate>> consumer) {
            return (FS) this;
        }

        @Override
        public final NestedItems rightParen() {
            return ((NestedClauseSupplier) this.clauseSupplier).endNested();
        }

        @Override
        final void crossJoinEvent(boolean success) {
            //no-op
        }


    }//NoActionOnOrJoinBlock


    static abstract class OnOrJoinBlock<C, FT, FS, FP, FJ, JT, JS, JP>
            extends JoinableClause<C, FT, FS, FP, FJ, JT, JS, JP>
            implements Statement._OnClause<C, FS>, _TableBlock, Statement._RightParenClause<NestedItems> {

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
        public final FS on(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::addPredicate);
            final List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                throw _Exceptions.predicateListIsEmpty();//TODO
            }
            this.predicateList = _CollectionUtils.unmodifiableList(predicateList);
            return (FS) this;
        }

        @Override
        public final FS on(BiConsumer<C, Consumer<IPredicate>> consumer) {
            consumer.accept(this.criteria, this::addPredicate);
            final List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                throw _Exceptions.predicateListIsEmpty();//TODO
            }
            this.predicateList = _CollectionUtils.unmodifiableList(predicateList);
            return (FS) this;
        }

        @Override
        public final NestedItems rightParen() {
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

        private void addPredicate(final IPredicate predicate) {
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                predicateList = new ArrayList<>();
                this.predicateList = predicateList;
            } else if (!(predicateList instanceof ArrayList)) {
                throw _Exceptions.castCriteriaApi();//TODO CriteriaContextStack
            }
            predicateList.add((OperationPredicate) predicate);

        }


    }//OnOrJoinBlock


}
