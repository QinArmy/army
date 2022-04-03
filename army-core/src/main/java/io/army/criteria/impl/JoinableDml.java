package io.army.criteria.impl;

import io.army.criteria.DialectStatement;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._Dml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This is base class of below:
 *     <ul>
 *         <li>{@link MultiUpdate}</li>
 *         <li>{@link MultiDelete}</li>
 *     </ul>
 * </p>
 */
abstract class JoinableDml<C, JT, JS, WR, WA> extends DmlWhereClause<C, WR, WA>
        implements DialectStatement.DialectJoinClause<C, JT, JS>, _Dml {


    JoinableDml(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.LEFT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, _JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, _JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, _JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT crossJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.CROSS_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS crossJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS crossJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.CROSS_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, _JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifCrossJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifCrossJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.CROSS_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.FULL_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, _JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
    }


    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias) {
        return this.ifAddTableBlock(predicate, _JoinType.STRAIGHT_JOIN, table, alias);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(_JoinType joinType, TableItem tableItem, String alias);

    abstract JT createNoActionTableBlock();

    abstract JS createNoActionOnBlock();

    abstract JT getNoActionTableBlock();

    abstract JS getNoActionOnBlock();


    /*################################## blow private method ##################################*/


    final JS ifAddOnBlock(_JoinType joinType, @Nullable TableItem tableItem, String alias) {
        final JS block;
        if (tableItem == null) {
            block = getNoActionOnBlock();
        } else {
            block = createOnBlock(joinType, tableItem, alias);
        }
        return block;
    }

    final JT ifAddTableBlock(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = createTableBlock(joinType, table, tableAlias);
        } else {
            block = getNoActionTableBlock();
        }
        return block;
    }


}
