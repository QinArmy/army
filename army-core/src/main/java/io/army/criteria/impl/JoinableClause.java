package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Consumer;
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
abstract class JoinableClause<FT, FS, FC, JT, JS, JC, WR, WA, OR, LR>
        extends WhereClause<WR, WA, OR, LR>
        implements Statement._JoinModifierClause<JT, JS>, Statement._CrossJoinModifierClause<FT, FS>
        , DialectStatement._JoinModifierCteClause<JC>, DialectStatement._CrossJoinModifierCteClause<FC>
        , DialectStatement._StraightJoinModifierClause<JT, JS>, DialectStatement._StraightJoinModifierCteClause<JC> {


    final Consumer<_TableBlock> blockConsumer;

    /**
     * <p>
     * private constructor
     * </p>
     */
    private JoinableClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        super(context);
        this.blockConsumer = blockConsumer;
    }


    /**
     * <p>
     * package constructor for {@link  Statement}
     * </p>
     */
    JoinableClause(CriteriaContext context) {
        super(context);
        this.blockConsumer = context::onAddBlock;
    }


    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final JT leftJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        assert wordAs == SQLs.AS;
        return this.onAddTableItem(_JoinType.LEFT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> leftJoin(Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.LEFT_JOIN, null, supplier.get());
    }


    @Override
    public final JC leftJoin(String cteName) {
        return this.onAddCteItem(_JoinType.LEFT_JOIN, null, cteName, "");
    }

    @Override
    public final JC leftJoin(String cteName, SQLs.WordAs wordAs, String alias) {
        assert wordAs == SQLs.AS;
        return this.onAddCteItem(_JoinType.LEFT_JOIN, null, cteName, alias);
    }

    @Override
    public final JT leftJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.LEFT_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> leftJoin(Query.TabularModifier modifier, Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.LEFT_JOIN, modifier, supplier.get());
    }

    @Override
    public final JC leftJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.LEFT_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC leftJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onAddCteItem(_JoinType.LEFT_JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT join(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onAddTableItem(_JoinType.JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> join(Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.JOIN, null, supplier.get());
    }

    @Override
    public final JC join(String cteName) {
        return this.onAddCteItem(_JoinType.JOIN, null, cteName, "");
    }

    @Override
    public final JC join(String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.JOIN, null, cteName, alias);
    }

    @Override
    public final JT join(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> join(Query.TabularModifier modifier, Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.JOIN, modifier, supplier.get());
    }

    @Override
    public final JC join(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.JOIN, modifier, cteName, "");
    }

    @Override
    public final JC join(Query.TabularModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.RIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> rightJoin(Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.RIGHT_JOIN, null, supplier.get());
    }

    @Override
    public final JC rightJoin(String cteName) {
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, null, cteName, "");
    }

    @Override
    public final JC rightJoin(String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, null, cteName, alias);
    }

    @Override
    public final JT rightJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.RIGHT_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> rightJoin(Query.TabularModifier modifier, Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.RIGHT_JOIN, modifier, supplier.get());
    }

    @Override
    public final JC rightJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC rightJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.FULL_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> fullJoin(Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.FULL_JOIN, null, supplier.get());
    }

    @Override
    public final JC fullJoin(String cteName) {
        return this.onAddCteItem(_JoinType.FULL_JOIN, null, cteName, "");
    }

    @Override
    public final JC fullJoin(String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.FULL_JOIN, null, cteName, alias);
    }

    @Override
    public final JT fullJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.FULL_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> fullJoin(Query.TabularModifier modifier, Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.FULL_JOIN, modifier, supplier.get());
    }

    @Override
    public final JC fullJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.FULL_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC fullJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.FULL_JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.STRAIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> straightJoin(Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.STRAIGHT_JOIN, null, supplier.get());
    }

    @Override
    public final JC straightJoin(String cteName) {
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, null, cteName, "");
    }

    @Override
    public final JC straightJoin(String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, null, cteName, alias);
    }

    @Override
    public final JC straightJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC straightJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, modifier, cteName, alias);
    }


    @Override
    public final JT straightJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddTableItem(_JoinType.STRAIGHT_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends TabularItem> Statement._AsClause<JS> straightJoin(Query.TabularModifier modifier, Supplier<T> supplier) {
        return this.onAddQueryItem(_JoinType.STRAIGHT_JOIN, modifier, supplier.get());
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onAddNoOnTableItem(_JoinType.CROSS_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> Statement._AsClause<FS> crossJoin(Supplier<T> supplier) {
        return this.onAddNoOnQueryItem(_JoinType.CROSS_JOIN, null, supplier.get());
    }


    @Override
    public final FC crossJoin(String cteName) {
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, null, cteName, "");
    }

    @Override
    public final FC crossJoin(String cteName, SQLs.WordAs wordAs, String alias) {
        assert wordAs == SQLs.AS;
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, null, cteName, alias);
    }

    @Override
    public final FT crossJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        assert wordAs == SQLs.AS;
        return this.onAddNoOnTableItem(_JoinType.CROSS_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends TabularItem> Statement._AsClause<FS> crossJoin(Query.TabularModifier modifier, Supplier<T> supplier) {
        return this.onAddNoOnQueryItem(_JoinType.CROSS_JOIN, modifier, supplier.get());
    }

    @Override
    public final FC crossJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, modifier, cteName, "");
    }

    @Override
    public final FC crossJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs wordAs, String alias) {
        assert wordAs == SQLs.AS;
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, modifier, cteName, alias);
    }

    abstract _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier itemWord, TableMeta<?> table, String alias);

    abstract _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier itemWord, TabularItem tableItem, String alias);


    abstract JT createTableBlock(_JoinType joinType, @Nullable Query.TableModifier itemWord, TableMeta<?> table, String tableAlias);

    abstract JS createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier itemWord, TabularItem tableItem, String alias);

    abstract JC createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier itemWord, TabularItem tableItem, String alias);


    final FT onAddNoOnTableItem(_JoinType joinType, @Nullable Query.TableModifier modifier
            , TableMeta<?> table, String alias) {

        final _TableBlock block;
        block = createNoOnTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        return (FT) this;
    }

    /**
     * @see #crossJoin(Supplier)
     * @see #crossJoin(Query.TabularModifier, Supplier)
     */
    final Statement._AsClause<FS> onAddNoOnQueryItem(final _JoinType joinType
            , final @Nullable Query.TabularModifier modifier, final @Nullable TabularItem item) {
        if (item == null) {
            throw ContextStack.nullPointer(this.context);
        }

        return alias -> {
            final _TableBlock block;
            block = createNoOnItemBlock(joinType, modifier, item, alias);
            this.blockConsumer.accept(block);
            return (FS) JoinableClause.this;
        };
    }

    final FC onAddNoOnCteItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , String cteName, String alias) {

        final _TableBlock block;
        block = createNoOnItemBlock(joinType, modifier, this.context.refCte(cteName), alias);
        this.blockConsumer.accept(block);
        return (FC) this;
    }

    final JT onAddTableItem(_JoinType joinType, @Nullable Query.TableModifier modifier
            , TableMeta<?> table, String alias) {

        final JT block;
        block = this.createTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }


    final Statement._AsClause<JS> onAddQueryItem(final _JoinType joinType
            , final @Nullable Query.TabularModifier modifier, final @Nullable TabularItem item) {
        if (item == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return alias -> {
            final JS block;
            block = this.createItemBlock(joinType, modifier, item, alias);
            this.blockConsumer.accept((_TableBlock) block);
            return block;
        };
    }

    final JC onAddCteItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , @Nullable String cteName, String alias) {
        if (!(modifier == null || modifier instanceof ItemWord)) {
            throw errorTabularModifier(modifier);
        } else if (cteName == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final JC block;
        block = this.createCteBlock(joinType, modifier, this.context.refCte(cteName), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }


    private CriteriaException errorTabularModifier(@Nullable Query.TabularModifier modifier) {
        String m = String.format("error %s instance %s.", Query.TabularModifier.class.getName(), modifier);
        return ContextStack.criteriaError(this.context, m);
    }


}
