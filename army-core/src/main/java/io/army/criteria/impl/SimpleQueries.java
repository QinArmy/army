package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.function.DialectBooleanOperator;
import io.army.function.ExpressionOperator;
import io.army.function.TeFunction;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;


/**
 * <p>
 * This class is base class of all simple SELECT query.
*
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleQueries<Q extends Item, B extends CteBuilderSpec, WE extends Item, W extends Query.SelectModifier, SR extends Item, SD, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, HD, OR, OD, LR, LO, LF, SP>
        extends JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, OR, OD, LR, LO, LF>
        implements DialectStatement._DynamicWithClause<B, WE>,
        ArmyStmtSpec,
        Query._WithSelectDispatcher<B, WE, W, SR, SD>,
        Query._StaticSelectCommaClause<SR>,
        Query._StaticSelectSpaceClause<SR>,
        Statement._QueryWhereClause<WR, WA>,
        Query._StaticGroupByClause<GR>,
        Query._GroupByCommaClause<GR>,
        Query._DynamicGroupByClause<GD>,
        Query._StaticHavingClause<HR>,
        Query._HavingAndClause<HR>,
        Query._DynamicHavingClause<HD>,
        Query._AsQueryClause<Q>,
        RowSet._StaticUnionClause<SP>,
        RowSet._StaticIntersectClause<SP>,
        RowSet._StaticExceptClause<SP>,
        RowSet._StaticMinusClause<SP>,
        _SelectionMap,
        JoinableClause.SimpleQuery,
        _Query {

    private boolean recursive;

    private List<_Cte> cteList;


    private List<Hint> hintList;

    private List<? extends Query.SelectModifier> modifierList;

    private List<_TabularBlock> tableBlockList;


    private List<ArmyGroupByItem> groupByList;

    private List<_Predicate> havingList;

    private Boolean prepared;


    SimpleQueries(@Nullable ArmyStmtSpec withSpec, CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
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
    public final SR select(SqlField field1, SqlField field2, SqlField field3) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3);
        return (SR) this;
    }

    @Override
    public final SR select(SqlField field1, SqlField field2, SqlField field3, SqlField field4) {
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
    public final SD select(final Consumer<_DeferSelectSpaceClause> consumer) {
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
        return (SD) this;
    }

    @Override
    public final SD selects(final Consumer<SelectionConsumer> consumer) {
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
        return (SD) this;
    }

    @Override
    public final SD select(W modifier, final Consumer<_DeferSelectSpaceClause> consumer) {
        this.modifierList = this.asSingleModifier(modifier);
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
        return (SD) this;
    }

    @Override
    public final SD selects(W modifier, final Consumer<SelectionConsumer> consumer) {
        this.modifierList = this.asSingleModifier(modifier);
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
        return (SD) this;
    }

    @Override
    public final SD select(List<W> modifiers, final Consumer<_DeferSelectSpaceClause> consumer) {
        this.modifierList = this.asModifierList(modifiers);
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
        return (SD) this;
    }

    @Override
    public final SD selects(List<W> modifierList, final Consumer<SelectionConsumer> consumer) {
        this.modifierList = this.asModifierList(modifierList);
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
        return (SD) this;
    }

    @Override
    public final SD select(Supplier<List<Hint>> hints, List<W> modifiers, final Consumer<_DeferSelectSpaceClause> consumer) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
        return (SD) this;
    }

    @Override
    public final SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<SelectionConsumer> consumer) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
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
    public final SR space(SqlField field1, SqlField field2, SqlField field3) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3);
        return (SR) this;
    }

    @Override
    public final SR space(SqlField field1, SqlField field2, SqlField field3, SqlField field4) {
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
    public final SR comma(SqlField field1, SqlField field2, SqlField field3) {
        this.context.onAddSelectItem(field1)
                .onAddSelectItem(field2)
                .onAddSelectItem(field3);
        return (SR) this;
    }

    @Override
    public final SR comma(SqlField field1, SqlField field2, SqlField field3, SqlField field4) {
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
    public final HR having(final IPredicate predicate) {
        if (this.groupByList != null) {
            this.addHavingPredicate(predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(Supplier<IPredicate> supplier) {
        if (this.groupByList != null) {
            this.addHavingPredicate(supplier.get());
        }
        return (HR) this;
    }


    @Override
    public final <E> HR having(Function<E, IPredicate> operator, E value) {
        if (this.groupByList != null) {
            this.addHavingPredicate(operator.apply(value));
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR having(Function<V, IPredicate> operator, Function<K, V> function, K key) {
        if (this.groupByList != null) {
            this.addHavingPredicate(operator.apply(function.apply(key)));
        }
        return (HR) this;
    }

    @Override
    public final <E> HR having(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator,
                               BiFunction<SimpleExpression, E, Expression> valueOperator, E value) {
        if (this.groupByList != null) {
            this.addHavingPredicate(expOperator.apply(valueOperator, value));
        }
        return (HR) this;
    }

    @Override
    public final <E> HR having(DialectBooleanOperator<E> fieldOperator,
                               BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                               BiFunction<SimpleExpression, E, Expression> func, @Nullable E value) {
        if (this.groupByList != null) {
            this.addHavingPredicate(fieldOperator.apply(operator, func, value));
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR having(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                  BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function,
                                  K key) {
        if (this.groupByList != null) {
            this.addHavingPredicate(expOperator.apply(valueOperator, function.apply(key)));
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR having(DialectBooleanOperator<V> fieldOperator,
                                  BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                  BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        if (this.groupByList != null) {
            this.addHavingPredicate(fieldOperator.apply(operator, func, function.apply(key)));
        }
        return (HR) this;
    }

    @Override
    public final <E> HR ifHaving(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator,
                                 BiFunction<SimpleExpression, E, Expression> valueOperator, Supplier<E> supplier) {
        if (this.groupByList != null) {
            final E value;
            if ((value = supplier.get()) != null) {
                this.addHavingPredicate(expOperator.apply(valueOperator, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final <E> HR ifHaving(DialectBooleanOperator<E> fieldOperator,
                                 BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                 BiFunction<SimpleExpression, E, Expression> func, Supplier<E> supplier) {
        if (this.groupByList != null) {
            final E value;
            if ((value = supplier.get()) != null) {
                this.addHavingPredicate(fieldOperator.apply(operator, func, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR ifHaving(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                    BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function,
                                    K key) {
        if (this.groupByList != null) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.addHavingPredicate(expOperator.apply(valueOperator, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR ifHaving(DialectBooleanOperator<V> fieldOperator,
                                    BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                    BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        if (this.groupByList != null) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.addHavingPredicate(fieldOperator.apply(operator, func, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final HR spaceAnd(IPredicate predicate) {
        if (this.groupByList != null) {
            this.addHavingPredicate(predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR spaceAnd(Supplier<IPredicate> supplier) {
        if (this.groupByList != null) {
            this.addHavingPredicate(supplier.get());
        }
        return (HR) this;
    }

    @Override
    public final <E> HR spaceAnd(Function<E, IPredicate> operator, E value) {
        if (this.groupByList != null) {
            this.addHavingPredicate(operator.apply(value));
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR spaceAnd(Function<V, IPredicate> operator, Function<K, V> operand, K key) {
        if (this.groupByList != null) {
            this.addHavingPredicate(operator.apply(operand.apply(key)));
        }
        return (HR) this;
    }

    @Override
    public final <E> HR spaceAnd(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator,
                                 BiFunction<SimpleExpression, E, Expression> valueOperator, E value) {
        if (this.groupByList != null) {
            this.addHavingPredicate(expOperator.apply(valueOperator, value));
        }
        return (HR) this;
    }

    @Override
    public final <E> HR spaceAnd(DialectBooleanOperator<E> fieldOperator,
                                 BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                 BiFunction<SimpleExpression, E, Expression> func, @Nullable E value) {
        if (this.groupByList != null) {
            this.addHavingPredicate(fieldOperator.apply(operator, func, value));
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR spaceAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                    BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function,
                                    K key) {
        if (this.groupByList != null) {
            this.addHavingPredicate(expOperator.apply(valueOperator, function.apply(key)));
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR spaceAnd(DialectBooleanOperator<V> fieldOperator,
                                    BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                    BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        if (this.groupByList != null) {
            this.addHavingPredicate(fieldOperator.apply(operator, func, function.apply(key)));
        }
        return (HR) this;
    }

    @Override
    public final <E> HR ifSpaceAnd(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator,
                                   BiFunction<SimpleExpression, E, Expression> valueOperator, Supplier<E> supplier) {
        if (this.groupByList != null) {
            final E value;
            if ((value = supplier.get()) != null) {
                this.addHavingPredicate(expOperator.apply(valueOperator, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final <E> HR ifSpaceAnd(DialectBooleanOperator<E> fieldOperator,
                                   BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                   BiFunction<SimpleExpression, E, Expression> func, Supplier<E> supplier) {
        if (this.groupByList != null) {
            final E value;
            if ((value = supplier.get()) != null) {
                this.addHavingPredicate(fieldOperator.apply(operator, func, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR ifSpaceAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                      BiFunction<SimpleExpression, V, Expression> valueOperator,
                                      Function<K, V> function, K key) {
        if (this.groupByList != null) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.addHavingPredicate(expOperator.apply(valueOperator, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final <K, V> HR ifSpaceAnd(DialectBooleanOperator<V> fieldOperator,
                                      BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                      BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        if (this.groupByList != null) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.addHavingPredicate(fieldOperator.apply(operator, func, value));
            }
        }
        return (HR) this;
    }

    @Override
    public final HD having(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(true);
        }
        return (HD) this;
    }


    @Override
    public final HD ifHaving(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(false);
        }
        return (HD) this;
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
    public final void close() {
        this.recursive = false;
        this.cteList = null;
        this.hintList = null;
        this.modifierList = null;

        this.tableBlockList = null;
        this.groupByList = null;
        this.havingList = null;
        this.clearOrderByList();

        this.clearWhereClause();
        this.onClear();
    }

    @Override
    public final List<String> validateIdDefaultExpression() {
        final String msgSuffix = "in id default scalar expression";

        String m;
        final List<_Cte> cteList = this.cteList;
        if (cteList != null && cteList.size() > 0) {
            m = String.format("couldn't exists WITH clause %s", msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }

        final List<? extends Selection> selectionList = this.context.flatSelectItems();
        if (selectionList.size() != 1) {
            //io.army.criteria.impl.Expressions.scalarExpression(SubQuery) no bug,never here
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, "Expression isn't scalar sub query.");
        } else if (this.hasLimitClause()) {
            m = String.format("couldn't exists LIMIT/OFFSET clause %s.", msgSuffix);
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (this.hasGroupByClause()) {
            m = String.format("couldn't exists GROUP BY clause %s.", msgSuffix);
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (this instanceof _Query._WindowClauseSpec && ((_WindowClauseSpec) this).windowList().size() > 0) {
            m = String.format("couldn't exists WINDOW clause %s.", msgSuffix);
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        final Selection selection = selectionList.get(0);
        final DerivedField derivedIdField;


        if (selection instanceof DerivedField) {
            derivedIdField = (DerivedField) selection;
        } else if (selection instanceof ArmySelections.ExpressionSelection
                && ((ArmySelections.ExpressionSelection) selection).expression instanceof DerivedField) {
            derivedIdField = (DerivedField) ((ArmySelections.ExpressionSelection) selection).expression;
        } else {
            m = String.format("%s %s isn't derived field %s.", Selection.class.getName(), selection.label(), msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }

        final List<_TabularBlock> blockList = this.tableBlockList;
        assert blockList != null;
        if (blockList.size() != 1) {
            m = String.format("Must just one from-item %s", msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }
        final _TabularBlock block = blockList.get(0);
        final TabularItem item = block.tableItem();
        if (!(item instanceof _Cte)) {
            m = String.format("From item must be CTE %s.", msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }

        final List<_Predicate> whereClause = this.wherePredicateList();
        switch (whereClause.size()) {
            case 0:
                return ArrayUtils.of(((_Cte) item).name(), derivedIdField.fieldName());
            case 1:
                //no-op
                break;
            default: {
                m = String.format("Can only exists one equal predicate %s.", msgSuffix);
                throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
            }
        }

        final OperationPredicate predicate = (OperationPredicate) whereClause.get(0);
        final Expressions.DualPredicate dual;
        if (!(predicate instanceof Expressions.DualPredicate)
                || (dual = (Expressions.DualPredicate) predicate).operator != DualBooleanOperator.EQUAL) {
            m = String.format("Not found equal predicate %s.", msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        } else if (dual.left == derivedIdField) {
            m = String.format("%s couldn't be %s %s", dual.left, derivedIdField, msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        } else if (!(dual.left instanceof DerivedField)) {
            m = String.format("%s isn't %s %s.", dual.left, DerivedField.class.getName(), msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        } else if (dual.right != SQLs.BATCH_NO_PARAM && dual.right != SQLs.BATCH_NO_LITERAL) {
            m = String.format("The right item of %s should be %s.%s or %s.%s , but is %s %s",
                    dual.left,
                    SQLs.class.getName(), "BATCH_NO_PARAM",
                    SQLs.class.getName(), "BATCH_NO_LITERAL",
                    dual.right,
                    msgSuffix);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }
        return ArrayUtils.of(((_Cte) item).name(), derivedIdField.fieldName(), ((DerivedField) dual.left).fieldName());
    }

    @Override
    public final String validateParentSubInsertRowNumberQuery(final String thisCteName, final List<String> names) {
        if (names.size() < 3) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        String m;
        final List<_Cte> cteList = this.cteList;
        if (cteList != null && cteList.size() > 0) {
            m = String.format("couldn't exists WITH clause in CTE[%s]", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        } else if (this.hasLimitClause()) {
            m = String.format("couldn't exists LIMIT/OFFSET clause in CTE[%s].", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        } else if (this.hasGroupByClause()) {
            m = String.format("couldn't exists GROUP BY clause in CTE[%s].", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        } else if (this instanceof _Query._WindowClauseSpec && ((_WindowClauseSpec) this).windowList().size() > 0) {
            m = String.format("couldn't exists WINDOW clause in CTE[%s].", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }

        final List<? extends _SelectItem> selectItemList = this.selectItemList();
        if (selectItemList.size() != 2) {
            m = String.format("parent sub-insert row number CTE[%s] must two select item.", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }
        final String idAlias = names.get(1), rowNumberAlias = names.get(2);

        final _SelectItem selectItem;
        selectItem = selectItemList.get(0);
        Expression expression;
        if (!(selectItem instanceof ArmySelections.ExpressionSelection)) {
            m = String.format("first select item isn't window function rowNumber() expression in CTE[%s]", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }
        if (!((Selection) selectItem).label().equals(rowNumberAlias)) {
            m = String.format("first selection isn't selection[%s] in CTE[%s]", rowNumberAlias, thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        } else if (!((expression = ((ArmySelections.ExpressionSelection) selectItem).expression) instanceof WindowFunctionUtils.WindowFunction)
                || ((WindowFunctionUtils.WindowFunction<?>) expression).isNotGlobalRowNumber()) {
            m = String.format("selection[%s] isn't global window function rowNumber() expression in CTE[%s]",
                    rowNumberAlias, thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }

        if (this.refSelection(idAlias) == null) {
            m = String.format("parent sub-insert id selection[%s] in CTE[%s]", idAlias, thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }


        final List<_TabularBlock> blockList = this.tableBlockList;
        assert blockList != null;
        if (blockList.size() != 1) {
            m = String.format("Must just one from-item in CTE[%s]", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }
        final _TabularBlock block = blockList.get(0);
        final TabularItem item = block.tableItem();
        if (!(item instanceof _Cte)) {
            m = String.format("from-item isn't CTE in CTE[%s]", thisCteName);
            throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
        }
        return ((_Cte) item).name();
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


    abstract B createCteBuilder(boolean recursive);


    final WE endStaticWithClause(final boolean recursive) {
        if (this.cteList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, true);
        return (WE) this;
    }


    final void endStmtBeforeCommand() {
        this.endQueryStatement(true);
    }

    final boolean hasGroupByClause() {
        final List<ArmyGroupByItem> itemList = this.groupByList;
        return itemList != null && itemList.size() > 0;
    }


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

    private WE endDynamicWithClause(final B builder, final boolean required) {
        ((CriteriaSupports.CteBuilder) builder).endLastCte();

        final boolean recursive;
        recursive = builder.isRecursive();
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, required);
        return (WE) this;
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

        this.endOrderByClauseIfNeed();

        this.endGroupBy(false);
        this.endHaving(false);

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
            predicateList = _Collections.arrayList();
            this.havingList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        predicateList.add((OperationPredicate) predicate);
    }

    private void endHaving(final boolean required) {
        final List<_Predicate> predicateList = this.havingList;
        if (this.groupByList == null) {
            this.havingList = _Collections.emptyList();
        } else if (predicateList instanceof ArrayList) {
            this.havingList = _Collections.unmodifiableList(predicateList);
        } else if (predicateList == null) {
            if (required) {
                throw ContextStack.criteriaError(this.context, "having clause is empty");
            }
            this.havingList = _Collections.emptyList();
        }

    }


    static abstract class WithCteDistinctOnSimpleQueries<Q extends Item, B extends CteBuilderSpec, WE extends Item, W extends Query.SelectModifier, SR extends Item, SD extends Item, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, HD, OR, OD, LR, LO, LF, SP>
            extends SimpleQueries<Q, B, WE, W, SR, SD, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, GR, GD, HR, HD, OR, OD, LR, LO, LF, SP>
            implements _SelectDistinctOnDispatcher<W, SR, SD>,
            _Query._DistinctOnClauseSpec {

        private List<_Expression> distinctOnExpList;

        private boolean registered;

        WithCteDistinctOnSimpleQueries(@Nullable ArmyStmtSpec withSpec, CriteriaContext context) {
            super(withSpec, context);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Consumer<Consumer<Expression>> expConsumer) {
            this.registerDistinctOn(true, expConsumer);
            return this;
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectIfDistinctOn(Consumer<Consumer<Expression>> expConsumer) {
            this.registerDistinctOn(false, expConsumer);
            return this;
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(SQLs.WordDistinct distinct, SQLs.WordOn on,
                                                         Consumer<Consumer<Expression>> expConsumer) {
            this.registerDistinctOn(true, distinct, expConsumer);
            return this;
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on,
                                                           Consumer<Consumer<Expression>> expConsumer) {
            this.registerDistinctOn(false, distinct, expConsumer);
            return this;
        }


        @Override
        public final SD selectDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer) {
            this.registerDistinctOn(true, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
        }

        @Override
        public final SD selectIfDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer) {
            this.registerDistinctOn(false, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
        }

        @Override
        public final SD select(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer,
                               Consumer<_DeferSelectSpaceClause> consumer) {
            this.registerDistinctOn(true, distinct, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
        }

        @Override
        public final SD selectIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on,
                                 Consumer<Consumer<Expression>> expConsumer, final Consumer<_DeferSelectSpaceClause> consumer) {
            this.registerDistinctOn(false, distinct, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
        }

        @Override
        public final SD selectsDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            this.registerDistinctOn(true, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
        }

        @Override
        public final SD selectsIfDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            this.registerDistinctOn(false, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
        }


        @Override
        public final SD selects(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer,
                                Consumer<SelectionConsumer> consumer) {
            this.registerDistinctOn(true, distinct, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
        }


        @Override
        public final SD selectsIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on,
                                  Consumer<Consumer<Expression>> expConsumer, final Consumer<SelectionConsumer> consumer) {
            this.registerDistinctOn(false, distinct, expConsumer);
            this.context.registerDeferSelectClause(() -> consumer.accept(new SelectionConsumerImpl(this.context)));
            return (SD) this;
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

        private void registerDistinctOn(final boolean required, final Consumer<Consumer<Expression>> expConsumer) {
            if (this.registered) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.context.addSelectClauseEndListener(() -> {
                final List<_Expression> list = _Collections.arrayList();
                expConsumer.accept(e -> {
                    if (!(e instanceof ArmyExpression)) {
                        throw ContextStack.nonArmyExp(this.context);
                    }
                    list.add((ArmyExpression) e);
                });

                if (this.distinctOnExpList != null) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                if (list.size() > 0) {
                    this.selectDistinct();
                    this.distinctOnExpList = _Collections.unmodifiableList(list);
                } else if (required) {
                    throw CriteriaUtils.dontAddAnyItem();
                } else {
                    this.distinctOnExpList = _Collections.emptyList();
                }

            });

            this.registered = true;
        }

        private void registerDistinctOn(final boolean required, final @Nullable SQLs.WordDistinct distinct,
                                        final Consumer<Consumer<Expression>> expConsumer) {
            if (this.registered) {
                throw ContextStack.castCriteriaApi(this.context);
            }

            this.context.addSelectClauseEndListener(() -> {
                final List<_Expression> list = _Collections.arrayList();
                expConsumer.accept(e -> {
                    if (!(e instanceof ArmyExpression)) {
                        throw ContextStack.nonArmyExp(this.context);
                    }
                    list.add((ArmyExpression) e);
                });

                if (this.distinctOnExpList != null) {
                    throw ContextStack.castCriteriaApi(this.context);
                }

                if (distinct != null && list.size() > 0) {
                    this.selectDistinct();
                    this.distinctOnExpList = _Collections.unmodifiableList(list);
                } else if (required) {
                    if (list.size() == 0) {
                        throw CriteriaUtils.dontAddAnyItem();
                    }
                    throw ContextStack.nullPointer(this.context);
                } else {
                    if (distinct != null) {
                        this.selectDistinct();
                    }
                    this.distinctOnExpList = _Collections.emptyList();
                }

            });

            this.registered = true;
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
            this.tableAliasList = ArrayUtils.of(tableAlias1, tableAlias2);
            return (LT) this;
        }

        @Override
        public final LT of(String tableAlias1, String tableAlias2, String tableAlias3) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = ArrayUtils.of(tableAlias1, tableAlias2, tableAlias3);
            return (LT) this;
        }

        @Override
        public final LT of(String tableAlias1, String tableAlias2, String tableAlias3, String tableAlias4) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = ArrayUtils.of(tableAlias1, tableAlias2, tableAlias3, tableAlias4);
            return (LT) this;
        }

        @Override
        public final LT of(String tableAlias1, String tableAlias2, String tableAlias3, String tableAlias4,
                           String tableAlias5, String... restTableAlias) {
            if (this.clauseEnd) {
                throw ContextStack.castCriteriaApi(this.getContext());
            }
            this.tableAliasList = ArrayUtils.of(tableAlias1, tableAlias2, tableAlias3, tableAlias4,
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


    static final class NamedWindowAsClause<T extends Item, R extends Item>
            implements Window._WindowAsClause<T, R> {

        private final CriteriaContext context;

        private final String name;

        private final Function<ArmyWindow, R> function;

        private final TeFunction<String, CriteriaContext, String, T> constructor;

        /**
         * @param name        window name
         * @param function    end function
         * @param constructor constructor of window. arguments:
         *                    <ul>
         *                      <li>first : window name</li>
         *                      <li>second : {@link CriteriaContext}</li>
         *                      <li>third : nullable existingWindowName </li>
         *                    </ul>
         */
        NamedWindowAsClause(CriteriaContext context, String name, Function<ArmyWindow, R> function,
                            TeFunction<String, CriteriaContext, String, T> constructor) {
            this.context = context;
            this.name = name;
            this.function = function;
            this.constructor = constructor;
        }

        @Override
        public R as() {
            return this.function.apply(SQLWindow.namedGlobalWindow(this.context, this.name));
        }

        @Override
        public R as(@Nullable String existingWindowName) {
            return this.function.apply(SQLWindow.namedRefWindow(this.context, this.name, existingWindowName));
        }

        @Override
        public R as(Consumer<T> consumer) {
            return this.as(null, consumer);
        }

        @Override
        public R as(@Nullable String existingWindowName, Consumer<T> consumer) {
            final T window;
            window = this.constructor.apply(this.name, this.context, existingWindowName);
            consumer.accept(window);
            return this.function.apply((ArmyWindow) window);
        }


    }//NamedWindowAsClause


    static abstract class SelectClauseDispatcher<W extends Query.SelectModifier, SR extends Item, SD>
            implements Query._SelectDispatcher<W, SR, SD>,
            CriteriaContextSpec {

        final CriteriaContext context;

        SelectClauseDispatcher(Dialect dialect, @Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            this.context = CriteriaContexts.dispatcherContext(dialect, outerContext, leftContext);
            ContextStack.push(this.context);
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final SR select(Selection selection) {
            return this.createSelectClause().select(selection);
        }

        @Override
        public final SR select(Function<String, Selection> function, String alias) {
            return this.createSelectClause().select(function, alias);
        }

        @Override
        public final SR select(Selection selection1, Selection selection2) {
            return this.createSelectClause().select(selection1, selection2);
        }

        @Override
        public final SR select(Function<String, Selection> function, String alias, Selection selection) {
            return this.createSelectClause().select(function, alias, selection);
        }

        @Override
        public final SR select(Selection selection, Function<String, Selection> function, String alias) {
            return this.createSelectClause().select(selection, function, alias);
        }

        @Override
        public final SR select(Function<String, Selection> function1, String alias1,
                               Function<String, Selection> function2, String alias2) {
            return this.createSelectClause().select(function1, alias1, function2, alias2);
        }

        @Override
        public final SR select(SqlField field1, SqlField field2, SqlField field3) {
            return this.createSelectClause().select(field1, field2, field3);
        }

        @Override
        public final SR select(SqlField field1, SqlField field2, SqlField field3, SqlField field4) {
            return this.createSelectClause().select(field1, field2, field3, field4);
        }

        @Override
        public final SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            return this.createSelectClause().select(tableAlias, period, table);
        }

        @Override
        public final <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                   String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
            return this.createSelectClause().select(parenAlias, period1, parent, childAlias, period2, child);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectAll() {
            return this.createSelectClause().selectAll();
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinct() {
            return this.createSelectClause().selectDistinct();
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(List<W> modifiers) {
            return this.createSelectClause().select(modifiers);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(Supplier<List<Hint>> hints, List<W> modifiers) {
            return this.createSelectClause().select(hints, modifiers);
        }

        @Override
        public final SD select(Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().select(consumer);
        }

        @Override
        public final SD select(W modifier, Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().select(modifier, consumer);
        }

        @Override
        public final SD select(List<W> modifiers, Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().select(modifiers, consumer);
        }

        @Override
        public final SD select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().select(hints, modifiers, consumer);
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


        WithBuilderSelectClauseDispatcher(Dialect dialect, @Nullable CriteriaContext outerContext,
                                          @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
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

        WithDistinctOnSelectClauseDispatcher(Dialect dialect, @Nullable CriteriaContext outerContext,
                                             @Nullable CriteriaContext leftContext) {
            super(dialect, outerContext, leftContext);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectDistinctOn(Consumer<Consumer<Expression>> expConsumer) {
            return this.createSelectClause().selectDistinctOn(expConsumer);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> selectIfDistinctOn(Consumer<Consumer<Expression>> expConsumer) {
            return this.createSelectClause().selectIfDistinctOn(expConsumer);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer) {
            return this.createSelectClause().select(distinct, on, expConsumer);
        }


        @Override
        public final _StaticSelectSpaceClause<SR> selectIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on,
                                                           Consumer<Consumer<Expression>> expConsumer) {
            return this.createSelectClause().selectIf(distinct, on, expConsumer);
        }

        @Override
        public final SD selectIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on,
                                 Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().selectIf(distinct, on, expConsumer, consumer);
        }

        @Override
        public final SD selectsIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on,
                                  Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selectsIf(distinct, on, expConsumer, consumer);
        }

        @Override
        public final SD selectDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().selectDistinctOn(expConsumer, consumer);
        }

        @Override
        public final SD selectsDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selectsDistinctOn(expConsumer, consumer);
        }

        @Override
        public final SD select(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().select(distinct, on, expConsumer, consumer);
        }

        @Override
        public final SD selects(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selects(distinct, on, expConsumer, consumer);
        }

        @Override
        public final SD selectIfDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer) {
            return this.createSelectClause().selectIfDistinctOn(expConsumer, consumer);
        }

        @Override
        public final SD selectsIfDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer) {
            return this.createSelectClause().selectsIfDistinctOn(expConsumer, consumer);
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


        UnionBatchSelect wrapToBatchSelect(List<?> paramList) {
            return new UnionBatchSelect(this, CriteriaUtils.paramList(paramList));
        }

    }//UnionSelect


    static final class UnionBatchSelect extends UnionRowSet implements ArmyBatchSelect {

        private final List<?> paramList;

        private UnionBatchSelect(UnionSelect select, List<?> paramList) {
            super(select.left, select.unionType, select.right);
            this.paramList = paramList;
        }

        @Override
        public List<? extends _SelectItem> selectItemList() {
            return ((_PrimaryRowSet) this.left).selectItemList();
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }


    }//UnionBatchSelect


    private static final class SelectionConsumerImpl implements SelectionConsumer, Query._DeferSelectSpaceClause,
            Query._DeferSelectCommaSpace {

        private final CriteriaContext context;

        private boolean spaceFirst;

        /**
         * @see #selects(Consumer)
         */
        private SelectionConsumerImpl(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public _DeferSelectCommaSpace space(Selection selection) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(selection);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Function<String, Selection> function, String alias) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Selection selection1, Selection selection2) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(selection1)
                    .onAddSelectItem(selection2);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Function<String, Selection> function, String alias, Selection selection) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(function.apply(alias))
                    .onAddSelectItem(selection);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Selection selection, Function<String, Selection> function, String alias) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(selection)
                    .onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(Function<String, Selection> function1, String alias1,
                                            Function<String, Selection> function2, String alias2) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(function1.apply(alias1))
                    .onAddSelectItem(function2.apply(alias2));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(SqlField field1, SqlField field2, SqlField field3) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(SqlField field1, SqlField field2, SqlField field3, SqlField field4) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3)
                    .onAddSelectItem(field4);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
            return this;
        }

        @Override
        public <P> _DeferSelectCommaSpace space(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                                String childAlias, SQLs.SymbolPeriod period2,
                                                ComplexTableMeta<P, ?> child) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            if (child.parentMeta() != parent) {
                throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
            }
            this.context.onAddSelectItem(SelectionGroups.singleGroup(parent, parenAlias))
                    .onAddSelectItem(SelectionGroups.groupWithoutId(child, childAlias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
            if (this.spaceFirst) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.spaceFirst = true;
            this.context.onAddDerivedGroup(derivedAlias);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Selection selection) {
            this.context.onAddSelectItem(selection);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Selection selection1, Selection selection2) {
            this.context.onAddSelectItem(selection1)
                    .onAddSelectItem(selection2);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Function<String, Selection> function, String alias, Selection selection) {
            this.context.onAddSelectItem(function.apply(alias))
                    .onAddSelectItem(selection);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Selection selection, Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(selection)
                    .onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(Function<String, Selection> function1, String alias1,
                                            Function<String, Selection> function2, String alias2) {
            this.context.onAddSelectItem(function1.apply(alias1))
                    .onAddSelectItem(function2.apply(alias2));
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(SqlField field1, SqlField field2, SqlField field3) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(SqlField field1, SqlField field2, SqlField field3, SqlField field4) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3)
                    .onAddSelectItem(field4);
            return this;
        }

        @Override
        public _DeferSelectCommaSpace comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
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
            this.context.onAddDerivedGroup(derivedAlias);
            return this;
        }

        @Override
        public SelectionConsumer selection(Selection selection) {
            this.context.onAddSelectItem(selection);
            return this;
        }

        @Override
        public SelectionConsumer selection(Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public SelectionConsumer selection(Selection selection1, Selection selection2) {
            this.context.onAddSelectItem(selection1)
                    .onAddSelectItem(selection2);
            return this;
        }

        @Override
        public SelectionConsumer selection(Function<String, Selection> function, String alias, Selection selection) {
            this.context.onAddSelectItem(function.apply(alias))
                    .onAddSelectItem(selection);
            return this;
        }

        @Override
        public SelectionConsumer selection(Selection selection, Function<String, Selection> function, String alias) {
            this.context.onAddSelectItem(selection)
                    .onAddSelectItem(function.apply(alias));
            return this;
        }

        @Override
        public SelectionConsumer selection(Function<String, Selection> function1, String alias1,
                                           Function<String, Selection> function2, String alias2) {
            this.context.onAddSelectItem(function1.apply(alias1))
                    .onAddSelectItem(function2.apply(alias2));
            return this;
        }

        @Override
        public SelectionConsumer selection(SqlField field1, SqlField field2, SqlField field3) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3);
            return this;
        }

        @Override
        public SelectionConsumer selection(SqlField field1, SqlField field2, SqlField field3, SqlField field4) {
            this.context.onAddSelectItem(field1)
                    .onAddSelectItem(field2)
                    .onAddSelectItem(field3)
                    .onAddSelectItem(field4);
            return this;
        }

        @Override
        public SelectionConsumer selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
            return this;
        }

        @Override
        public <P> SelectionConsumer selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
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
        public SelectionConsumer selection(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
            this.context.onAddDerivedGroup(derivedAlias);
            return this;
        }


    }//SelectionsImpl

    static abstract class ArmyBatchSimpleSelect extends CriteriaSupports.StatementMockSupport implements Query,
            _Query, _Statement._WithClauseSpec, ArmyBatchSelect {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final List<Hint> hintList;

        private final List<? extends SQLWords> modifierList;

        private final int selectionSize;

        private final List<? extends _SelectItem> selectItemList;

        private final List<_TabularBlock> tableBlockList;

        private final List<_Predicate> wherePredicateList;

        private final List<? extends GroupByItem> groupByList;

        private final List<_Predicate> havingList;

        private final List<? extends SortItem> orderByList;

        private final _Expression rowExpression;

        private final _Expression offsetExpression;

        private final List<?> paramList;

        private boolean prepared = true;


        ArmyBatchSimpleSelect(final _Query query, final List<?> paramList) {
            super(((CriteriaContextSpec) query).getContext());
            this.recursive = query.isRecursive();
            this.cteList = query.cteList();
            this.hintList = query.hintList();
            this.modifierList = query.modifierList();

            this.selectionSize = query.selectionSize();
            this.selectItemList = query.selectItemList();
            this.tableBlockList = query.tableBlockList();
            this.wherePredicateList = query.wherePredicateList();

            this.groupByList = query.groupByList();
            this.havingList = query.havingList();
            this.orderByList = query.orderByList();
            this.rowExpression = query.rowCountExp();

            this.offsetExpression = query.offsetExp();
            this.paramList = paramList;
        }


        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }


        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<? extends SQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final int selectionSize() {
            return this.selectionSize;
        }

        @Override
        public final List<? extends _SelectItem> selectItemList() {
            return this.selectItemList;
        }

        @Override
        public final List<_TabularBlock> tableBlockList() {
            return this.tableBlockList;
        }

        @Override
        public final List<_Predicate> wherePredicateList() {
            return this.wherePredicateList;
        }

        @Override
        public final List<? extends GroupByItem> groupByList() {
            return this.groupByList;
        }

        @Override
        public final List<_Predicate> havingList() {
            return this.havingList;
        }

        @Override
        public final List<? extends SortItem> orderByList() {
            return this.orderByList;
        }


        @Override
        public final _Expression rowCountExp() {
            return this.rowExpression;
        }

        @Override
        public final _Expression offsetExp() {
            return this.offsetExpression;
        }


        @Override
        public final List<?> paramList() {
            return this.paramList;
        }

        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            return this.prepared;
        }

        @Override
        public final void close() {
            if (!this.prepared) {
                return;
            }
            this.prepared = false;
        }


    }//ArmyBatchSelect


}
