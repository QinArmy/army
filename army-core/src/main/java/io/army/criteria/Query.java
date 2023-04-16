package io.army.criteria;


import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This interface representing query,is base interface of below:
 *     <ul>
 *         <li>{@link Select}</li>
 *         <li>{@link SubQuery}</li>
 *     </ul>
 * </p>
 *
 * @see Select
 * @see SubQuery
 * @since 1.0
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

    interface TableModifier extends SQLWords {

    }

    interface DerivedModifier extends SQLWords {

    }

    interface FetchFirstNext {

    }

    interface FetchRow {

    }

    interface FetchOnly {

    }

    interface FetchWithTies {

    }

    interface FetchOnlyWithTies extends FetchOnly, FetchWithTies {

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

        SR select(DataField field1, DataField field2, DataField field3);

        SR select(DataField field1, DataField field2, DataField field3, DataField field4);

        SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                      String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);

    }


    interface _StaticSelectSpaceClause<SR extends Item> {

        SR space(Selection selection);

        SR space(Function<String, Selection> function, String alias);

        SR space(Selection selection1, Selection selection2);

        SR space(Function<String, Selection> function, String alias, Selection selection);

        SR space(Selection selection, Function<String, Selection> function, String alias);

        SR space(Function<String, Selection> function1, String alias1, Function<String, Selection> function2, String alias2);

        SR space(DataField field1, DataField field2, DataField field3);

        SR space(DataField field1, DataField field2, DataField field3, DataField field4);

        SR space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR space(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                     String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);
    }


    interface _DynamicDistinctOnExpClause<SR extends Item> {

        _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp);

        _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp1, Expression exp2);

        _StaticSelectSpaceClause<SR> selectDistinctOn(Expression exp1, Expression exp2, Expression exp3);

        _StaticSelectSpaceClause<SR> selectDistinctOn(Consumer<Consumer<Expression>> consumer);

        _StaticSelectSpaceClause<SR> selectDistinctIfOn(Consumer<Consumer<Expression>> consumer);

    }

    interface _DynamicDistinctOnAndSelectsClause<SD extends Item> {

        SD selectDistinctOn(Expression exp, Consumer<Selections> consumer);

        SD selectDistinctOn(Expression exp1, Expression exp2, Consumer<Selections> consumer);

        SD selectDistinctOn(Expression exp1, Expression exp2, Expression exp3, Consumer<Selections> consumer);

        SD selectDistinctOn(Consumer<Consumer<Expression>> expConsumer, Consumer<Selections> consumer);

        SD selectDistinctIfOn(Consumer<Consumer<Expression>> expConsumer, Consumer<Selections> consumer);
    }


    interface _ModifierSelectClause<W extends SelectModifier, SR extends Item> extends _StaticSelectClause<SR> {

        _StaticSelectSpaceClause<SR> select(W modifier);

    }

    interface _ModifierListSelectClause<W extends SelectModifier, SR extends Item>
            extends _ModifierSelectClause<W, SR> {

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

        SR comma(DataField field1, DataField field2, DataField field3);

        SR comma(DataField field1, DataField field2, DataField field3, DataField field4);

        SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                     String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star);

    }


    interface _DynamicSelectClause<SD> {


        SD selects(Consumer<Selections> consumer);


    }

    interface _DynamicModifierSelectClause<W extends SelectModifier, SD> extends _DynamicSelectClause<SD> {

        SD selects(W modifier, Consumer<Selections> consumer);

    }


    interface _DynamicModifierListSelectClause<W extends SelectModifier, SD>
            extends _DynamicModifierSelectClause<W, SD> {

        SD selects(List<W> modifierList, Consumer<Selections> consumer);

    }

    interface _DynamicHintModifierSelectClause<W extends SelectModifier, SD>
            extends _DynamicModifierListSelectClause<W, SD> {


        SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Selections> consumer);

    }


    /**
     * <p>
     * This interface representing dialect FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <R> next clause java type
     * @see Statement._FromClause
     * @since 1.0
     */
    interface _FromTableClause<R> {

        R from(TableMeta<?> table);
    }


    interface _GroupByClause<R> {

        R groupBy(Expression sortItem);

        R groupBy(Expression sortItem1, Expression sortItem2);

        R groupBy(Expression sortItem1, Expression sortItem2, Expression sortItem3);

        R groupBy(Consumer<Consumer<Expression>> consumer);

        R ifGroupBy(Consumer<Consumer<Expression>> consumer);

    }


    interface _HavingClause<R> {

        R having(IPredicate predicate);

        R having(IPredicate predicate1, IPredicate predicate2);

        R having(Supplier<IPredicate> supplier);

        R having(Function<Object, IPredicate> operator, Supplier<?> operand);

        R having(Function<Object, IPredicate> operator, Function<String, ?> operand, String operandKey);

        R having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        R having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

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
