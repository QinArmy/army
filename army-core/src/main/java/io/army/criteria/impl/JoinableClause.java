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
abstract class JoinableClause<FT, FS, FC, JT, JS, JC, WR, WA, OR>
        extends WhereClause<WR, WA, OR>
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
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.LEFT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS leftJoin(Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.LEFT_JOIN, null, supplier.get(), alias);
    }


    @Override
    public final JC leftJoin(String cteName) {
        return this.onAddCteItem(_JoinType.LEFT_JOIN, null, cteName, "");
    }

    @Override
    public final JC leftJoin(String cteName, String alias) {
        return this.onAddCteItem(_JoinType.LEFT_JOIN, null, cteName, alias);
    }

    @Override
    public final JT leftJoin(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.LEFT_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends TabularItem> JS leftJoin(Query.TabularModifier modifier, Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.LEFT_JOIN, modifier, supplier.get(), alias);
    }

    @Override
    public final JC leftJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.LEFT_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC leftJoin(Query.TabularModifier modifier, String cteName, String alias) {
        return this.onAddCteItem(_JoinType.LEFT_JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS join(Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.JOIN, null, supplier.get(), alias);
    }

    @Override
    public final JC join(String cteName) {
        return this.onAddCteItem(_JoinType.JOIN, null, cteName, "");
    }

    @Override
    public final JC join(String cteName, String alias) {
        return this.onAddCteItem(_JoinType.JOIN, null, cteName, alias);
    }

    @Override
    public final JT join(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS join(Query.TabularModifier modifier, Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.JOIN, modifier, supplier.get(), alias);
    }

    @Override
    public final JC join(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.JOIN, modifier, cteName, "");
    }

    @Override
    public final JC join(Query.TabularModifier modifier, String cteName, String alias) {
        return this.onAddCteItem(_JoinType.JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.RIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS rightJoin(Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.RIGHT_JOIN, null, supplier.get(), alias);
    }

    @Override
    public final JC rightJoin(String cteName) {
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, null, cteName, "");
    }

    @Override
    public final JC rightJoin(String cteName, String alias) {
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, null, cteName, alias);
    }

    @Override
    public final JT rightJoin(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.RIGHT_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS rightJoin(Query.TabularModifier modifier, Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.RIGHT_JOIN, modifier, supplier.get(), alias);
    }

    @Override
    public final JC rightJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC rightJoin(Query.TabularModifier modifier, String cteName, String alias) {
        return this.onAddCteItem(_JoinType.RIGHT_JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.FULL_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS fullJoin(Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.FULL_JOIN, null, supplier.get(), alias);
    }
    @Override
    public final JC fullJoin(String cteName) {
        return this.onAddCteItem(_JoinType.FULL_JOIN, null, cteName, "");
    }

    @Override
    public final JC fullJoin(String cteName, String alias) {
        return this.onAddCteItem(_JoinType.FULL_JOIN, null, cteName, alias);
    }

    @Override
    public final JT fullJoin(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.FULL_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS fullJoin(Query.TabularModifier modifier, Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.FULL_JOIN, modifier, supplier.get(), alias);
    }

    @Override
    public final JC fullJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.FULL_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC fullJoin(Query.TabularModifier modifier, String cteName, String alias) {
        return this.onAddCteItem(_JoinType.FULL_JOIN, modifier, cteName, alias);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.STRAIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> JS straightJoin(Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.STRAIGHT_JOIN, null, supplier.get(), alias);
    }

    @Override
    public final JC straightJoin(String cteName) {
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, null, cteName, "");
    }

    @Override
    public final JC straightJoin(String cteName, String alias) {
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, null, cteName, alias);
    }

    @Override
    public final JC straightJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, modifier, cteName, "");
    }

    @Override
    public final JC straightJoin(Query.TabularModifier modifier, String cteName, String alias) {
        return this.onAddCteItem(_JoinType.STRAIGHT_JOIN, modifier, cteName, alias);
    }


    @Override
    public final JT straightJoin(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias) {
        return this.onAddTableItem(_JoinType.STRAIGHT_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends TabularItem> JS straightJoin(Query.TabularModifier modifier, Supplier<T> supplier, String alias) {
        return this.onAddQueryItem(_JoinType.STRAIGHT_JOIN, modifier, supplier.get(), alias);
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, String tableAlias) {
        return this.onAddNoOnTableItem(_JoinType.CROSS_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> FS crossJoin(Supplier<T> supplier, String alias) {
        return this.onAddNoOnQueryItem(_JoinType.CROSS_JOIN, null, supplier.get(), alias);
    }


    @Override
    public final FC crossJoin(String cteName) {
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, null, cteName, "");
    }

    @Override
    public final FC crossJoin(String cteName, String alias) {
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, null, cteName, alias);
    }

    @Override
    public final FT crossJoin(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias) {
        return this.onAddNoOnTableItem(_JoinType.CROSS_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends TabularItem> FS crossJoin(Query.TabularModifier modifier, Supplier<T> supplier, String alias) {
        return this.onAddNoOnQueryItem(_JoinType.CROSS_JOIN, modifier, supplier.get(), alias);
    }

    @Override
    public final FC crossJoin(Query.TabularModifier modifier, String cteName) {
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, modifier, cteName, "");
    }

    @Override
    public final FC crossJoin(Query.TabularModifier modifier, String cteName, String alias) {
        return this.onAddNoOnCteItem(_JoinType.CROSS_JOIN, modifier, cteName, alias);
    }

    abstract _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias);

    abstract _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias);


    abstract JT createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias);

    abstract JS createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias);

    abstract JC createCteBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias);


    @Deprecated
    void crossJoinEvent(boolean success) {
        throw ContextStack.castCriteriaApi(this.context);
    }

    final FT onAddNoOnTableItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , TableMeta<?> table, String alias) {
        if (!(modifier == null || modifier instanceof ItemWord)) {
            throw errorTabularModifier(modifier);
        }
        final _TableBlock block;
        block = createNoOnItemBlock(joinType, (ItemWord) modifier, table, alias);
        this.blockConsumer.accept(block);
        return (FT) this;
    }

    final FS onAddNoOnQueryItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , @Nullable TabularItem item, String alias) {
        if (!(modifier == null || modifier instanceof ItemWord)) {
            throw errorTabularModifier(modifier);
        } else if (item == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final _TableBlock block;
        block = createNoOnItemBlock(joinType, (ItemWord) modifier, item, alias);
        this.blockConsumer.accept(block);
        return (FS) this;
    }

    final FC onAddNoOnCteItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , String cteName, String alias) {
        if (!(modifier == null || modifier instanceof ItemWord)) {
            throw errorTabularModifier(modifier);
        }
        final _TableBlock block;
        block = createNoOnItemBlock(joinType, (ItemWord) modifier, this.context.refCte(cteName), alias);
        this.blockConsumer.accept(block);
        return (FC) this;
    }

    final JT onAddTableItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , TableMeta<?> table, String alias) {
        if (!(modifier == null || modifier instanceof ItemWord)) {
            throw errorTabularModifier(modifier);
        }
        final JT block;
        block = this.createTableBlock(joinType, (ItemWord) modifier, table, alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }


    final JS onAddQueryItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , @Nullable TabularItem item, String alias) {
        if (!(modifier == null || modifier instanceof ItemWord)) {
            throw errorTabularModifier(modifier);
        } else if (item == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final JS block;
        block = this.createItemBlock(joinType, (ItemWord) modifier, item, alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    final JC onAddCteItem(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , @Nullable String cteName, String alias) {
        if (!(modifier == null || modifier instanceof ItemWord)) {
            throw errorTabularModifier(modifier);
        } else if (cteName == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final JC block;
        block = this.createCteBlock(joinType, (ItemWord) modifier, this.context.refCte(cteName), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }


    private CriteriaException errorTabularModifier(@Nullable Query.TabularModifier modifier) {
        String m = String.format("error %s instance %s.", Query.TabularModifier.class.getName(), modifier);
        return ContextStack.criteriaError(this.context, m);
    }


}
