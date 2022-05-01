package io.army.criteria.impl;

import io.army.criteria.DialectStatement;
import io.army.criteria.NestedItems;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._TableItemGroup;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
abstract class AbstractTableItemGroup<C, JT, JS, JP>
        implements _TableItemGroup, DialectStatement._DialectJoinClause<C, JT, JS, JP>
        , NestedItems.TableItemGroupSpec {

    final C criteria;

    List<_TableBlock> tableBlockList;

    private JT noActionTableBlock;

    private JS noActionTablePartBlock;

    private boolean prepared;

    AbstractTableItemGroup(TableItem tableItem, String alias, @Nullable C criteria) {
        this.criteria = criteria;
        this.tableBlockList = new ArrayList<>();
        this.tableBlockList.add(TableBlock.noneBlock(tableItem, alias));
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.LEFT_JOIN, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Function<C, T> function, String alias) {
        return this.ifJoinTableItem(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTableItem(_JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.JOIN, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.JOIN, supplier.get(), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Function<C, T> function, String alias) {
        return this.ifJoinTableItem(_JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTableItem(_JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Function<C, T> function, String alias) {
        return this.ifJoinTableItem(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTableItem(_JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT crossJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.CROSS_JOIN, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS crossJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS crossJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.CROSS_JOIN, supplier.get(), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifCrossJoin(Function<C, T> function, String alias) {
        return this.ifJoinTableItem(_JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifCrossJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTableItem(_JoinType.CROSS_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.FULL_JOIN, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Function<C, T> function, String alias) {
        return this.ifJoinTableItem(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTableItem(_JoinType.FULL_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias) {
        return this.ifJoinTable(predicate, _JoinType.STRAIGHT_JOIN, table, alias);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifJoinTableItem(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> JS ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTableItem(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JP ifLeftJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJointTableBeforeAs(predicate, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JP join(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.JOIN, table);
    }

    @Override
    public final JP ifJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJointTableBeforeAs(predicate, _JoinType.JOIN, table);
    }

    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JP ifRightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJointTableBeforeAs(predicate, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JP straightJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JP ifStraightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJointTableBeforeAs(predicate, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.FULL_JOIN, table);
    }

    @Override
    public final JP ifFullJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifJointTableBeforeAs(predicate, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final NestedItems asTableGroup() {
        _Assert.nonPrepared(this.prepared);
        this.tableBlockList = _CollectionUtils.asUnmodifiableList(this.tableBlockList);
        this.prepared = true;
        return this;
    }

    @Override
    public final List<? extends _TableBlock> tableGroup() {
        _Assert.prepared(this.prepared);
        return this.tableBlockList;
    }

    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(_JoinType joinType, TableItem tableItem, String alias);

    abstract JT createNoActionTableBlock();

    abstract JS createNoActionOnBlock();

    JP createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }

    JP ifJointTableBeforeAs(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }


    private JT getNoActionTableBlock() {
        JT noActionTableBlock = this.noActionTableBlock;
        if (noActionTableBlock == null) {
            noActionTableBlock = createNoActionTableBlock();
            this.noActionTableBlock = noActionTableBlock;
        }
        return noActionTableBlock;
    }

    private JS getNoActionOnBlock() {
        JS noActionTablePartBlock = this.noActionTablePartBlock;
        if (noActionTablePartBlock == null) {
            noActionTablePartBlock = createNoActionOnBlock();
            this.noActionTablePartBlock = noActionTablePartBlock;
        }
        return noActionTablePartBlock;
    }


    private JT ifJoinTable(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table, String alias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = this.createTableBlock(joinType, table, alias);
            this.tableBlockList.add((_TableBlock) block);
        } else {
            block = getNoActionTableBlock();
        }
        return block;
    }

    private JS ifJoinTableItem(_JoinType joinType, @Nullable TableItem tableItem, String alias) {
        final JS block;
        if (tableItem == null) {
            block = this.getNoActionOnBlock();
        } else {
            block = this.createOnBlock(joinType, tableItem, alias);
            this.tableBlockList.add((_TableBlock) block);
        }
        return block;
    }


}
