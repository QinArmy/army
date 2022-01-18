package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Query;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


abstract class SimpleQuery<C, Q extends Query, SR, FT, FS, JT, JS, WR, AR, GR, HR, OR, LR, UR, SP>
        extends PartQuery<C, Q, UR, OR, LR, SP> implements Query.SelectClause<C, SR>, Statement.JoinClause<C, JT, JS>
        , Statement.WhereClause<C, WR, AR>, Statement.WhereAndClause<C, AR>
        , Query.GroupClause<C, GR>, Query.HavingClause<C, HR>, _Query, Statement.FromClause<C, FT, FS> {


    private List<Hint> hintList;

    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList;

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<SortPart> groupByList;

    private List<_Predicate> havingList;

    private JT noActionTableBlock;

    private JS noActionTablePartBlock;


    SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final <S extends SelectPart> SR select(Function<C, List<Hint>> hints, List<SQLModifier> modifiers
            , Function<C, List<S>> function) {
        return this.select(hints.apply(this.criteria), modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectPart> SR select(List<Hint> hints, List<SQLModifier> modifiers, Function<C, List<S>> function) {
        return this.select(hints, modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectPart> SR select(List<Hint> hints, List<SQLModifier> modifiers, List<S> selectPartList) {
        if (hints.size() > 0) {
            this.hintList = new ArrayList<>(hints);
        }
        if (modifiers.size() > 0) {
            this.modifierList = new ArrayList<>(modifiers);
        }
        this.selectPartList = new ArrayList<>(selectPartList);
        return (SR) this;
    }

    @Override
    public final <S extends SelectPart> SR select(SQLModifier sqlModifier, List<S> selectPartList) {
        return this.select(Collections.emptyList(), Collections.singletonList(sqlModifier), selectPartList);
    }

    @Override
    public final <S extends SelectPart> SR select(List<SQLModifier> modifiers, Function<C, List<S>> function) {
        return this.select(Collections.emptyList(), modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectPart> SR select(List<SQLModifier> modifiers, Supplier<List<S>> supplier) {
        return this.select(Collections.emptyList(), modifiers, supplier.get());
    }

    @Override
    public final <S extends SelectPart> SR select(Function<C, List<S>> function) {
        return this.select(function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectPart> SR select(Supplier<List<S>> supplier) {
        return this.select(supplier.get());
    }

    @Override
    public final SR select(SQLModifier sqlModifier, SelectPart selectPart) {
        this.modifierList = Collections.singletonList(sqlModifier);
        this.selectPartList = Collections.singletonList(selectPart);
        return (SR) this;
    }

    @Override
    public final SR select(SelectPart selectPart) {
        this.selectPartList = Collections.singletonList(selectPart);
        return (SR) this;
    }


    @Override
    public final SR select(SelectPart selectPart1, SelectPart selectPart2) {
        this.selectPartList = Arrays.asList(selectPart1, selectPart2);
        return (SR) this;
    }


    @Override
    public final SR select(SelectPart selectPart1, SelectPart selectPart2, SelectPart selectPart3) {
        this.selectPartList = Arrays.asList(selectPart1, selectPart2, selectPart3);
        return (SR) this;
    }

    @Override
    public final <S extends SelectPart> SR select(List<SQLModifier> modifiers, List<S> selectPartList) {
        this.modifierList = new ArrayList<>(modifiers);
        this.selectPartList = new ArrayList<>(selectPartList);
        return (SR) this;
    }

    @Override
    public final <S extends SelectPart> SR select(List<S> selectPartList) {
        this.selectPartList = new ArrayList<>(selectPartList);
        return (SR) this;
    }

    /*################################## blow FromSpec method ##################################*/


    @Override
    public final FT from(TableMeta<?> table, String tableAlias) {
        final FT block;
        block = this.addTableFromBlock(table, tableAlias);
        return block;
    }

    @Override
    public final <T extends TablePart> FS from(Function<C, T> function, String alias) {
        final T tablePart;
        tablePart = function.apply(this.criteria);
        Objects.requireNonNull(tablePart);
        final FS block;
        block = this.addTablePartFromBlock(tablePart, alias);
        if (!(tablePart instanceof TableMeta)) {
            this.criteriaContext.onAddTablePart(tablePart, alias);
        }
        return block;
    }

    @Override
    public final <T extends TablePart> FS from(Supplier<T> supplier, String alias) {
        final T tablePart;
        tablePart = supplier.get();
        Objects.requireNonNull(tablePart);
        final FS block;
        block = this.addTablePartFromBlock(tablePart, alias);
        if (!(tablePart instanceof TableMeta)) {
            this.criteriaContext.onAddTablePart(tablePart, alias);
        }
        return block;
    }


    abstract FT addTableFromBlock(TableMeta<?> table, String tableAlias);

    abstract FS addTablePartFromBlock(TablePart tablePart, String alias);


    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        return this.doJoinTable(JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS leftJoin(Supplier<T> supplier, String alias) {
        return this.doJoinTablePart(JoinType.LEFT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TablePart> JS leftJoin(Function<C, T> function, String alias) {
        return this.doJoinTablePart(JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.doIfJoinTable(predicate, JoinType.LEFT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifLeftJoin(Supplier<T> supplier, String alias) {
        return this.doIfJoinTablePart(JoinType.LEFT_JOIN, supplier, alias);
    }

    @Override
    public final <T extends TablePart> JS ifLeftJoin(Function<C, T> function, String alias) {
        return this.doIfJoinTablePart(JoinType.LEFT_JOIN, function, alias);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        return this.doJoinTable(JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS join(Supplier<T> supplier, String alias) {
        return this.doJoinTablePart(JoinType.JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TablePart> JS join(Function<C, T> function, String alias) {
        return this.doJoinTablePart(JoinType.JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.doIfJoinTable(predicate, JoinType.JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifJoin(Supplier<T> supplier, String alias) {
        return this.doIfJoinTablePart(JoinType.JOIN, supplier, alias);
    }

    @Override
    public final <T extends TablePart> JS ifJoin(Function<C, T> function, String alias) {
        return this.doIfJoinTablePart(JoinType.JOIN, function, alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        return this.doJoinTable(JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS rightJoin(Supplier<T> supplier, String alias) {
        return this.doJoinTablePart(JoinType.RIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TablePart> JS rightJoin(Function<C, T> function, String alias) {
        return this.doJoinTablePart(JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.doIfJoinTable(predicate, JoinType.RIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifRightJoin(Supplier<T> supplier, String alias) {
        return this.doIfJoinTablePart(JoinType.RIGHT_JOIN, supplier, alias);
    }

    @Override
    public final <T extends TablePart> JS ifRightJoin(Function<C, T> function, String alias) {
        return this.doIfJoinTablePart(JoinType.RIGHT_JOIN, function, alias);
    }

    @Override
    public final JT crossJoin(TableMeta<?> table, String tableAlias) {
        return this.doJoinTable(JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS crossJoin(Function<C, T> function, String alias) {
        return this.doJoinTablePart(JoinType.CROSS_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS crossJoin(Supplier<T> supplier, String alias) {
        return this.doJoinTablePart(JoinType.CROSS_JOIN, supplier.get(), alias);
    }

    @Override
    public final JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.doIfJoinTable(predicate, JoinType.CROSS_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifCrossJoin(Supplier<T> supplier, String alias) {
        return this.doIfJoinTablePart(JoinType.CROSS_JOIN, supplier, alias);
    }

    @Override
    public final <T extends TablePart> JS ifCrossJoin(Function<C, T> function, String alias) {
        return this.doIfJoinTablePart(JoinType.CROSS_JOIN, function, alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        return this.doJoinTable(JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS fullJoin(Supplier<T> supplier, String alias) {
        return this.doJoinTablePart(JoinType.FULL_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TablePart> JS fullJoin(Function<C, T> function, String alias) {
        return this.doJoinTablePart(JoinType.FULL_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.doIfJoinTable(predicate, JoinType.FULL_JOIN, table, tableAlias);
    }

    @Override
    public final <T extends TablePart> JS ifFullJoin(Supplier<T> supplier, String alias) {
        return this.doIfJoinTablePart(JoinType.FULL_JOIN, supplier, alias);
    }

    @Override
    public final <T extends TablePart> JS ifFullJoin(Function<C, T> function, String alias) {
        return this.doIfJoinTablePart(JoinType.FULL_JOIN, function, alias);
    }


    @Override
    public final WR where(final List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
            list.add((_Predicate) predicate);
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
    public final AR where(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return (AR) this;
    }

    @Override
    public final AR and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return (AR) this;
    }

    @Override
    public final AR and(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        assert predicate != null;
        this.predicateList.add((_Predicate) predicate);
        return (AR) this;
    }

    @Override
    public final AR and(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        assert predicate != null;
        this.predicateList.add((_Predicate) predicate);
        return (AR) this;
    }

    @Override
    public final AR ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return (AR) this;
    }

    @Override
    public final GR groupBy(SortPart sortPart) {
        this.groupByList = Collections.singletonList(sortPart);
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortPart sortPart1, SortPart sortPart2) {
        this.groupByList = Arrays.asList(sortPart1, sortPart2);
        return (GR) this;
    }

    @Override
    public final GR groupBy(List<SortPart> sortPartList) {
        if (sortPartList.size() == 0) {
            throw new CriteriaException("group by clause is empty.");
        }
        this.groupByList = new ArrayList<>(sortPartList);
        return (GR) this;
    }

    @Override
    public final GR groupBy(Function<C, List<SortPart>> function) {
        return this.groupBy(function.apply(this.criteria));
    }

    @Override
    public final GR groupBy(Supplier<List<SortPart>> supplier) {
        return this.groupBy(supplier.get());
    }

    @Override
    public final GR ifGroupBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.groupByList = Collections.singletonList(sortPart);
        }
        return (GR) this;
    }

    @Override
    public final GR ifGroupBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> list;
        list = supplier.get();
        if (!CollectionUtils.isEmpty(list)) {
            this.groupByList = new ArrayList<>(list);
        }
        return (GR) this;
    }

    @Override
    public final GR ifGroupBy(Function<C, List<SortPart>> function) {
        final List<SortPart> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.groupByList = new ArrayList<>(list);
        }
        return (GR) this;
    }

    @Override
    public final HR having(final List<IPredicate> predicateList) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<_Predicate> list = new ArrayList<>(predicateList.size());
            for (IPredicate predicate : predicateList) {
                list.add((_Predicate) predicate);
            }
            if (list.size() == 0) {
                throw new CriteriaException("having clause is empty.");
            }
            this.havingList = list;
        }
        return (HR) this;
    }

    @Override
    public final HR having(IPredicate predicate) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = Collections.singletonList((_Predicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(IPredicate predicate1, IPredicate predicate2) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<_Predicate> list = new ArrayList<>(2);
            list.add((_Predicate) predicate1);
            list.add((_Predicate) predicate2);
            this.havingList = list;
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
            this.havingList = Collections.singletonList((_Predicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Supplier<List<IPredicate>> supplier) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list = supplier.get();
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
    public final List<SelectPart> selectPartList() {
        prepared();
        return this.selectPartList;
    }

    @Override
    public final List<_Predicate> predicateList() {
        prepared();
        return this.predicateList;
    }

    @Override
    public final List<SortPart> groupPartList() {
        return this.groupByList;
    }

    @Override
    public final List<_Predicate> havingList() {
        return this.havingList;
    }


    @Override
    protected final Q internalAsQuery(final boolean justAsQuery) {
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
        final List<SQLModifier> modifierList = this.modifierList;
        if (CollectionUtils.isEmpty(modifierList)) {
            this.modifierList = Collections.emptyList();
        } else {
            this.modifierList = CollectionUtils.unmodifiableList(modifierList);
        }
        // selection list
        final List<SelectPart> selectPartList = this.selectPartList;
        if (CollectionUtils.isEmpty(selectPartList)) {
            throw _Exceptions.selectListIsEmpty();
        }
        if (this instanceof ColumnSubQuery
                && (selectPartList.size() != 1 || !(selectPartList.get(0) instanceof Selection))) {
            throw _Exceptions.columnSubQuerySelectionError();
        }
        this.selectPartList = Collections.unmodifiableList(selectPartList);

        // group by and having
        final List<SortPart> groupByList = this.groupByList;
        if (CollectionUtils.isEmpty(groupByList)) {
            this.groupByList = Collections.emptyList();
            this.hintList = Collections.emptyList();
        } else {
            this.groupByList = CollectionUtils.unmodifiableList(groupByList);
            final List<_Predicate> havingList = this.havingList;
            if (!CollectionUtils.isEmpty(havingList)) {
                this.havingList = CollectionUtils.unmodifiableList(havingList);
            }
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


    abstract JT addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS addOnBlock(JoinType joinType, TablePart tablePart, String tableAlias);

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

    private JS getNoActionTablePartBlock() {
        JS noActionTablePartBlock = this.noActionTablePartBlock;
        if (noActionTablePartBlock == null) {
            noActionTablePartBlock = createNoActionOnBlock();
            this.noActionTablePartBlock = noActionTablePartBlock;
        }
        return noActionTablePartBlock;
    }


    final JT doIfJoinTable(Predicate<C> predicate, JoinType joinType, TableMeta<?> table, String alias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = this.doJoinTable(joinType, table, alias);
        } else {
            block = getNoActionTableBlock();
        }
        return block;
    }

    final <T extends TablePart> JS doIfJoinTablePart(JoinType joinType, Function<C, T> function, String alias) {
        final T tablePart;
        tablePart = function.apply(this.criteria);
        final JS block;
        if (tablePart == null) {
            block = this.getNoActionTablePartBlock();
        } else {
            block = this.doJoinTablePart(joinType, tablePart, alias);
        }
        return block;
    }

    final <T extends TablePart> JS doIfJoinTablePart(JoinType joinType, Supplier<T> supplier, String alias) {
        final T tablePart;
        tablePart = supplier.get();
        final JS block;
        if (tablePart == null) {
            block = this.getNoActionTablePartBlock();
        } else {
            block = this.doJoinTablePart(joinType, tablePart, alias);
        }
        return block;
    }

    final JT doJoinTable(JoinType joinType, TableMeta<?> table, String alias) {
        final JT block;
        block = this.addTableBlock(joinType, table, alias);
        this.criteriaContext.onAddTable(table, alias);
        return block;
    }

    final JS doJoinTablePart(JoinType joinType, TablePart tablePart, String alias) {
        Objects.requireNonNull(tablePart);
        final JS block;
        block = this.addOnBlock(joinType, tablePart, alias);
        this.criteriaContext.onAddTablePart(tablePart, alias);
        return block;
    }


    static IllegalStateException asQueryMethodError() {
        return new IllegalStateException("onAsQuery(boolean) error");
    }


}