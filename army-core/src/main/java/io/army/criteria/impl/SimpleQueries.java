package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;


/**
 * <p>
 * This class is base class of all simple SELECT query.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleQueries<Q extends Item, W extends Query.SelectModifier, SR extends Item, SD, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, OR, OD, LR, LO, LF, SP>
        extends JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, OR, OD, LR, LO, LF>
        implements Query._SelectDispatcher<W, SR, SD>,
        Query._StaticSelectCommaClause<SR>,
        Query._StaticSelectSpaceClause<SR>,
        Statement._QueryWhereClause<WR, WA>,
        Query._StaticGroupByClause<GR>,
        Query._GroupByCommaClause<GR>,
        Query._DynamicGroupByClause<GD>,
        Query._HavingClause<HR>,
        Query._AsQueryClause<Q>,
        RowSet._StaticUnionClause<SP>,
        RowSet._StaticIntersectClause<SP>,
        RowSet._StaticExceptClause<SP>,
        RowSet._StaticMinusClause<SP>,
        _SelectionMap,
        _Query {


    private List<Hint> hintList;

    private List<? extends Query.SelectModifier> modifierList;

    private List<_TabularBlock> tableBlockList;


    private List<ArmyGroupByItem> groupByList;

    private List<_Predicate> havingList;

    private Boolean prepared;


    SimpleQueries(CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
    }

    /*-------------------below _StaticSelectClause method-------------------*/

    @Override
    public final SR select(Selection selection) {
        this.context.onAddSelectItem(selection);
        return (SR) this;
    }

    @Override
    public final SR select(Function<String, Selection> function, String alias) {
        this.context.onAddSelectItem(function.apply(alias));
        return (SR) this;
    }

    @Override
    public final SR select(Selection selection1, Selection selection2) {
        this.context.onAddSelectItem(selection1)
                .onAddSelectItem(selection2);
        return (SR) this;
    }

    @Override
    public final SR select(Function<String, Selection> function, String alias, Selection selection) {
        this.context.onAddSelectItem(function.apply(alias))
                .onAddSelectItem(selection);
        return (SR) this;
    }

    @Override
    public final SR select(Selection selection, Function<String, Selection> function, String alias) {
        this.context.onAddSelectItem(selection)
                .onAddSelectItem(function.apply(alias));
        return (SR) this;
    }

    @Override
    public final SR select(Function<String, Selection> function1, String alias1, Function<String, Selection> function2,
                           String alias2) {
        this.context.onAddSelectItem(function1.apply(alias1))
                .onAddSelectItem(function2.apply(alias2));
        return (SR) this;
    }

    @Override
    public final SR select(SQLField field1, SQLField field2, SQLField field3) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3);
        return (SR) this;
    }

    @Override
    public final SR select(SQLField field1, SQLField field2, SQLField field3, SQLField field4) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3)
                .onAddSelectItem(field4);
        return (SR) this;
    }

    @Override
    public final SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
        return (SR) this;
    }

    @Override
    public final <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                               String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
        }
        this.context.onAddSelectItem(SelectionGroups.singleGroup(parent, parenAlias))
                .onAddSelectItem(SelectionGroups.groupWithoutId(child, childAlias));
        return (SR) this;
    }

    @Override
    public final SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
        this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
        return (SR) this;
    }


    @Override
    public final _StaticSelectSpaceClause<SR> selectAll() {
        this.modifierList = _Collections.singletonList(this.allModifier());
        return this;
    }

    @Override
    public final _StaticSelectSpaceClause<SR> selectDistinct() {
        this.modifierList = _Collections.singletonList(this.distinctModifier());
        return this;
    }

    @Override
    public final _StaticSelectSpaceClause<SR> select(List<W> modifiers) {
        this.modifierList = this.asModifierList(modifiers);
        return this;
    }


    @Override
    public final _StaticSelectSpaceClause<SR> select(Supplier<List<Hint>> hints, List<W> modifiers) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        return this;
    }


    /*-------------------below dynamic select clause method -------------------*/

    @Override
    public final SD select(Consumer<_DeferSelectSpaceClause> consumer) {
        return (SD) this;
    }

    @Override
    public final SD selects(Consumer<SelectionConsumer> consumer) {
        consumer.accept(new SelectionConsumerImpl(this.context));
        return (SD) this;
    }

    @Override
    public final SD select(W modifier, Consumer<_DeferSelectSpaceClause> consumer) {
        return (SD) this;
    }

    @Override
    public final SD selects(W modifier, Consumer<SelectionConsumer> consumer) {
        this.modifierList = this.asSingleModifier(modifier);
        consumer.accept(new SelectionConsumerImpl(this.context));
        return (SD) this;
    }

    @Override
    public final SD select(List<W> modifiers, Consumer<_DeferSelectSpaceClause> consumer) {
        return (SD) this;
    }

    @Override
    public final SD selects(List<W> modifierList, Consumer<SelectionConsumer> consumer) {
        this.modifierList = this.asModifierList(modifierList);
        consumer.accept(new SelectionConsumerImpl(this.context));
        return (SD) this;
    }

    @Override
    public final SD select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<_DeferSelectSpaceClause> consumer) {
        return (SD) this;
    }

    @Override
    public final SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<SelectionConsumer> consumer) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(new SelectionConsumerImpl(this.context));
        return (SD) this;
    }



    /*-------------------below select space clause method -------------------*/

    @Override
    public final SR space(Selection selection) {
        this.context.onAddSelectItem(selection);
        return (SR) this;
    }

    @Override
    public final SR space(Function<String, Selection> function, String alias) {
        this.context.onAddSelectItem(function.apply(alias));
        return (SR) this;
    }

    @Override
    public final SR space(Selection selection1, Selection selection2) {
        this.context.onAddSelectItem(selection1)
                .onAddSelectItem(selection2);
        return (SR) this;
    }

    @Override
    public final SR space(Function<String, Selection> function, String alias, Selection selection) {
        this.context.onAddSelectItem(function.apply(alias))
                .onAddSelectItem(selection);
        return (SR) this;
    }

    @Override
    public final SR space(Selection selection, Function<String, Selection> function, String alias) {
        this.context.onAddSelectItem(selection)
                .onAddSelectItem(function.apply(alias));
        return (SR) this;
    }

    @Override
    public final SR space(Function<String, Selection> function1, String alias1, Function<String, Selection> function2,
                          String alias2) {
        this.context.onAddSelectItem(function1.apply(alias1))
                .onAddSelectItem(function2.apply(alias2));
        return (SR) this;
    }

    @Override
    public final SR space(SQLField field1, SQLField field2, SQLField field3) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3);
        return (SR) this;
    }

    @Override
    public final SR space(SQLField field1, SQLField field2, SQLField field3, SQLField field4) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3)
                .onAddSelectItem(field4);
        return (SR) this;
    }

    @Override
    public final SR space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
        return (SR) this;
    }

    @Override
    public final <P> SR space(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                              String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        return this.select(parenAlias, period1, parent, childAlias, period2, child);
    }

    @Override
    public final SR space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
        this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
        return (SR) this;
    }

    /*-------------------below select comma -------------------*/

    @Override
    public final SR comma(Selection selection) {
        this.context.onAddSelectItem(selection);
        return (SR) this;
    }

    @Override
    public final SR comma(Function<String, Selection> function, String alias) {
        this.context.onAddSelectItem(function.apply(alias));
        return (SR) this;
    }

    @Override
    public final SR comma(Selection selection1, Selection selection2) {
        this.context.onAddSelectItem(selection1)
                .onAddSelectItem(selection2);
        return (SR) this;
    }

    @Override
    public final SR comma(Function<String, Selection> function, String alias, Selection selection) {
        this.context.onAddSelectItem(function.apply(alias))
                .onAddSelectItem(selection);
        return (SR) this;
    }

    @Override
    public final SR comma(Selection selection, Function<String, Selection> function, String alias) {
        this.context.onAddSelectItem(selection)
                .onAddSelectItem(function.apply(alias));
        return (SR) this;
    }

    @Override
    public final SR comma(Function<String, Selection> function1, String alias1, Function<String, Selection> function2,
                          String alias2) {
        this.context.onAddSelectItem(function1.apply(alias1))
                .onAddSelectItem(function2.apply(alias2));
        return (SR) this;
    }

    @Override
    public final SR comma(SQLField field1, SQLField field2, SQLField field3) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3);
        return (SR) this;
    }

    @Override
    public final SR comma(SQLField field1, SQLField field2, SQLField field3, SQLField field4) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3)
                .onAddSelectItem(field4);
        return (SR) this;
    }

    @Override
    public final SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
        return (SR) this;
    }

    @Override
    public final <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                              String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
        }
        this.context.onAddSelectItem(SelectionGroups.singleGroup(parent, parenAlias))
                .onAddSelectItem(SelectionGroups.groupWithoutId(child, childAlias));
        return (SR) this;
    }

    @Override
    public final SR comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
        this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
        return (SR) this;
    }

    /*################################## blow FromSpec method ##################################*/


    @Override
    public final WR ifWhere(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        return (WR) this;
    }


    @Override
    public final GR groupBy(GroupByItem item) {
        this.addGroupByItem(item);
        return (GR) this;
    }

    @Override
    public final GR groupBy(GroupByItem item1, GroupByItem item2) {
        this.addGroupByItem(item1);
        this.addGroupByItem(item2);
        return (GR) this;
    }

    @Override
    public final GR groupBy(GroupByItem item1, GroupByItem item2, GroupByItem item3) {
        this.addGroupByItem(item1);
        this.addGroupByItem(item2);
        this.addGroupByItem(item3);
        return (GR) this;
    }

    @Override
    public final GR groupBy(GroupByItem item1, GroupByItem item2, GroupByItem item3, GroupByItem item4) {
        this.addGroupByItem(item1);
        this.addGroupByItem(item2);
        this.addGroupByItem(item3);
        this.addGroupByItem(item4);
        return (GR) this;
    }

    @Override
    public final GR commaSpace(GroupByItem item) {
        this.addGroupByItem(item);
        return (GR) this;
    }

    @Override
    public final GR commaSpace(GroupByItem item1, GroupByItem item2) {
        this.addGroupByItem(item1);
        this.addGroupByItem(item2);
        return (GR) this;
    }

    @Override
    public final GR commaSpace(GroupByItem item1, GroupByItem item2, GroupByItem item3) {
        this.addGroupByItem(item1);
        this.addGroupByItem(item2);
        this.addGroupByItem(item3);
        return (GR) this;
    }

    @Override
    public final GR commaSpace(GroupByItem item1, GroupByItem item2, GroupByItem item3, GroupByItem item4) {
        this.addGroupByItem(item1);
        this.addGroupByItem(item2);
        this.addGroupByItem(item3);
        this.addGroupByItem(item4);
        return (GR) this;
    }

    @Override
    public final GD groupBy(Consumer<Consumer<GroupByItem>> consumer) {
        consumer.accept(this::addGroupByItem);
        return this.endGroupBy(true);
    }


    @Override
    public final GD ifGroupBy(Consumer<Consumer<GroupByItem>> consumer) {
        consumer.accept(this::addGroupByItem);
        return this.endGroupBy(false);
    }

    @Override
    public final HR having(final @Nullable IPredicate predicate) {
        if (this.groupByList != null) {
            if (predicate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(final @Nullable IPredicate predicate1, final @Nullable IPredicate predicate2) {
        if (this.groupByList != null) {
            if (predicate1 == null || predicate2 == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.havingList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) predicate1
                    , (OperationPredicate) predicate2
            );
        }
        return (HR) this;
    }

    @Override
    public final HR having(Supplier<IPredicate> supplier) {
        if (this.groupByList != null) {
            this.having(supplier.get());
        }
        return (HR) this;
    }


    @Override
    public final HR having(Function<Object, IPredicate> operator, Supplier<?> operand) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.get()));
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<Object, IPredicate> operator, Function<String, ?> operand, String operandKey) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.apply(operandKey)));
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand,
                           Supplier<?> secondOperand) {
        if (this.groupByList != null) {
            this.having(operator.apply(firstOperand.get(), secondOperand.get()));
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand,
                           String firstKey, String secondKey) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
        }
        return (HR) this;
    }

    @Override
    public final HR having(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(false);
        }
        return (HR) this;
    }


    @Override
    public final HR ifHaving(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(true);
        }
        return (HR) this;
    }

    @Override
    public final SP union() {
        return this.onUnion(_UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.onUnion(_UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.onUnion(_UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.onUnion(_UnionType.INTERSECT);
    }


    @Override
    public final SP intersectAll() {
        return this.onUnion(_UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.onUnion(_UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.onUnion(_UnionType.EXCEPT);
    }


    @Override
    public final SP exceptAll() {
        return this.onUnion(_UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.onUnion(_UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.onUnion(_UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.onUnion(_UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.onUnion(_UnionType.MINUS_DISTINCT);
    }


    /*################################## blow _Query method ##################################*/


    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<? extends SQLWords> modifierList() {
        final List<? extends SQLWords> list = this.modifierList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final int selectionSize() {
        return this.context.flatSelectItems().size();
    }

    @Override
    public final List<? extends _SelectItem> selectItemList() {
        return this.context.selectItemList();
    }

    @Override
    public final List<Selection> refAllSelection() {
        if (!(this instanceof SubQuery)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.context.flatSelectItems();
    }

    @Override
    public final Selection refSelection(final String derivedAlias) {
        if (!(this instanceof SubQuery)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.context.selection(derivedAlias);
    }

    @Override
    public final List<_TabularBlock> tableBlockList() {
        final List<_TabularBlock> list = this.tableBlockList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final List<? extends GroupByItem> groupByList() {
        final List<ArmyGroupByItem> list = this.groupByList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<_Predicate> havingList() {
        final List<_Predicate> list = this.havingList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    @Override
    public final Q asQuery() {
        this.endQueryStatement(false);
        return this.onAsQuery();
    }


    @Override
    public final void clear() {
        this.hintList = null;
        this.modifierList = null;
        this.tableBlockList = null;
        this.groupByList = null;

        this.havingList = null;
        this.onClear();
    }

    abstract SP createQueryUnion(_UnionType unionType);

    abstract void onEndQuery();

    abstract Q onAsQuery();

    abstract void onClear();

    abstract List<W> asModifierList(@Nullable List<W> modifiers);

    abstract List<Hint> asHintList(@Nullable List<Hint> hints);


    abstract boolean isErrorModifier(W modifier);

    abstract W allModifier();

    abstract W distinctModifier();


    private SP onUnion(_UnionType unionType) {
        this.endQueryStatement(false);
        return this.createQueryUnion(unionType);
    }


    private List<W> asSingleModifier(final @Nullable W modifier) {
        if (modifier == null || this.isErrorModifier(modifier)) {
            String m = String.format("%s syntax error.", modifier);
            throw ContextStack.criteriaError(this.context, m);
        }
        return Collections.singletonList(modifier);
    }


    final void endStmtBeforeCommand() {
        this.endQueryStatement(true);
    }

    final boolean hasGroupByClause() {
        final List<ArmyGroupByItem> itemList = this.groupByList;
        return itemList != null && itemList.size() > 0;
    }


    private void endQueryStatement(final boolean beforeSelect) {
        _Assert.nonPrepared(this.prepared);
        // hint list
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        // modifier list
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        this.endWhereClauseIfNeed();

        // group by and having
        if (this.groupByList == null) {
            this.groupByList = Collections.emptyList();
            this.havingList = Collections.emptyList();
        } else if (this.havingList == null) {
            this.havingList = Collections.emptyList();
        }

        this.endOrderByClauseIfNeed();

        this.endGroupBy(false);

        final CriteriaContext context = this.context;
        if (beforeSelect) {
            context.endContextBeforeCommand();
            this.tableBlockList = Collections.emptyList();
        } else {
            this.onEndQuery();
            this.tableBlockList = context.endContext();
        }
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;

    }


    private void addGroupByItem(final @Nullable GroupByItem item) {
        if (item == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<ArmyGroupByItem> itemList = this.groupByList;
        if (itemList == null) {
            itemList = _Collections.arrayList();
            this.groupByList = itemList;
        } else if (!(itemList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        itemList.add((ArmyGroupByItem) item);
    }

    private GD endGroupBy(final boolean required) {
        final List<ArmyGroupByItem> itemList = this.groupByList;
        if (itemList instanceof ArrayList) {
            this.groupByList = _Collections.unmodifiableList(itemList);
        } else if (itemList == null) {
            if (required) {
                throw ContextStack.criteriaError(this.context, "group by clause is empty");
            }
            this.groupByList = _Collections.emptyList();
        }
        return (GD) this;
    }

    private void addHavingPredicate(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<_Predicate> predicateList = this.havingList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.havingList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        predicateList.add((OperationPredicate) predicate);
    }

    private void endHaving(final boolean optional) {
        final List<_Predicate> predicateList = this.havingList;
        if (this.groupByList == null) {
            this.havingList = Collections.emptyList();
        } else if (predicateList == null) {
            if (!optional) {
                throw ContextStack.criteriaError(this.context, "having clause is empty");
            }
            this.havingList = Collections.emptyList();
        } else if (predicateList instanceof ArrayList) {
            this.havingList = _Collections.unmodifiableList(predicateList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }


    static abstract class WithCteSimpleQueries<Q extends Item, B extends CteBuilderSpec, WE extends Item, W extends Query.SelectModifier, SR extends Item, SD, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, OR, OD, LR, LO, LF, SP>
            extends SimpleQueries<Q, W, SR, SD, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, OR, OD, LR, LO, LF, SP>
            implements DialectStatement._DynamicWithClause<B, WE>,
            ArmyStmtSpec,
            Query._WithSelectDispatcher<B, WE, W, SR, SD> {

        private boolean recursive;

        private List<_Cte> cteList;

        WithCteSimpleQueries(@Nullable ArmyStmtSpec withSpec, CriteriaContext context) {
            super(context);
            if (withSpec != null) {
                this.recursive = withSpec.isRecursive();
                this.cteList = withSpec.cteList();
            }
        }

        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }

        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }

        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }

        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }


        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            List<_Cte> list = this.cteList;
            if (list == null) {
                list = Collections.emptyList();
                this.cteList = list;
            }
            return list;
        }


        abstract B createCteBuilder(boolean recursive);


        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);
            return (WE) this;
        }


        private WE endDynamicWithClause(final B builder, final boolean required) {
            ((CriteriaSupports.CteBuilder) builder).endLastCte();

            final boolean recursive;
            recursive = builder.isRecursive();
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }

    }//WithCteSimpleQueries

    static abstract class WithCteDistinctOnSimpleQueries<Q extends Item, B extends CteBuilderSpec, WE extends Item, W extends Query.SelectModifier, SR extends Item, SD extends Item, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, OR, OD, LR, LO, LF, SP>
            extends WithCteSimpleQueries<Q, B, WE, W, SR, SD, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, OR, OD, LR, LO, LF, SP>
            implements _SelectDistinctOnDispatcher<W, SR, SD>,
            _Query._DistinctOnClauseSpec {

        private List<_Expression> distinctOnExpList;

        WithCteDistinctOnSimpleQueries(@Nullable ArmyStmtSpec withSpec, CriteriaContext context) {
            super(withSpec, context);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp) {
            this.onDistinctOnExpList(Collections.singletonList((ArmyExpression) exp));
            return this;
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp1, Expression exp2) {
            this.onDistinctOnExpList(
                    ArrayUtils.asUnmodifiableList((ArmyExpression) exp1, (ArmyExpression) exp2)
            );
            return this;
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp1, Expression exp2, Expression exp3) {
            this.onDistinctOnExpList(
                    ArrayUtils.asUnmodifiableList((ArmyExpression) exp1, (ArmyExpression) exp2, (ArmyExpression) exp3)
            );
            return this;
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Consumer<Consumer<Expression>> consumer) {
            this.onDistinctOnExpList(
                    CriteriaUtils.expressionList(this.context, true, consumer)
            );
            return this;
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctIfOn(Consumer<Consumer<Expression>> consumer) {
            this.onDistinctOnExpList(
                    CriteriaUtils.expressionList(this.context, false, consumer)
            );
            return this;
        }

        @Override
        public final SD selectDistinctOn(Expression exp, Consumer<SelectionConsumer> consumer) {
            this.onDistinctOnExpList(Collections.singletonList((ArmyExpression) exp));
            return this.selects(consumer);
        }

        @Override
        public final SD selectDistinctOn(Expression exp1, Expression exp2, Consumer<SelectionConsumer> consumer) {
            this.onDistinctOnExpList(
                    ArrayUtils.asUnmodifiableList((ArmyExpression) exp1, (ArmyExpression) exp2)
            );
            return this.selects(consumer);
        }

        @Override
        public final SD selectDistinctOn(Expression exp1, Expression exp2, Expression exp3, Consumer<SelectionConsumer> consumer) {
            this.onDistinctOnExpList(
                    ArrayUtils.asUnmodifiableList((ArmyExpression) exp1, (ArmyExpression) exp2, (ArmyExpression) exp3)
            );
            return this.selects(consumer);
        }

        @Override
        public final SD selectDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            this.onDistinctOnExpList(
                    CriteriaUtils.expressionList(this.context, true, expConsumer)
            );
            return this.selects(consumer);
        }

        @Override
        public final SD selectDistinctIfOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            this.onDistinctOnExpList(
                    CriteriaUtils.expressionList(this.context, false, expConsumer)
            );
            return this.selects(consumer);
        }

        @Override
        public final List<_Expression> distinctOnExpressions() {
            List<_Expression> list = this.distinctOnExpList;
            if (list == null) {
                list = Collections.emptyList();
                this.distinctOnExpList = list;
            }
            return list;
        }


        /**
         * @param list a unmodified list
         */
        private void onDistinctOnExpList(final List<_Expression> list) {
            if (this.distinctOnExpList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.selectDistinct();
            this.distinctOnExpList = list;
        }


    }//WithCteDistinctOnSimpleQueries


    enum LockWaitOption implements SQLWords {

        NOWAIT(" NOWAIT"),
        SKIP_LOCKED(" SKIP LOCKED");

        private final String spaceWords;

        LockWaitOption(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//LockWaitOption

    static abstract class LockClauseBlock<LT, LW> implements Query._LockOfTableAliasClause<LT>,
            Query._MinLockWaitOptionClause<LW>,
            CriteriaContextSpec,
            _Query._LockBlock,
            Item {


        private List<String> tableAliasList;

        private SQLWords lockWaitOption;

        private boolean clauseEnd;

        LockClauseBlock() {
        }

        @Override
        public final LT of(String tableAlias) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = Collections.singletonList(tableAlias);
            return (LT) this;
        }

        @Override
        public final LT of(String tableAlias1, String tableAlias2) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
            return (LT) this;
        }

        @Override
        public final LT of(String tableAlias1, String tableAlias2, String tableAlias3) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2, tableAlias3);
            return (LT) this;
        }

        @Override
        public final LT of(String tableAlias1, String tableAlias2, String tableAlias3, String tableAlias4) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2, tableAlias3, tableAlias4);
            return (LT) this;
        }

        @Override
        public final LT of(String tableAlias1, String tableAlias2, String tableAlias3, String tableAlias4,
                           String tableAlias5, String... restTableAlias) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2, tableAlias3, tableAlias4,
                    tableAlias5, restTableAlias);
            return (LT) this;
        }

        @Override
        public final LT of(Consumer<Consumer<String>> consumer) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = CriteriaUtils.stringList(this.getContext(), true, consumer);
            return (LT) this;
        }

        @Override
        public final LT ifOf(Consumer<Consumer<String>> consumer) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = CriteriaUtils.stringList(this.getContext(), false, consumer);
            return (LT) this;
        }


        @Override
        public final LW noWait() {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.lockWaitOption = LockWaitOption.NOWAIT;
            return (LW) this;
        }

        @Override
        public final LW skipLocked() {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
            return (LW) this;
        }

        @Override
        public final LW ifNoWait(BooleanSupplier predicate) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            } else if (predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.NOWAIT;
            } else {
                this.lockWaitOption = null;
            }
            return (LW) this;
        }

        @Override
        public final LW ifSkipLocked(BooleanSupplier predicate) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            } else if (predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
            } else {
                this.lockWaitOption = null;
            }
            return (LW) this;
        }

        @Override
        public final List<String> lockTableAliasList() {
            List<String> list = this.tableAliasList;
            if (list == null) {
                list = Collections.emptyList();
                this.tableAliasList = list;
            }
            return list;
        }

        @Override
        public final SQLWords lockWaitOption() {
            return this.lockWaitOption;
        }

        final _LockBlock endLockClause() {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.clauseEnd = true;
            return this;
        }


    }//LockClauseBlock


    static abstract class SimpleWindowAsClause<T extends Item, R extends Item>
            implements Window._WindowAsClause<T, R> {

        final CriteriaContext context;

        final String name;

        final Function<ArmyWindow, R> function;

        SimpleWindowAsClause(CriteriaContext context, String name, Function<ArmyWindow, R> function) {
            this.context = context;
            this.name = name;
            this.function = function;
        }

        @Override
        public final R as() {
            return this.function.apply(SQLWindow.namedGlobalWindow(this.context, this.name));
        }

        @Override
        public final R as(@Nullable String existingWindowName) {
            return this.function.apply(SQLWindow.namedRefWindow(this.context, this.name, existingWindowName));
        }

        @Override
        public final R as(Consumer<T> consumer) {
            return this.as(null, consumer);
        }

        @Override
        public final R as(@Nullable String existingWindowName, Consumer<T> consumer) {
            final T window;
            window = this.createNameWindow(existingWindowName);
            consumer.accept(window);
            return this.function.apply((ArmyWindow) window);
        }

        abstract T createNameWindow(@Nullable String existingWindowName);


    }//WindowAsClause


    static abstract class SelectClauseDispatcher<W extends Query.SelectModifier, SR extends Item, SD>
            implements Query._SelectDispatcher<W, SR, SD>,
            CriteriaContextSpec {

        final CriteriaContext context;

        SelectClauseDispatcher(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            this.context = CriteriaContexts.dispatcherContext(outerContext, leftContext);
            ContextStack.push(this.context);
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final SR select(Selection selection) {
            return this.createSelectClause()
                    .select(selection);
        }

        @Override
        public final SR select(Function<String, Selection> function, String alias) {
            return this.createSelectClause()
                    .select(function, alias);
        }

        @Override
        public final SR select(Selection selection1, Selection selection2) {
            return this.createSelectClause()
                    .select(selection1, selection2);
        }

        @Override
        public final SR select(Function<String, Selection> function, String alias, Selection selection) {
            return this.createSelectClause()
                    .select(function, alias, selection);
        }

        @Override
        public final SR select(Selection selection, Function<String, Selection> function, String alias) {
            return this.createSelectClause()
                    .select(selection, function, alias);
        }

        @Override
        public final SR select(Function<String, Selection> function1, String alias1,
                               Function<String, Selection> function2, String alias2) {
            return this.createSelectClause()
                    .select(function1, alias1, function2, alias2);
        }

        @Override
        public final SR select(SQLField field1, SQLField field2, SQLField field3) {
            return this.createSelectClause()
                    .select(field1, field2, field3);
        }

        @Override
        public final SR select(SQLField field1, SQLField field2, SQLField field3, SQLField field4) {
            return this.createSelectClause()
                    .select(field1, field2, field3, field4);
        }

        @Override
        public final SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            return this.createSelectClause()
                    .select(tableAlias, period, table);
        }

        @Override
        public <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                             String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
            return this.createSelectClause()
                    .select(parenAlias, period1, parent, childAlias, period2, child);
        }

        @Override
        public final SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
            return this.createSelectClause()
                    .select(derivedAlias, period, star);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectAll() {
            return this.createSelectClause()
                    .selectAll();
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinct() {
            return this.createSelectClause()
                    .selectDistinct();
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(List<W> modifiers) {
            return this.createSelectClause()
                    .select(modifiers);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(Supplier<List<Hint>> hints, List<W> modifiers) {
            return this.createSelectClause()
                    .select(hints, modifiers);
        }

        @Override
        public final SD selects(Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selects(consumer);
        }

        @Override
        public final SD selects(W modifier, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selects(modifier, consumer);
        }

        @Override
        public final SD selects(List<W> modifierList, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selects(modifierList, consumer);
        }

        @Override
        public final SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selects(hints, modifiers, consumer);
        }

        @Override
        public final String toString() {
            return super.toString();
        }

        abstract Query._SelectDispatcher<W, SR, SD> createSelectClause();

        final void endDispatcher() {
            this.context.endContext();
            ContextStack.pop(this.context);
        }


    }//SelectClauseDispatcher


    static abstract class WithBuilderSelectClauseDispatcher<B extends CteBuilderSpec, WE extends Item, W extends Query.SelectModifier, SR extends Item, SD>
            extends SelectClauseDispatcher<W, SR, SD>
            implements DialectStatement._DynamicWithClause<B, WE>,
            ArmyStmtSpec {

        private boolean recursive;

        private List<_Cte> cteList;


        WithBuilderSelectClauseDispatcher(@Nullable CriteriaContext outerContext,
                                          @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }

        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false, this.context);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true, this.context);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false, this.context);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true, this.context);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            List<_Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
                this.cteList = cteList;
            }
            return cteList;
        }

        abstract B createCteBuilder(boolean recursive, CriteriaContext context);


        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);
            return (WE) this;
        }

        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            ((CriteriaSupports.CteBuilder) builder).endLastCte();

            final boolean recursive;
            recursive = builder.isRecursive();

            this.recursive = recursive;
            this.cteList = context.endWithClause(recursive, required);
            return (WE) this;
        }


    }//WithBuilderSelectClauseDispatcher

    static abstract class WithDistinctOnSelectClauseDispatcher<B extends CteBuilderSpec, WE extends Item, W extends Query.SelectModifier, SR extends Item, SD extends Item>
            extends WithBuilderSelectClauseDispatcher<B, WE, W, SR, SD>
            implements Query._DynamicDistinctOnExpClause<SR>,
            Query._DynamicDistinctOnAndSelectsClause<SD> {

        WithDistinctOnSelectClauseDispatcher(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp) {
            return this.createSelectClause()
                    .selectDistinctOn(exp);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp1, Expression exp2) {
            return this.createSelectClause()
                    .selectDistinctOn(exp1, exp2);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp1, Expression exp2, Expression exp3) {
            return this.createSelectClause()
                    .selectDistinctOn(exp1, exp2, exp3);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Consumer<Consumer<Expression>> consumer) {
            return this.createSelectClause()
                    .selectDistinctOn(consumer);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctIfOn(Consumer<Consumer<Expression>> consumer) {
            return this.createSelectClause()
                    .selectDistinctIfOn(consumer);
        }

        @Override
        public final SD selectDistinctOn(Expression exp, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause()
                    .selectDistinctOn(exp, consumer);
        }

        @Override
        public final SD selectDistinctOn(Expression exp1, Expression exp2, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause()
                    .selectDistinctOn(exp1, exp2, consumer);
        }

        @Override
        public final SD selectDistinctOn(Expression exp1, Expression exp2, Expression exp3, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause()
                    .selectDistinctOn(exp1, exp2, exp3, consumer);
        }

        @Override
        public final SD selectDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause()
                    .selectDistinctOn(expConsumer, consumer);
        }

        @Override
        public final SD selectDistinctIfOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause()
                    .selectDistinctIfOn(expConsumer, consumer);
        }

        @Override
        abstract _SelectDistinctOnDispatcher<W, SR, SD> createSelectClause();


    }//WithDistinctOnSelectClauseDispatcher


    static final class UnionSubQuery extends UnionSubRowSet implements ArmySubQuery {

        UnionSubQuery(SubQuery left, _UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSubQuery

    static final class UnionSelect extends UnionRowSet implements ArmySelect {


        UnionSelect(Select left, _UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }

        @Override
        public List<? extends _SelectItem> selectItemList() {
            return ((_PrimaryRowSet) this.left).selectItemList();
        }


    }//UnionSelect


    private static final class SelectionConsumerImpl implements SelectionConsumer, Query._DeferSelectSpaceClause,
            Query._DeferSelectCommaSpace {

        private final CriteriaContext context;


        /**
         * @see #selects(Consumer)
         */
        private SelectionConsumerImpl(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public _DeferSelectCommaSpace space(Selection selection) {
            this.context.onAddSelectItem(selection);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Selection selection1, Selection selection2) {
            this.context.onAddSelectItem(selection1)
                    .onAddSelectItem(selection2);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Function<String, Selection> function, String alias, Selection selection) {
            this.context.onAddSelectItem(function.apply(alias))
                    .onAddSelectItem(selection);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Selection selection, Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(selection)
                    .onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Function<String, Selection> function1, String alias1,
                                            Function<String, Selection> function2, String alias2) {
            this.context.onAddSelectItem(function1.apply(alias1))
                    .onAddSelectItem(function2.apply(alias2));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(SQLField field1, SQLField field2, SQLField field3) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(SQLField field1, SQLField field2, SQLField field3, SQLField field4) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3)
                    .onAddSelectItem(field4);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
            return this;
        }

        @Override
        public <P> _DeferSelectCommaSpace space(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                                String childAlias, SQLs.SymbolPeriod period2,
                                                ComplexTableMeta<P, ?> child) {
            if (child.parentMeta() != parent) {
                throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
            }
            this.context.onAddSelectItem(SelectionGroups.singleGroup(parent, parenAlias))
                    .onAddSelectItem(SelectionGroups.groupWithoutId(child, childAlias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {

            this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Selection selection) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Function<String, Selection> function, String alias) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Selection selection1, Selection selection2) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Function<String, Selection> function, String alias, Selection selection) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Selection selection, Function<String, Selection> function, String alias) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Function<String, Selection> function1, String alias1,
                                            Function<String, Selection> function2, String alias2) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(SQLField field1, SQLField field2, SQLField field3) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(SQLField field1, SQLField field2, SQLField field3, SQLField field4) {
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            return this;
        }

        @Override
        public <P> _DeferSelectCommaSpace comma(String parenAlias, SQLs.SymbolPeriod p1, ParentTableMeta<P> parent,
                                                String childAlias, SQLs.SymbolPeriod p2, ComplexTableMeta<P, ?> child) {
            if (child.parentMeta() != parent) {
                throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
            }
            this.context.onAddSelectItem(SelectionGroups.singleGroup(parent, parenAlias))
                    .onAddSelectItem(SelectionGroups.groupWithoutId(child, childAlias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
            return this;
        }

        @Override
        public SelectionConsumer accept(Selection selection) {
            this.context.onAddSelectItem(selection);
            return this;
        }

        @Override
        public SelectionConsumer accept(Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public SelectionConsumer accept(Selection selection1, Selection selection2) {
            this.context.onAddSelectItem(selection1)
                    .onAddSelectItem(selection2);
            return this;
        }

        @Override
        public SelectionConsumer accept(Function<String, Selection> function, String alias, Selection selection) {
            this.context.onAddSelectItem(function.apply(alias))
                    .onAddSelectItem(selection);
            return this;
        }

        @Override
        public SelectionConsumer accept(Selection selection, Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(selection)
                    .onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public SelectionConsumer accept(Function<String, Selection> function1, String alias1,
                                        Function<String, Selection> function2, String alias2) {
            this.context.onAddSelectItem(function1.apply(alias1))
                    .onAddSelectItem(function2.apply(alias2));
            return this;
        }

        @Override
        public SelectionConsumer accept(SQLField field1, SQLField field2, SQLField field3) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3);
            return this;
        }

        @Override
        public SelectionConsumer accept(SQLField field1, SQLField field2, SQLField field3, SQLField field4) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3)
                    .onAddSelectItem(field4);
            return this;
        }

        @Override
        public SelectionConsumer accept(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
            return this;
        }

        @Override
        public <P> SelectionConsumer accept(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                            String childAlias, SQLs.SymbolPeriod period2,
                                            ComplexTableMeta<P, ?> child) {
            if (child.parentMeta() != parent) {
                throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
            }
            this.context.onAddSelectItem(SelectionGroups.singleGroup(parent, parenAlias))
                    .onAddSelectItem(SelectionGroups.groupWithoutId(child, childAlias));
            return this;
        }

        @Override
        public SelectionConsumer accept(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
            this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
            return this;
        }


    }//SelectionsImpl


}
