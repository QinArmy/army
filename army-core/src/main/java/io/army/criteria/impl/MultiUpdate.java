package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of multi-table update implementation.
 * </p>
 */
abstract class MultiUpdate<C, JT, JS, WR, WA, SR> extends UpdateStatement<C, WR, WA, SR>
        implements Statement.JoinClause<C, JT, JS>, _MultiUpdate {

    private JT noActionTableBlock;

    private JS noActionTablePartBlock;

    MultiUpdate(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS leftJoin(Function<C, T> function, String alias) {
        return this.innerAddTablePartBlock(JoinType.LEFT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS leftJoin(Supplier<T> supplier, String alias) {
        return this.innerAddTablePartBlock(JoinType.LEFT_JOIN, supplier, alias);
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifLeftJoin(Function<C, T> function, String alias) {
        return this.ifAddTablePartBlock(JoinType.LEFT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS ifLeftJoin(Supplier<T> supplier, String alias) {
        return this.ifAddTablePartBlock(JoinType.LEFT_JOIN, supplier, alias);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS join(Function<C, T> function, String alias) {
        return this.innerAddTablePartBlock(JoinType.JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS join(Supplier<T> supplier, String alias) {
        return this.innerAddTablePartBlock(JoinType.JOIN, supplier, alias);
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifJoin(Function<C, T> function, String alias) {
        return this.ifAddTablePartBlock(JoinType.JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS ifJoin(Supplier<T> supplier, String alias) {
        return this.ifAddTablePartBlock(JoinType.JOIN, supplier, alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS rightJoin(Function<C, T> function, String alias) {
        return this.innerAddTablePartBlock(JoinType.RIGHT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS rightJoin(Supplier<T> supplier, String alias) {
        return this.innerAddTablePartBlock(JoinType.RIGHT_JOIN, supplier, alias);
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifRightJoin(Function<C, T> function, String alias) {
        return this.ifAddTablePartBlock(JoinType.RIGHT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS ifRightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddTablePartBlock(JoinType.RIGHT_JOIN, supplier, alias);
    }

    @Override
    public final JT crossJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS crossJoin(Function<C, T> function, String alias) {
        return this.innerAddTablePartBlock(JoinType.CROSS_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS crossJoin(Supplier<T> supplier, String alias) {
        return this.innerAddTablePartBlock(JoinType.CROSS_JOIN, supplier, alias);
    }

    @Override
    public final JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifCrossJoin(Function<C, T> function, String alias) {
        return this.ifAddTablePartBlock(JoinType.CROSS_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS ifCrossJoin(Supplier<T> supplier, String alias) {
        return this.ifAddTablePartBlock(JoinType.CROSS_JOIN, supplier, alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS fullJoin(Function<C, T> function, String alias) {
        return this.innerAddTablePartBlock(JoinType.FULL_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS fullJoin(Supplier<T> supplier, String alias) {
        return this.innerAddTablePartBlock(JoinType.FULL_JOIN, supplier, alias);
    }

    @Override
    public final JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifAddTableBlock(predicate, JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifFullJoin(Function<C, T> function, String alias) {
        return this.ifAddTablePartBlock(JoinType.FULL_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS ifFullJoin(Supplier<T> supplier, String alias) {
        return this.ifAddTablePartBlock(JoinType.FULL_JOIN, supplier, alias);
    }

    abstract JT addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS addTablePartBlock(JoinType joinType, TablePart tablePart, String alias);

    abstract JT createNoActionTableBlock();

    abstract JS createNoActionTablePartBlock();


    /*################################## blow private method ##################################*/


    final <T extends TablePart> JS innerAddTablePartBlock(JoinType joinType, Supplier<T> supplier, String alias) {
        final TablePart tablePart;
        tablePart = supplier.get();
        assert tablePart != null;
        return addTablePartBlock(joinType, tablePart, alias);
    }

    final <T extends TablePart> JS innerAddTablePartBlock(JoinType joinType, Function<C, T> function, String alias) {
        final TablePart tablePart;
        tablePart = function.apply(this.criteria);
        assert tablePart != null;
        return addTablePartBlock(joinType, tablePart, alias);
    }

    final <T extends TablePart> JS ifAddTablePartBlock(JoinType joinType, Supplier<T> supplier, String alias) {
        final TablePart tablePart;
        tablePart = supplier.get();
        final JS block;
        if (tablePart == null) {
            block = getNoActionTablePartBlock();
        } else {
            block = addTablePartBlock(joinType, tablePart, alias);
        }
        return block;
    }

    final <T extends TablePart> JS ifAddTablePartBlock(JoinType joinType, Function<C, T> function, String alias) {
        final TablePart tablePart;
        tablePart = function.apply(this.criteria);
        final JS block;
        if (tablePart == null) {
            block = getNoActionTablePartBlock();
        } else {
            block = addTablePartBlock(joinType, tablePart, alias);
        }
        return block;
    }


    final JT ifAddTableBlock(Predicate<C> predicate, JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = addTableBlock(joinType, table, tableAlias);
        } else {
            JT noActionTableBlock = this.noActionTableBlock;
            if (noActionTableBlock == null) {
                noActionTableBlock = createNoActionTableBlock();
                this.noActionTableBlock = noActionTableBlock;
            }
            block = noActionTableBlock;
        }
        return block;
    }


    private JS getNoActionTablePartBlock() {
        JS block = this.noActionTablePartBlock;
        if (block == null) {
            block = createNoActionTablePartBlock();
            this.noActionTablePartBlock = block;
        }
        return block;
    }


}
