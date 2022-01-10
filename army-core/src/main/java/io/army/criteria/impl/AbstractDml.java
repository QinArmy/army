package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.criteria.TablePart;
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
abstract class AbstractDml<C, JT, JS, WR, WA> extends DmlWhereClause<C, WR, WA>
        implements Statement.JoinClause<C, JT, JS> {


    AbstractDml(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS leftJoin(Function<C, T> function, String alias) {
        return this.addOnBlock(JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS leftJoin(Supplier<T> supplier, String alias) {
        return this.addOnBlock(JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return ifAddTableBlock(predicate, JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifLeftJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS ifLeftJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS join(Function<C, T> function, String alias) {
        return this.addOnBlock(JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS join(Supplier<T> supplier, String alias) {
        return this.addOnBlock(JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS ifJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS rightJoin(Function<C, T> function, String alias) {
        return this.addOnBlock(JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS rightJoin(Supplier<T> supplier, String alias) {
        return this.addOnBlock(JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifRightJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS ifRightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT crossJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS crossJoin(Function<C, T> function, String alias) {
        return this.addOnBlock(JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS crossJoin(Supplier<T> supplier, String alias) {
        return this.addOnBlock(JoinType.CROSS_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifCrossJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS ifCrossJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(JoinType.CROSS_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS fullJoin(Function<C, T> function, String alias) {
        return this.addOnBlock(JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS fullJoin(Supplier<T> supplier, String alias) {
        return this.addOnBlock(JoinType.FULL_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifFullJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS ifFullJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(JoinType.FULL_JOIN, supplier.get(), alias);
    }

    abstract JT addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS addOnBlock(JoinType joinType, TablePart tablePart, String alias);

    abstract JT createNoActionTableBlock();

    abstract JS createNoActionOnBlock();

    abstract JT getNoActionTableBlock();

    abstract JS getNoActionOnBlock();


    /*################################## blow private method ##################################*/


    final <T extends TablePart> JS ifAddOnBlock(JoinType joinType, @Nullable TablePart tablePart, String alias) {
        final JS block;
        if (tablePart == null) {
            block = getNoActionOnBlock();
        } else {
            block = addOnBlock(joinType, tablePart, alias);
        }
        return block;
    }

    final JT ifAddTableBlock(Predicate<C> predicate, JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = addTableBlock(joinType, table, tableAlias);
        } else {
            block = getNoActionTableBlock();
        }
        return block;
    }


}
