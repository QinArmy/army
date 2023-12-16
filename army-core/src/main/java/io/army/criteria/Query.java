package io.army.criteria;


import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.SQLs;
import io.army.function.DialectBooleanOperator;
import io.army.function.ExpressionOperator;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This interface representing query,is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 *     <li>{@link SubQuery}</li>
 * </ul>
 *
 * @see Select
 * @see SubQuery
 * @since 0.6.0
 */
public interface Query extends RowSet {

    /**
     * @see SQLs#ALL
     * @see SQLs#DISTINCT
     */
    interface SelectModifier extends SQLWords {

    }

    /**
     * @see SQLs#ALL
     * @see SQLs#DISTINCT
     */
    interface UnionModifier {

    }




    /*-------------------below clause interfaces -------------------*/


    interface _AsQueryClause<I extends Item> {

        I asQuery();

    }


    /*################################## blow select clause  interfaces ##################################*/


    interface _StaticSelectClause<SR extends Item> extends Item {

        SR select(Selection selection);

        SR select(Function<String, Selection> function, String alias);

        SR select(Selection selection1, Selection selection2);

        SR select(Function<String, Selection> function, String alias, Selection selection);

        SR select(Selection selection, Function<String, Selection> function, String alias);

        SR select(Function<String, Selection> function1, String alias1, Function<String, Selection> function2, String alias2);

        SR select(SqlField field1, SqlField field2, SqlField field3);

        SR select(SqlField field1, SqlField field2, SqlField field3, SqlField field4);

        SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                      String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);


    }


    interface _StaticSelectSpaceClause<SR extends Item> {

        SR space(Selection selection);

        SR space(Function<String, Selection> function, String alias);

        SR space(Selection selection1, Selection selection2);

        SR space(Function<String, Selection> function, String alias, Selection selection);

        SR space(Selection selection, Function<String, Selection> function, String alias);

        SR space(Function<String, Selection> function1, String alias1, Function<String, Selection> function2, String alias2);

        SR space(SqlField field1, SqlField field2, SqlField field3);

        SR space(SqlField field1, SqlField field2, SqlField field3, SqlField field4);

        SR space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR space(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                     String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

    }


    interface _DynamicDistinctOnExpClause<SR extends Item> {


        _StaticSelectSpaceClause<SR> selectDistinctOn(Consumer<Consumer<Expression>> expConsumer);

        _StaticSelectSpaceClause<SR> select(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer);

        _StaticSelectSpaceClause<SR> selectIfDistinctOn(Consumer<Consumer<Expression>> expConsumer);

        _StaticSelectSpaceClause<SR> selectIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer);

    }

    interface _DynamicDistinctOnAndSelectsClause<SD extends Item> {

        SD selectDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer);

        SD selectsDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer);

        SD select(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer);

        SD selects(SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer);

        SD selectIfDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer);

        SD selectsIfDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer);

        SD selectIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer, Consumer<_DeferSelectSpaceClause> consumer);

        SD selectsIf(@Nullable SQLs.WordDistinct distinct, SQLs.WordOn on, Consumer<Consumer<Expression>> expConsumer, Consumer<SelectionConsumer> consumer);
    }


    interface _ModifierSelectClause<SR extends Item> extends _StaticSelectClause<SR> {

        _StaticSelectSpaceClause<SR> selectAll();

        _StaticSelectSpaceClause<SR> selectDistinct();

    }

    interface _ModifierListSelectClause<W extends SelectModifier, SR extends Item>
            extends _ModifierSelectClause<SR> {

        _StaticSelectSpaceClause<SR> select(List<W> modifiers);


    }

    interface _HintsModifiersListSelectClause<W extends SelectModifier, SR extends Item>
            extends _ModifierListSelectClause<W, SR> {

        _StaticSelectSpaceClause<SR> select(Supplier<List<Hint>> hints, List<W> modifiers);

    }


    interface _StaticSelectCommaClause<SR extends Item> extends Item {

        SR comma(Selection selection);

        SR comma(Function<String, Selection> function, String alias);

        SR comma(Selection selection1, Selection selection2);

        SR comma(Function<String, Selection> function, String alias, Selection selection);

        SR comma(Selection selection, Function<String, Selection> function, String alias);

        SR comma(Function<String, Selection> function1, String alias1, Function<String, Selection> function2, String alias2);

        SR comma(SqlField field1, SqlField field2, SqlField field3);

        SR comma(SqlField field1, SqlField field2, SqlField field3, SqlField field4);

        SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                     String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

    }


    interface _DeferSelectCommaSpace extends _StaticSelectCommaClause<_DeferSelectCommaSpace> {

        _DeferSelectCommaSpace comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);
    }


    interface _DeferSelectSpaceClause extends _StaticSelectSpaceClause<_DeferSelectCommaSpace>, _DeferContextSpec {

        _DeferSelectCommaSpace space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);


    }

    interface _DynamicSelectClause<SD> {

        SD select(Consumer<_DeferSelectSpaceClause> consumer);

        SD selects(Consumer<SelectionConsumer> consumer);

    }

    interface _DynamicModifierSelectClause<W extends SelectModifier, SD> extends _DynamicSelectClause<SD> {

        SD select(W modifier, Consumer<_DeferSelectSpaceClause> consumer);

        SD selects(W modifier, Consumer<SelectionConsumer> consumer);

    }


    interface _DynamicModifierListSelectClause<W extends SelectModifier, SD>
            extends _DynamicModifierSelectClause<W, SD> {

        SD select(List<W> modifiers, Consumer<_DeferSelectSpaceClause> consumer);

        SD selects(List<W> modifierList, Consumer<SelectionConsumer> consumer);

    }

    interface _DynamicHintModifierSelectClause<W extends SelectModifier, SD>
            extends _DynamicModifierListSelectClause<W, SD> {

        SD select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<_DeferSelectSpaceClause> consumer);

        SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<SelectionConsumer> consumer);

    }


    /**
     * <p>
     * This interface representing dialect FROM clause.
     *
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *
     *
     * @param <R> next clause java type
     * @see Statement._FromClause
     * @since 0.6.0
     */
    interface _FromTableClause<R> {

        R from(TableMeta<?> table);
    }


    interface _GroupByCommaClause<R> {

        R commaSpace(GroupByItem item);

        R commaSpace(GroupByItem item1, GroupByItem item2);

        R commaSpace(GroupByItem item1, GroupByItem item2, GroupByItem item3);

        R commaSpace(GroupByItem item1, GroupByItem item2, GroupByItem item3, GroupByItem item4);
    }


    interface _StaticGroupByClause<R> {

        R groupBy(GroupByItem item);

        R groupBy(GroupByItem item1, GroupByItem item2);

        R groupBy(GroupByItem item1, GroupByItem item2, GroupByItem item3);

        R groupBy(GroupByItem item1, GroupByItem item2, GroupByItem item3, GroupByItem item4);


    }


    interface _DynamicGroupByClause<R> {

        R groupBy(Consumer<Consumer<GroupByItem>> consumer);

        R ifGroupBy(Consumer<Consumer<GroupByItem>> consumer);
    }

    interface _HavingAndClause<R> {

        R spaceAnd(IPredicate predicate);

        R spaceAnd(Supplier<IPredicate> supplier);

        <E> R spaceAnd(Function<E, IPredicate> operator, E value);

        <K, V> R spaceAnd(Function<V, IPredicate> operator, Function<K, V> operand, K key);


        <E> R spaceAnd(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator, BiFunction<SimpleExpression, E, Expression> valueOperator, E value);

        <E> R spaceAnd(DialectBooleanOperator<E> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                       BiFunction<SimpleExpression, E, Expression> func, @Nullable E value);

        <K, V> R spaceAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator, BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function, K key);

        <K, V> R spaceAnd(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                          BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key);

        <E> R ifSpaceAnd(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator, BiFunction<SimpleExpression, E, Expression> valueOperator, Supplier<E> supplier);

        <E> R ifSpaceAnd(DialectBooleanOperator<E> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                         BiFunction<SimpleExpression, E, Expression> func, Supplier<E> supplier);

        <K, V> R ifSpaceAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator, BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function, K key);

        <K, V> R ifSpaceAnd(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                            BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key);
    }


    interface _StaticHavingClause<R> {

        R having(IPredicate predicate);

        R having(Supplier<IPredicate> supplier);

        <E> R having(Function<E, IPredicate> operator, E value);

        <K, V> R having(Function<V, IPredicate> operator, Function<K, V> operand, K key);


        <E> R having(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator, BiFunction<SimpleExpression, E, Expression> valueOperator, E value);

        <E> R having(DialectBooleanOperator<E> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                     BiFunction<SimpleExpression, E, Expression> func, @Nullable E value);

        <K, V> R having(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator, BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function, K key);

        <K, V> R having(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                        BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key);

        <E> R ifHaving(ExpressionOperator<SimpleExpression, E, IPredicate> expOperator, BiFunction<SimpleExpression, E, Expression> valueOperator, Supplier<E> supplier);

        <E> R ifHaving(DialectBooleanOperator<E> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                       BiFunction<SimpleExpression, E, Expression> func, Supplier<E> supplier);

        <K, V> R ifHaving(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator, BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function, K key);

        <K, V> R ifHaving(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                          BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key);


    }


    interface _DynamicHavingClause<R> {

        R having(Consumer<Consumer<IPredicate>> consumer);

        R ifHaving(Consumer<Consumer<IPredicate>> consumer);

    }

    interface _LockOfTableAliasClause<R> {

        R of(String tableAlias);

        R of(String tableAlias1, String tableAlias2);

        R of(String tableAlias1, String tableAlias2, String tableAlias3);

        R of(String tableAlias1, String tableAlias2, String tableAlias3, String tableAlias4);

        R of(String tableAlias1, String tableAlias2, String tableAlias3, String tableAlias4, String tableAlias5, String... restTableAlias);

        R of(Consumer<Consumer<String>> consumer);

        R ifOf(Consumer<Consumer<String>> consumer);

    }


    interface _MinLockWaitOptionClause<WR> {

        WR noWait();

        WR skipLocked();

        WR ifNoWait(BooleanSupplier predicate);

        WR ifSkipLocked(BooleanSupplier predicate);

    }

    interface _StaticForUpdateClause<R> extends Item {

        R forUpdate();

    }


    interface _MinLockStrengthClause<R> extends _StaticForUpdateClause<R> {

        R forShare();

    }

    interface _SimpleForUpdateClause<R> extends _StaticForUpdateClause<R> {

        R ifForUpdate(BooleanSupplier predicate);

    }


    interface _DynamicLockClause<T extends Item, R extends Item> {

        R ifFor(Consumer<T> consumer);

    }


    interface _SelectDispatcher<W extends SelectModifier, SR extends Item, SD>
            extends _HintsModifiersListSelectClause<W, SR>, _DynamicHintModifierSelectClause<W, SD> {

    }

    interface _SelectDistinctOnDispatcher<W extends SelectModifier, SR extends Item, SD extends Item>
            extends _SelectDispatcher<W, SR, SD>,
            _DynamicDistinctOnExpClause<SR>,
            _DynamicDistinctOnAndSelectsClause<SD> {

    }


    interface _WithSelectDispatcher<B extends CteBuilderSpec, WE extends Item, W extends SelectModifier, SR extends Item, SD>
            extends DialectStatement._DynamicWithClause<B, WE>, _SelectDispatcher<W, SR, SD> {

    }


}
