package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class SimpleQuery<C, Q extends Query, SR, FT, FS, JT, JS, WR, AR, GR, HR, OR, LR, UR, SP>
        extends PartQuery<C, Q, UR, OR, LR, SP> implements Query.SelectClause<C, SR>, Statement.JoinClause<C, JT, JS>
        , Statement.WhereClause<C, WR, AR>, Statement.WhereAndClause<C, AR>
        , Query.GroupClause<C, GR>, Query.HavingClause<C, HR>, _Query, Statement.FromClause<C, FT, FS> {


    private List<Hint> hintList;

    private List<SQLModifier> modifierList;

    private List<? extends SelectItem> selectPartList;

    private List<_TableBlock> tableBlockList;

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<ArmySortItem> groupByList;

    private List<_Predicate> havingList;

    private JT noActionTableBlock;

    private JS noActionTablePartBlock;


    SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final <S extends SelectItem> SR select(List<Hint> hints, List<SQLModifier> modifiers, Function<C, List<S>> function) {
        return this.select(hints, modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem> SR select(List<Hint> hints, List<SQLModifier> modifiers, List<S> selectPartList) {
        this.hintList = CollectionUtils.asUnmodifiableList(hints);
        this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
        return this.select(selectPartList);
    }

    @Override
    public final <S extends SelectItem> SR select(SQLModifier sqlModifier, List<S> selectPartList) {
        return this.select(Collections.emptyList(), Collections.singletonList(sqlModifier), selectPartList);
    }

    @Override
    public final <S extends SelectItem> SR select(List<SQLModifier> modifiers, Function<C, List<S>> function) {
        return this.select(Collections.emptyList(), modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem> SR select(List<SQLModifier> modifiers, Supplier<List<S>> supplier) {
        return this.select(Collections.emptyList(), modifiers, supplier.get());
    }

    @Override
    public final <S extends SelectItem> SR select(Function<C, List<S>> function) {
        return this.select(function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem> SR select(Supplier<List<S>> supplier) {
        return this.select(supplier.get());
    }

    @Override
    public final <S extends SelectItem> SR select(Consumer<List<S>> consumer) {
        final List<S> list = new ArrayList<>();
        consumer.accept(list);
        return this.select(list);
    }

    @Override
    public final SR select(SQLModifier sqlModifier, SelectItem selectItem) {
        this.modifierList = Collections.singletonList(sqlModifier);
        return this.select(selectItem);
    }

    @Override
    public final SR select(SelectItem selectItem) {
        final List<? extends SelectItem> selectPartList;
        selectPartList = Collections.singletonList(selectItem);

        this.criteriaContext.selectList(selectPartList); // notify context

        this.selectPartList = selectPartList;
        return (SR) this;
    }


    @Override
    public final SR select(SelectItem selectItem1, SelectItem selectItem2) {
        final List<? extends SelectItem> selectPartList;
        selectPartList = ArrayUtils.asUnmodifiableList(selectItem1, selectItem2);

        this.criteriaContext.selectList(selectPartList); // notify context

        this.selectPartList = selectPartList;
        return (SR) this;
    }

    @Override
    public final <S extends SelectItem> SR select(List<SQLModifier> modifiers, List<S> selectPartList) {
        return this.select(Collections.emptyList(), modifiers, selectPartList);
    }

    @Override
    public final <S extends SelectItem> SR select(SQLModifier modifier, Consumer<List<S>> consumer) {
        final List<S> list = new ArrayList<>();
        consumer.accept(list);
        return this.select(modifier, list);
    }

    @Override
    public final <S extends SelectItem> SR select(List<S> selectPartList) {
        final List<S> selectParts;
        selectParts = CollectionUtils.asUnmodifiableList(selectPartList);

        this.criteriaContext.selectList(selectParts);// notify context

        this.selectPartList = selectParts;
        return (SR) this;
    }

    /*################################## blow FromSpec method ##################################*/


    @Override
    public final FT from(TableMeta<?> table, String tableAlias) {
        return this.addFirstTableBlock(table, tableAlias);
    }

    @Override
    public final <T extends TableItem> FS from(Function<C, T> function, String alias) {
        return this.addFirstTablePartBlock(function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TableItem> FS from(Supplier<T> supplier, String alias) {
        return this.addFirstTablePartBlock(supplier.get(), alias);
    }


    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.LEFT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTablePart(_JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifLeftJoin(Function<C, T> function, String alias) {
        return this.ifJoinTablePart(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTablePart(_JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifJoin(Function<C, T> function, String alias) {
        return this.ifJoinTablePart(_JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTablePart(_JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifRightJoin(Function<C, T> function, String alias) {
        return this.ifJoinTablePart(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT crossJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.CROSS_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS crossJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS crossJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.CROSS_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifCrossJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTablePart(_JoinType.CROSS_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifCrossJoin(Function<C, T> function, String alias) {
        return this.ifJoinTablePart(_JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.FULL_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifJoinTable(predicate, _JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Supplier<T> supplier, String alias) {
        return this.ifJoinTablePart(_JoinType.FULL_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JS ifFullJoin(Function<C, T> function, String alias) {
        return this.ifJoinTablePart(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final WR where(final List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
            list.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (WR) this;
    }

    @Override
    public final WR where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public final WR where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public final WR where(Consumer<List<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list);
        return this.where(list);
    }

    @Override
    public final AR where(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (AR) this;
    }

    @Override
    public final AR and(IPredicate predicate) {
        this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (AR) this;
    }

    @Override
    public final AR and(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        assert predicate != null;
        this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (AR) this;
    }

    @Override
    public final AR and(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        assert predicate != null;
        this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (AR) this;
    }

    @Override
    public final AR ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (AR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem) {
        this.groupByList = Collections.singletonList((ArmySortItem) sortItem);
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2) {
        this.groupByList = ArrayUtils.asUnmodifiableList((ArmySortItem) sortItem1, (ArmySortItem) sortItem2);
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.groupByList = ArrayUtils.asUnmodifiableList((ArmySortItem) sortItem1, (ArmySortItem) sortItem2, (ArmySortItem) sortItem3);
        return (GR) this;
    }

    @Override
    public final GR groupBy(List<SortItem> sortItemList) {
        final int size = sortItemList.size();
        switch (size) {
            case 0:
                throw new CriteriaException("sortItemList must be not empty.");
            case 1:
                this.groupByList = Collections.singletonList((ArmySortItem) sortItemList.get(0));
                break;
            default: {
                final List<ArmySortItem> tempList = new ArrayList<>(size);
                for (SortItem sortItem : sortItemList) {
                    tempList.add((ArmySortItem) sortItem);
                }
                this.groupByList = Collections.unmodifiableList(tempList);
            }
        }
        return (GR) this;
    }

    @Override
    public final GR groupBy(Function<C, List<SortItem>> function) {
        return this.groupBy(function.apply(this.criteria));
    }

    @Override
    public final GR groupBy(Supplier<List<SortItem>> supplier) {
        return this.groupBy(supplier.get());
    }

    @Override
    public final GR groupBy(Consumer<List<SortItem>> consumer) {
        final List<SortItem> list = new ArrayList<>();
        consumer.accept(list);
        return this.groupBy(list);
    }

    @Override
    public final GR ifGroupBy(@Nullable SortItem sortItem) {
        if (sortItem != null) {
            this.groupByList = Collections.singletonList((ArmySortItem) sortItem);
        }
        return (GR) this;
    }

    @Override
    public final GR ifGroupBy(Supplier<List<SortItem>> supplier) {
        final List<SortItem> list;
        list = supplier.get();
        if (!CollectionUtils.isEmpty(list)) {
            this.groupBy(list);
        }
        return (GR) this;
    }

    @Override
    public final GR ifGroupBy(Function<C, List<SortItem>> function) {
        final List<SortItem> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.groupBy(list);
        }
        return (GR) this;
    }

    @Override
    public final HR having(final List<IPredicate> predicateList) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final int size = predicateList.size();
            switch (size) {
                case 0:
                    throw new CriteriaException("having predicate list must not empty.");
                case 1:
                    this.predicateList = Collections.singletonList((OperationPredicate) predicateList.get(0));
                    break;
                default: {
                    final List<_Predicate> list = new ArrayList<>(size);
                    for (IPredicate predicate : predicateList) {
                        list.add((OperationPredicate) predicate);
                    }
                    this.havingList = Collections.unmodifiableList(list);
                }
            }
        }
        return (HR) this;
    }

    @Override
    public final HR having(IPredicate predicate) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(IPredicate predicate1, IPredicate predicate2) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = ArrayUtils.asUnmodifiableList((OperationPredicate) predicate1
                    , (OperationPredicate) predicate2);
        }
        return (HR) this;
    }

    @Override
    public final HR having(Supplier<List<IPredicate>> supplier) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            this.having(supplier.get());
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<C, List<IPredicate>> function) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            this.having(function.apply(this.criteria));
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(@Nullable IPredicate predicate) {
        if (predicate != null && !CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Supplier<List<IPredicate>> supplier) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list;
            list = supplier.get();
            if (!CollectionUtils.isEmpty(list)) {
                this.having(list);
            }
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Function<C, List<IPredicate>> function) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list;
            list = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(list)) {
                this.having(list);
            }
        }
        return (HR) this;
    }

    /*################################## blow _Query method ##################################*/

    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }

    @Override
    public final List<? extends SelectItem> selectPartList() {
        final List<? extends SelectItem> selectItemList = this.selectPartList;
        assert selectItemList != null;
        return selectItemList;
    }

    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        prepared();
        return this.tableBlockList;
    }

    @Override
    public final List<_Predicate> predicateList() {
        prepared();
        return this.predicateList;
    }

    @Override
    public final List<? extends SortItem> groupPartList() {
        return this.groupByList;
    }

    @Override
    public final List<_Predicate> havingList() {
        return this.havingList;
    }


    @Override
    protected final Q internalAsQuery(final boolean justAsQuery) {
        this.tableBlockList = this.criteriaContext.clear();
        if (this instanceof SubQuery || this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }

        // hint list
        final List<Hint> hintList = this.hintList;
        if (CollectionUtils.isEmpty(hintList)) {
            this.hintList = Collections.emptyList();
        } else {
            this.hintList = Collections.unmodifiableList(hintList);
        }

        // modifier list
        if (CollectionUtils.isEmpty(this.modifierList)) {
            this.modifierList = Collections.emptyList();
        }
        // selection list
        final List<? extends SelectItem> selectPartList = this.selectPartList;
        if (CollectionUtils.isEmpty(selectPartList)) {
            throw _Exceptions.selectListIsEmpty();
        }
        if (this instanceof ColumnSubQuery
                && (selectPartList.size() != 1 || !(selectPartList.get(0) instanceof Selection))) {
            throw _Exceptions.columnSubQuerySelectionError();
        }

        // group by and having
        final List<? extends SortItem> groupByList = this.groupByList;
        if (CollectionUtils.isEmpty(groupByList)) {
            this.groupByList = Collections.emptyList();
            this.hintList = Collections.emptyList();
        } else if (CollectionUtils.isEmpty(this.havingList)) {
            this.havingList = Collections.emptyList();
        }
        this.noActionTableBlock = null;
        this.noActionTablePartBlock = null;
        return this.onAsQuery(justAsQuery);
    }


    @Override
    final void internalClear() {
        this.hintList = null;
        this.modifierList = null;
        this.selectPartList = null;
        this.tableBlockList = null;

        this.predicateList = null;
        this.groupByList = null;
        this.havingList = null;
        this.noActionTableBlock = null;

        this.noActionTablePartBlock = null;
        this.onClear();
    }

    final boolean hasGroupBy() {
        return !CollectionUtils.isEmpty(this.groupByList);
    }


    abstract Q onAsQuery(boolean outer);

    abstract void onClear();


    abstract FT addFirstTableBlock(TableMeta<?> table, String tableAlias);

    abstract FS addFirstTablePartBlock(TableItem tableItem, String alias);


    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(_JoinType joinType, TableItem tableItem, String alias);

    abstract JT createNoActionTableBlock();

    abstract JS createNoActionOnBlock();

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


    final JT ifJoinTable(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table, String alias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = this.createTableBlock(joinType, table, alias);
            this.criteriaContext.onAddBlock((_TableBlock) block);
        } else {
            block = getNoActionTableBlock();
        }
        return block;
    }

    final JS ifJoinTablePart(_JoinType joinType, @Nullable TableItem tableItem, String alias) {
        final JS block;
        if (tableItem == null) {
            block = this.getNoActionOnBlock();
        } else {
            block = this.createOnBlock(joinType, tableItem, alias);
        }
        return block;
    }


    static IllegalStateException asQueryMethodError() {
        return new IllegalStateException("onAsQuery(boolean) error");
    }


}
