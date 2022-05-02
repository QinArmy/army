package io.army.criteria.impl;

import io.army.criteria.DialectStatement;
import io.army.criteria.Statement;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

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
        , CriteriaSpec<C> {

    private final Consumer<_TableBlock> blockConsumer;

    final C criteria;

    JoinableClause(Consumer<_TableBlock> blockConsumer, @Nullable C criteria) {
        this.blockConsumer = blockConsumer;
        this.criteria = criteria;
    }


    /*################################## blow JoinSpec method ##################################*/


    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.createNextClause(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.LEFT_JOIN, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createItemBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createItemBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
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
        return this.createNextClause(_JoinType.JOIN, table);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.JOIN, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createItemBlock(_JoinType.JOIN, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS block;
        block = this.createItemBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
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
        return this.createNextClause(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createItemBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createItemBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
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
    public final FP crossJoin(TableMeta<?> table) {
        return this.createNextNoOnClause(_JoinType.CROSS_JOIN, table);
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        block = this.createNoOnTableBlock(_JoinType.CROSS_JOIN, table, tableAlias);
        this.blockConsumer.accept(block);
        this.crossJoinEvent(true);
        return (FT) this;
    }


    @Override
    public final <T extends TableItem> FS crossJoin(Supplier<T> supplier, String alias) {
        this.blockConsumer.accept(TableBlock.crossBlock(supplier.get(), alias));
        return (FS) this;
    }


    @Override
    public final <T extends TableItem> FS crossJoin(Function<C, T> function, String alias) {
        this.blockConsumer.accept(TableBlock.crossBlock(function.apply(this.criteria), alias));
        return (FS) this;
    }

    @Override
    public final FP ifCrossJoin(Predicate<C> predicate, TableMeta<?> table) {
        final FP clause;
        if (predicate.test(this.criteria)) {
            clause = this.crossJoin(table);
        } else {
            clause = this.getNoActionNextNoOnClause();
            this.crossJoinEvent(false);
        }
        return clause;
    }

    @Override
    public final FT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        if (predicate.test(this.criteria)) {
            this.crossJoin(table, tableAlias);
        } else {
            this.crossJoinEvent(false);
        }
        return (FT) this;
    }


    @Override
    public final <T extends TableItem> FS ifCrossJoin(Supplier<T> supplier, String alias) {
        final TableItem item;
        item = supplier.get();
        if (item != null) {
            this.blockConsumer.accept(TableBlock.crossBlock(item, alias));
        }
        return (FS) this;
    }

    @Override
    public final <T extends TableItem> FS ifCrossJoin(Function<C, T> function, String alias) {
        final TableItem item;
        item = function.apply(this.criteria);
        if (item != null) {
            this.blockConsumer.accept(TableBlock.crossBlock(item, alias));
        }
        return (FS) this;
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.createNextClause(_JoinType.FULL_JOIN, table);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.FULL_JOIN, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createItemBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createItemBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
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
        return this.createNextClause(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createItemBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createItemBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
        this.blockConsumer.accept((_TableBlock) block);
        return block;
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
    public final C getCriteria() {
        return this.criteria;
    }


    abstract _TableBlock createNoOnTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JT getNoActionTableBlock();

    abstract JS createItemBlock(_JoinType joinType, TableItem tableItem, String alias);

    abstract JS getNoActionItemBlock();

    abstract FP createNextNoOnClause(_JoinType joinType, TableMeta<?> table);


    abstract JP getNoActionNextClause();

    abstract FP getNoActionNextNoOnClause();


    abstract JP createNextClause(_JoinType joinType, TableMeta<?> table);

    abstract void crossJoinEvent(boolean success);

    FP createNoActionNextNoOnClause() {
        throw _Exceptions.castCriteriaApi();
    }

    JP createNoActionNextClause() {
        throw _Exceptions.castCriteriaApi();
    }

    private JP ifJoinTable(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        final JP clause;
        if (predicate.test(this.criteria)) {
            clause = this.createNextClause(joinType, table);
        } else {
            clause = this.getNoActionNextClause();
        }
        return clause;
    }


    private JT ifJoinTable(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = this.createTableBlock(joinType, table, tableAlias);
            this.blockConsumer.accept((_TableBlock) block);
        } else {
            block = this.getNoActionTableBlock();
        }
        return block;
    }


    private JS ifJoinItem(_JoinType joinType, @Nullable TableItem item, String alias) {
        final JS block;
        if (item == null) {
            block = this.getNoActionItemBlock();
        } else {
            block = this.createItemBlock(joinType, item, alias);
            this.blockConsumer.accept((_TableBlock) block);
        }
        return block;
    }


}
