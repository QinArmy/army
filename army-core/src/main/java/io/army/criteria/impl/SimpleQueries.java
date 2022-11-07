package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect._Constant;
import io.army.function.ExpressionOperator;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of all simple SELECT query.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleQueries<Q extends Item, W extends Query.SelectModifier, SR, SD, FT, FS, FC, JT, JS, JC, WR, WA, GR, HR, OR, LR, LO, LF, SP>
        extends JoinableClause<FT, FS, FC, JT, JS, JC, WR, WA, OR, LR, LO, LF>
        implements Query._DynamicHintModifierSelectClause<W, SR, SD>
        , Query._SelectCommaClause<SR>
        , Statement._QueryWhereClause<WR, WA>, Query._GroupByClause<GR>
        , Query._HavingClause<HR>, Query._AsQueryClause<Q>
        , TabularItem.DerivedTableSpec, Query._QueryUnionClause<SP>
        , Query._QueryIntersectClause<SP>, Query._QueryExceptClause<SP>
        , Query._QueryMinusClause<SP>, Query
        , _Query {


    private List<Hint> hintList;

    private List<? extends Query.SelectModifier> modifierList;

    private List<SelectItem> selectItemList;

    private List<_TableBlock> tableBlockList;


    private List<ArmySortItem> groupByList;

    private List<_Predicate> havingList;

    private Boolean prepared;


    SimpleQueries(CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
    }


    @Override
    public final SR select(FieldMeta<?> field) {
        this.context.onAddSelectItem(field);
        return (SR) this;
    }

    @Override
    public final SR select(FieldMeta<?> field1, FieldMeta<?> field2) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2);
        return (SR) this;
    }

    @Override
    public final SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3);
        return (SR) this;
    }

    @Override
    public final SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3)
                .onAddSelectItem(field4);
        return (SR) this;
    }

    @Override
    public final SR select(Expression exp, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        this.context.onAddSelectItem(ArmySelections.forExp(exp, alias));
        return (SR) this;
    }

    @Override
    public final SR select(Expression exp1, SQLs.WordAs as1, String alias1
            , Expression exp2, SQLs.WordAs as2, String alias2) {
        assert as1 == SQLs.AS && as2 == SQLs.AS;
        this.context.onAddSelectItem(ArmySelections.forExp(exp1, alias1))
                .onAddSelectItem(ArmySelections.forExp(exp2, alias2));
        return (SR) this;
    }

    @Override
    public final SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias) {
        assert period == SQLs.PERIOD;
        this.context.onAddSelectItem(ArmySelections.forExp(this.context.refThis(derivedAlias, fieldAlias), fieldAlias));
        return (SR) this;
    }

    @Override
    public final SR select(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
            , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2) {
        assert period1 == SQLs.PERIOD && period2 == SQLs.PERIOD;
        final CriteriaContext context = this.context;
        this.context.onAddSelectItem(ArmySelections.forExp(context.refThis(derivedAlias1, fieldAlias1), fieldAlias1))
                .onAddSelectItem(ArmySelections.forExp(context.refThis(derivedAlias2, fieldAlias2), fieldAlias2));
        return (SR) this;
    }

    @Override
    public final SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        assert period == SQLs.PERIOD;
        this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
        return (SR) this;
    }

    @Override
    public final <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
            , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        assert period1 == SQLs.PERIOD && period2 == SQLs.PERIOD;
        if (parent != child.parentMeta()) {
            String m = String.format("%s isn't child of %s.", child, parent);
            throw ContextStack.criteriaError(this.context, m);
        }
        this.context.onAddSelectItem(SelectionGroups.childGroup(child, childAlias, parenAlias));
        return (SR) this;
    }

    @Override
    public final SR select(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field) {
        assert period == SQLs.PERIOD;
        final CriteriaContext context = this.context;
        context.onAddSelectItem(context.qualifiedField(tableAlias, field));
        return (SR) this;
    }

    @Override
    public final SR select(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
            , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2) {
        assert period1 == SQLs.PERIOD && period2 == SQLs.PERIOD;
        final CriteriaContext context = this.context;
        context.onAddSelectItem(context.qualifiedField(tableAlias1, field1))
                .onAddSelectItem(context.qualifiedField(tableAlias2, field2));
        return (SR) this;
    }

    @Override
    public final _AsClause<SR> select(Supplier<Expression> supplier) {
        return this.onSelectExpression(supplier.get());
    }

    @Override
    public final SR select(Supplier<Expression> funcRef, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        this.context.onAddSelectItem(ArmySelections.forExp(funcRef.get(), alias));
        return (SR) this;
    }

    @Override
    public final SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias
            , SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR select(Function<Expression, Expression> funcRef, FieldMeta<?> field
            , SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        this.context.onAddSelectItem(ArmySelections.forExp(funcRef.apply(field), alias));
        return (SR) this;
    }

    @Override
    public final SR select(Function<Expression, Expression> funcRef, String tableAlias
            , SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR select(Function<Expression, Expression> funcRef, String tableAlias
            , SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR select(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
            , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2) {
        return null;
    }

    @Override
    public final <I extends Item> I select(Function<Function<Expression, _AsClause<SR>>, I> sqlFunc) {
        return null;
    }

    @Override
    public final <I extends Item> I select(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc, Expression expression) {
        return null;
    }

    @Override
    public final <E extends RightOperand> _AsClause<SR> select(Function<E, Expression> expOperator, Supplier<E> supplier) {
        return null;
    }

    @Override
    public final SR select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
            , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final <T> SR select(ExpressionOperator<Expression, T, Expression> expOperator
            , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final <T> SR select(ExpressionOperator<Expression, T, Expression> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR select(ExpressionOperator<Expression, Object, Expression> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
            , String keyName, SQLs.WordAs as, String alias) {
        return null;
    }


    @Override
    public final SD select(SQLs.SymbolStar star) {
        return null;
    }

    @Override
    public final SD select(W modifier, SQLs.SymbolStar star) {
        return null;
    }

    @Override
    public final SD select(Supplier<List<Hint>> hints, List<W> modifiers, SQLs.SymbolStar star) {
        return null;
    }

    @Override
    public final SD selects(Consumer<Selections> consumer) {
        return null;
    }

    @Override
    public final SD selects(W modifier, Consumer<Selections> consumer) {
        return null;
    }


    @Override
    public final SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Selections> consumer) {
        return null;
    }

    @Override
    public final SR comma(FieldMeta<?> field) {
        return null;
    }

    @Override
    public final SR comma(FieldMeta<?> field1, FieldMeta<?> field2) {
        return null;
    }

    @Override
    public final SR comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3) {
        return null;
    }

    @Override
    public final SR comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4) {
        return null;
    }

    @Override
    public final SR comma(Expression exp, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR comma(Expression exp1, SQLs.WordAs as1, String alias1
            , Expression exp2, SQLs.WordAs as2, String alias2) {
        return null;
    }

    @Override
    public final SR comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias) {
        return null;
    }

    @Override
    public final SR comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias
            , SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR comma(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
            , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2) {
        return null;
    }

    @Override
    public final SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        return null;
    }

    @Override
    public <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
            , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        return null;
    }

    @Override
    public final SR comma(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field) {
        return null;
    }

    @Override
    public SR comma(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
            , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2) {
        return null;
    }

    @Override
    public final _AsClause<SR> comma(Supplier<Expression> supplier) {
        return null;
    }

    @Override
    public final SR comma(Supplier<Expression> funcRef, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR comma(Function<Expression, Expression> funcRef, FieldMeta<?> field, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR comma(Function<Expression, Expression> funcRef, String tableAlias
            , SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR comma(Function<Expression, Expression> funcRef, String tableAlias
            , SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR comma(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
            , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2) {
        return null;
    }

    @Override
    public final <I extends Item> I comma(Function<Function<Expression, _AsClause<SR>>, I> sqlFunc) {
        return null;
    }

    @Override
    public final <I extends Item> I comma(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc, Expression expression) {
        return null;
    }

    @Override
    public final <E extends RightOperand> _AsClause<SR> comma(Function<E, Expression> expOperator, Supplier<E> supplier) {
        return null;
    }

    @Override
    public final SR comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
            , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final <T> SR comma(ExpressionOperator<Expression, T, Expression> expOperator
            , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public <T> SR comma(ExpressionOperator<Expression, T, Expression> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias) {
        return null;
    }

    @Override
    public final SR comma(ExpressionOperator<Expression, Object, Expression> expOperator
            , BiFunction<Expression, Object, Expression> operator
            , Function<String, ?> function, String keyName, SQLs.WordAs as, String alias) {
        return null;
    }

    /*################################## blow FromSpec method ##################################*/


    @Override
    public final WR ifWhere(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        return (WR) this;
    }


    @Override
    public final GR groupBy(SortItem sortItem) {
        this.groupByList = Collections.singletonList((ArmySortItem) sortItem);
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2) {
        this.groupByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.groupByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        return (GR) this;
    }


    @Override
    public final GR groupBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addGroupByItem);
        return this.endGroupBy(true);
    }


    @Override
    public final GR ifGroupBy(Consumer<Consumer<SortItem>> consumer) {
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
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand) {
        if (this.groupByList != null) {
            this.having(operator.apply(firstOperand.get(), secondOperand.get()));
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey) {
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
        return this.onUnion(UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.onUnion(UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.onUnion(UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.onUnion(UnionType.INTERSECT);
    }


    @Override
    public final SP intersectAll() {
        return this.onUnion(UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.onUnion(UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.onUnion(UnionType.EXCEPT);
    }


    @Override
    public final SP exceptAll() {
        return this.onUnion(UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.onUnion(UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.onUnion(UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.onUnion(UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.onUnion(UnionType.MINUS_DISTINCT);
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
        return this.context.selectionSize();
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        final List<? extends SelectItem> list = this.selectItemList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<_TableBlock> tableBlockList() {
        final List<_TableBlock> list = this.tableBlockList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final List<? extends SortItem> groupByList() {
        final List<? extends SortItem> list = this.groupByList;
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
        this.endQueryStatement();
        return this.onAsQuery();
    }


    @Override
    public final void clear() {
        this.hintList = null;
        this.modifierList = null;
        this.selectItemList = null;
        this.tableBlockList = null;

        this.groupByList = null;
        this.havingList = null;
        this.onClear();
    }

    @Override
    public final Selection selection(final String derivedAlias) {
        if (!(this instanceof SubQuery)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.context.selection(derivedAlias);
    }

    abstract SP createQueryUnion(UnionType unionType);

    abstract void onEndQuery();

    abstract Q onAsQuery();

    abstract void onClear();

    abstract List<W> asModifierList(@Nullable List<W> modifiers);

    abstract List<Hint> asHintList(@Nullable List<Hint> hints);


    private SP onUnion(UnionType unionType) {
        this.endQueryStatement();
        return this.createQueryUnion(unionType);
    }


    private void endQueryStatement() {
        _Assert.nonPrepared(this.prepared);
        // hint list
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        // modifier list
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        final CriteriaContext context = this.context;
        // selection list
        this.selectItemList = context.endSelectClause();

        this.endWhereClause();

        // group by and having
        if (this.groupByList == null) {
            this.groupByList = Collections.emptyList();
            this.havingList = Collections.emptyList();
        } else if (this.havingList == null) {
            this.havingList = Collections.emptyList();
        }

        this.endOrderByClause();

        this.onEndQuery();

        this.tableBlockList = context.endContext();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;

    }

    private _AsClause<SR> onSelectExpression(final Expression expression) {
        final _AsClause<SR> asClause;
        asClause = alias -> {
            this.context.onAddSelectItem(ArmySelections.forExp(expression, alias));
            return (SR) this;
        };
        return asClause;
    }

    private void addGroupByItem(final @Nullable SortItem sortItem) {
        if (sortItem == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            itemList = new ArrayList<>();
            this.groupByList = itemList;
        } else if (!(itemList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        itemList.add((ArmySortItem) sortItem);
    }

    private GR endGroupBy(final boolean required) {
        final List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            if (required) {
                throw ContextStack.criteriaError(this.context, "group by clause is empty");
            }
            //null,no-op
        } else if (itemList instanceof ArrayList) {
            this.groupByList = _CollectionUtils.unmodifiableList(itemList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (GR) this;
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
            this.havingList = _CollectionUtils.unmodifiableList(predicateList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }


    static abstract class WithCteSimpleQueries<Q extends Item, B extends CteBuilderSpec, WE, W extends Query.SelectModifier, SR, SD, FT, FS, FC, JT, JS, JC, WR, WA, GR, HR, OR, LR, LO, LF, SP>
            extends SimpleQueries<Q, W, SR, SD, FT, FS, FC, JT, JS, JC, WR, WA, GR, HR, OR, LR, LO, LF, SP>
            implements DialectStatement._DynamicWithClause<B, WE>
            , _Statement._WithClauseSpec
            , Query._WithSelectDispatcher<B, WE, W, SR, SD> {

        private boolean recursive;

        private List<_Cte> cteList;

        WithCteSimpleQueries(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
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
            return this.endWithClause(builder, true);
        }

        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, true);
        }

        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }

        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }


        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            final List<_Cte> list = this.cteList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }


        abstract B createCteBuilder(boolean recursive);


        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(true);
            return (WE) this;
        }

        private WE endWithClause(final B builder, final boolean required) {
            this.recursive = builder.isRecursive();
            this.cteList = this.context.endWithClause(required);
            return (WE) this;
        }

    }//WithCteSimpleQueries


    enum LockWaitOption implements SQLWords {

        NOWAIT(" NOWAIT"),
        SKIP_LOCKED(" SKIP LOCKED");

        private final String spaceWords;

        LockWaitOption(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(LockWaitOption.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//LockWaitOption


    static abstract class SelectClauseDispatcher<W extends Query.SelectModifier, SR, SD>
            implements Query._DynamicHintModifierSelectClause<W, SR, SD> {

        SelectClauseDispatcher() {
        }


        @Override
        public final SR select(FieldMeta<?> field) {
            return this.createSelectClause()
                    .select(field);
        }

        @Override
        public final SR select(FieldMeta<?> field1, FieldMeta<?> field2) {
            return this.createSelectClause()
                    .select(field1, field2);
        }

        @Override
        public final SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3) {
            return this.createSelectClause()
                    .select(field1, field2, field3);
        }

        @Override
        public final SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4) {
            return this.createSelectClause()
                    .select(field1, field2, field3, field4);
        }

        @Override
        public final SR select(Expression exp, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(exp, as, alias);
        }

        @Override
        public final SR select(Expression exp1, SQLs.WordAs as1, String alias1
                , Expression exp2, SQLs.WordAs as2, String alias2) {
            return this.createSelectClause()
                    .select(exp1, as1, alias1, exp2, as2, alias2);
        }

        @Override
        public final SD select(SQLs.SymbolStar star) {
            return this.createSelectClause()
                    .select(star);
        }

        @Override
        public final SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias) {
            return this.createSelectClause()
                    .select(derivedAlias, period, fieldAlias);
        }

        @Override
        public final SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias
                , SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(derivedAlias, period, fieldAlias, as, alias);
        }

        @Override
        public final SR select(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
                , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2) {
            return this.createSelectClause()
                    .select(derivedAlias1, period1, fieldAlias1, derivedAlias2, period2, fieldAlias2);
        }

        @Override
        public final SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            return this.createSelectClause()
                    .select(tableAlias, period, table);
        }

        @Override
        public final <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
                , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
            return this.createSelectClause()
                    .select(parenAlias, period1, parent, childAlias, period2, child);
        }

        @Override
        public final SR select(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field) {
            return this.createSelectClause()
                    .select(tableAlias, period, field);
        }

        @Override
        public final SR select(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
                , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2) {
            return this.createSelectClause()
                    .select(tableAlias1, period1, field1, tableAlias2, period2, field2);
        }

        @Override
        public final _AsClause<SR> select(Supplier<Expression> supplier) {
            return this.createSelectClause()
                    .select(supplier);
        }

        @Override
        public final SR select(Supplier<Expression> funcRef, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(funcRef, as, alias);
        }

        @Override
        public final SR select(Function<Expression, Expression> funcRef, FieldMeta<?> field
                , SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(funcRef, field, as, alias);
        }

        @Override
        public final SR select(Function<Expression, Expression> funcRef, String tableAlias
                , SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(funcRef, tableAlias, period, field, as, alias);
        }

        @Override
        public final SR select(Function<Expression, Expression> funcRef, String tableAlias
                , SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(funcRef, tableAlias, period, fieldAlias, as, alias);
        }

        @Override
        public final SR select(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
                , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2) {
            return this.createSelectClause()
                    .select(funcRef1, as1, alias1, funcRef2, as2, alias2);
        }

        @Override
        public final <I extends Item> I select(Function<Function<Expression, _AsClause<SR>>, I> sqlFunc) {
            return this.createSelectClause()
                    .select(sqlFunc);
        }

        @Override
        public final <I extends Item> I select(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc
                , Expression expression) {
            return this.createSelectClause()
                    .select(sqlFunc, expression);
        }

        @Override
        public final <E extends RightOperand> _AsClause<SR> select(Function<E, Expression> expOperator
                , Supplier<E> supplier) {
            return this.createSelectClause()
                    .select(expOperator, supplier);
        }

        @Override
        public final SR select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
                , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(fieldOperator, namedOperator, as, alias);
        }

        @Override
        public final <T> SR select(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(expOperator, operator, operand, as, alias);
        }

        @Override
        public final <T> SR select(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(expOperator, operator, getter, as, alias);
        }

        @Override
        public final SR select(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String keyName, SQLs.WordAs as, String alias) {
            return this.createSelectClause()
                    .select(expOperator, operator, function, keyName, as, alias);
        }

        @Override
        public final SD select(W modifier, StandardSyntax.SymbolStar star) {
            return this.createSelectClause()
                    .select(modifier, star);
        }

        @Override
        public final SD select(Supplier<List<Hint>> hints, List<W> modifiers, StandardSyntax.SymbolStar star) {
            return this.createSelectClause()
                    .select(hints, modifiers, star);
        }

        @Override
        public final SD selects(Consumer<Selections> consumer) {
            return this.createSelectClause()
                    .selects(consumer);
        }

        @Override
        public final SD selects(W modifier, Consumer<Selections> consumer) {
            return this.createSelectClause()
                    .selects(modifier, consumer);
        }

        @Override
        public final SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Selections> consumer) {
            return this.createSelectClause()
                    .selects(hints, modifiers, consumer);
        }


        abstract Query._DynamicHintModifierSelectClause<W, SR, SD> createSelectClause();


    }//SelectClauseDispatcher


    static abstract class WithBuilderSelectClauseDispatcher<B extends CteBuilderSpec, WE, W extends Query.SelectModifier, SR, SD>
            extends SelectClauseDispatcher<W, SR, SD>
            implements DialectStatement._DynamicWithClause<B, WE>
            , _WithClauseSpec {

        final CriteriaContext outerContext;

        private CriteriaContext withClauseContext;

        private boolean recursive;

        private List<_Cte> cteList;


        WithBuilderSelectClauseDispatcher(@Nullable CriteriaContext outerContext) {
            this.outerContext = outerContext;
        }


        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(true);
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

        abstract B createCteBuilder(boolean recursive, CriteriaContext withClauseContext);

        abstract _DynamicHintModifierSelectClause<W, SR, SD> onSelectClause(@Nullable _WithClauseSpec spec);


        @Override
        final _DynamicHintModifierSelectClause<W, SR, SD> createSelectClause() {
            return this.onSelectClause(this.cteList == null ? null : this);
        }

        @Nullable
        final _WithClauseSpec getWithClause() {
            return this.cteList == null ? null : this;
        }

        final void resetWithClause() {
            if (this.withClauseContext != null) {
                throw new IllegalStateException("withClauseContext non-null");
            }
            this.cteList = null;
            this.recursive = false;
        }

        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            final CriteriaContext withClauseContext = this.withClauseContext;
            assert withClauseContext != null;
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(withClauseContext);
            }
            this.recursive = builder.isRecursive();
            this.cteList = withClauseContext.endWithClause(builder.isRecursive(), required);
            ContextStack.pop(withClauseContext);
            withClauseContext.endContext();
            this.withClauseContext = null;
            return (WE) this;
        }

        private B innerCreateCteBuilder(boolean recursive) {
            CriteriaContext withClauseContext = this.withClauseContext;
            if (withClauseContext != null) {
                throw ContextStack.castCriteriaApi(withClauseContext);
            }
            withClauseContext = CriteriaContexts.withClauseContext(this.outerContext);
            ContextStack.push(withClauseContext);
            this.withClauseContext = withClauseContext;
            return this.createCteBuilder(recursive, withClauseContext);
        }


    }//WithBuilderSelectClauseDispatcher

    static abstract class WithSelectClauseDispatcher<B extends CteBuilderSpec, WE, W extends Query.SelectModifier, SR, SD>
            extends SelectClauseDispatcher<W, SR, SD>
            implements Query._WithSelectDispatcher<B, WE, W, SR, SD> {

        @Override
        public final WE with(Consumer<B> consumer) {
            return this.createSelectClause().with(consumer);
        }

        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            return this.createSelectClause().withRecursive(consumer);
        }

        @Override
        public final WE ifWith(Consumer<B> consumer) {
            return this.createSelectClause().ifWith(consumer);
        }

        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            return this.createSelectClause().ifWithRecursive(consumer);
        }

        @Override
        abstract _WithSelectDispatcher<B, WE, W, SR, SD> createSelectClause();


    }//WithSelectClauseDispatcher


    static abstract class ComplexSelectCommand<W extends Query.SelectModifier, SR, SD, RR>
            extends SelectClauseDispatcher<W, SR, SD>
            implements Statement._LeftParenStringQuadraOptionalSpec<RR>
            , _RightParenClause<RR> {

        final CriteriaContext context;

        private Statement._LeftParenStringQuadraOptionalSpec<RR> quadraClause;

        ComplexSelectCommand(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public final RR rightParen() {
            return (RR) this;
        }

        @Override
        public final _RightParenClause<RR> leftParen(String string) {
            this.columnAliasClauseEnd(Collections.singletonList(string));
            return this;
        }

        @Override
        public final _CommaStringDualSpec<RR> leftParen(String string1, String string2) {
            return this.stringQuadraClause()
                    .leftParen(string1, string2);
        }

        @Override
        public final _CommaStringQuadraSpec<RR> leftParen(String string1, String string2, String string3, String string4) {
            return this.stringQuadraClause()
                    .leftParen(string1, string2, string3, string4);
        }

        @Override
        public final _RightParenClause<RR> leftParen(Consumer<Consumer<String>> consumer) {
            return this.stringQuadraClause()
                    .leftParen(consumer);
        }

        @Override
        public final _RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer) {
            return this.stringQuadraClause()
                    .leftParenIf(consumer);
        }


        abstract RR columnAliasClauseEnd(List<String> list);


        private Statement._LeftParenStringQuadraOptionalSpec<RR> stringQuadraClause() {
            Statement._LeftParenStringQuadraOptionalSpec<RR> clause = this.quadraClause;
            if (clause == null) {
                clause = CriteriaSupports.stringQuadra(this.context, this::columnAliasClauseEnd);
                this.quadraClause = clause;
            }
            return clause;
        }

    }//ComplexSelectCommand


    static final class UnionSubQuery extends UnionSubRowSet implements SubQuery {

        UnionSubQuery(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSubQuery

    static final class UnionSelect extends UnionRowSet implements Select {


        UnionSelect(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSelect


    private static final class SelectionsImpl implements Selections, _SelectCommaSpec {

        private final _SelectClause<?> clause;

        private SelectionsImpl(_SelectClause<?> clause) {
            this.clause = clause;
        }


        @Override
        public _SelectCommaSpec selection(FieldMeta<?> field) {
            this.clause.select(field);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2) {
            this.clause.select(field1, field2);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3) {
            this.clause.select(field1, field2, field3);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3
                , FieldMeta<?> field4) {
            this.clause.select(field1, field2, field3, field4);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(Expression exp, SQLs.WordAs as, String alias) {
            this.clause.select(exp, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(Expression exp1, SQLs.WordAs as1, String alias1
                , Expression exp2, SQLs.WordAs as2, String alias2) {
            this.clause.select(exp1, as1, alias1, exp2, as2, alias2);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias) {
            this.clause.select(derivedAlias, period, fieldAlias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias
                , SQLs.WordAs as, String alias) {
            this.clause.select(derivedAlias, period, fieldAlias, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
                , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2) {
            this.clause.select(derivedAlias1, period1, fieldAlias1, derivedAlias2, period2, fieldAlias2);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            this.clause.select(tableAlias, period, table);
            return this;
        }

        @Override
        public <P> _SelectCommaSpec selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
                , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
            this.clause.select(parenAlias, period1, parent, childAlias, period2, child);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field) {
            this.clause.select(tableAlias, period, field);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
                , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2) {
            this.clause.select(tableAlias1, period1, field1, tableAlias2, period2, field2);
            return this;
        }

        @Override
        public _AsClause<_SelectCommaSpec> selection(Supplier<Expression> supplier) {
            return this.onSelectExpression(supplier.get());
        }

        @Override
        public _SelectCommaSpec selection(Supplier<Expression> funcRef, SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(Function<Expression, Expression> funcRef, FieldMeta<?> field
                , SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, field, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(Function<Expression, Expression> funcRef, String tableAlias
                , SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, tableAlias, period, field, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(Function<Expression, Expression> funcRef, String tableAlias
                , SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, tableAlias, period, fieldAlias, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
                , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2) {
            this.clause.select(funcRef1, as1, alias1, funcRef2, as2, alias2);
            return this;
        }

        @Override
        public <I extends Item> I selection(Function<Function<Expression, _AsClause<_SelectCommaSpec>>, I> sqlFunc) {
            return sqlFunc.apply(this::onSelectExpression);
        }

        @Override
        public <I extends Item> I selection(BiFunction<Expression, Function<Expression, _AsClause<_SelectCommaSpec>>, I> sqlFunc, Expression expression) {
            return sqlFunc.apply(expression, this::onSelectExpression);
        }

        @Override
        public <E extends RightOperand> _AsClause<_SelectCommaSpec> selection(final Function<E, Expression> expOperator
                , final Supplier<E> supplier) {
            return this.onSelectExpression(expOperator.apply(supplier.get()));
        }

        @Override
        public _SelectCommaSpec selection(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
                , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias) {
            this.clause.select(fieldOperator, namedOperator, as, alias);
            return this;
        }

        @Override
        public <T> _SelectCommaSpec selection(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias) {
            this.clause.select(expOperator, operator, operand, as, alias);
            return this;
        }

        @Override
        public <T> _SelectCommaSpec selection(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias) {
            this.clause.select(expOperator, operator, getter, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec selection(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordAs as, String alias) {
            this.clause.select(expOperator, operator, function, keyName, as, alias);
            return this;
        }


        /*-------------------below -------------------*/


        @Override
        public _SelectCommaSpec comma(FieldMeta<?> field) {
            this.clause.select(field);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(FieldMeta<?> field1, FieldMeta<?> field2) {
            this.clause.select(field1, field2);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3) {
            this.clause.select(field1, field2, field3);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3
                , FieldMeta<?> field4) {
            this.clause.select(field1, field2, field3, field4);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(Expression exp, SQLs.WordAs as, String alias) {
            this.clause.select(exp, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(Expression exp1, SQLs.WordAs as1, String alias1
                , Expression exp2, SQLs.WordAs as2, String alias2) {
            this.clause.select(exp1, as1, alias1, exp2, as2, alias2);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias) {
            this.clause.select(derivedAlias, period, fieldAlias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias
                , SQLs.WordAs as, String alias) {
            this.clause.select(derivedAlias, period, fieldAlias, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
                , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2) {
            this.clause.select(derivedAlias1, period1, fieldAlias1, derivedAlias2, period2, fieldAlias2);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            this.clause.select(tableAlias, period, table);
            return this;
        }

        @Override
        public <P> _SelectCommaSpec comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
                , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
            this.clause.select(parenAlias, period1, parent, childAlias, period2, child);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field) {
            this.clause.select(tableAlias, period, field);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
                , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2) {
            this.clause.select(tableAlias1, period1, field1, tableAlias2, period2, field2);
            return this;
        }

        @Override
        public _AsClause<_SelectCommaSpec> comma(Supplier<Expression> supplier) {
            return this.onSelectExpression(supplier.get());
        }

        @Override
        public _SelectCommaSpec comma(Supplier<Expression> funcRef, SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(Function<Expression, Expression> funcRef, FieldMeta<?> field
                , SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, field, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(Function<Expression, Expression> funcRef, String tableAlias
                , SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, tableAlias, period, field, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(Function<Expression, Expression> funcRef, String tableAlias
                , SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias) {
            this.clause.select(funcRef, tableAlias, period, fieldAlias, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
                , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2) {
            this.clause.select(funcRef1, as1, alias1, funcRef2, as2, alias2);
            return this;
        }

        @Override
        public <I extends Item> I comma(Function<Function<Expression, _AsClause<_SelectCommaSpec>>, I> sqlFunc) {
            return sqlFunc.apply(this::onSelectExpression);
        }

        @Override
        public <I extends Item> I comma(BiFunction<Expression, Function<Expression, _AsClause<_SelectCommaSpec>>, I> sqlFunc, Expression expression) {
            return sqlFunc.apply(expression, this::onSelectExpression);
        }


        @Override
        public <E extends RightOperand> _AsClause<_SelectCommaSpec> comma(final Function<E, Expression> expOperator
                , final Supplier<E> supplier) {
            return this.onSelectExpression(expOperator.apply(supplier.get()));
        }

        @Override
        public _SelectCommaSpec comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
                , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias) {
            this.clause.select(fieldOperator, namedOperator, as, alias);
            return this;
        }

        @Override
        public <T> _SelectCommaSpec comma(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias) {
            this.clause.select(expOperator, operator, operand, as, alias);
            return this;
        }

        @Override
        public <T> _SelectCommaSpec comma(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias) {
            this.clause.select(expOperator, operator, getter, as, alias);
            return this;
        }

        @Override
        public _SelectCommaSpec comma(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordAs as, String alias) {
            this.clause.select(expOperator, operator, function, keyName, as, alias);
            return this;
        }


        private _AsClause<_SelectCommaSpec> onSelectExpression(final Expression expression) {
            final _AsClause<_SelectCommaSpec> asClause;
            asClause = alias -> {
                this.clause.select(expression, SQLs.AS, alias);
                return this;
            };
            return asClause;
        }


    }//SelectionsImpl


}
