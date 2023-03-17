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

    interface _SelectClauseOfQuery extends Item {
        //NO method
    }


    interface _StaticSelectClause<SR extends Item> extends _SelectClauseOfQuery {

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

        SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

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

        SR space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);
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

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

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
     * @param <FP> next clause java type
     * @see Statement._FromClause
     * @since 1.0
     */
    interface _DialectFromClause<FP> {

        FP from(TableMeta<?> table);
    }


    interface _GroupByClause<GR> {

        GR groupBy(Expression sortItem);

        GR groupBy(Expression sortItem1, Expression sortItem2);

        GR groupBy(Expression sortItem1, Expression sortItem2, Expression sortItem3);

        GR groupBy(Consumer<Consumer<Expression>> consumer);

        GR ifGroupBy(Consumer<Consumer<Expression>> consumer);

    }


    interface _HavingClause<HR> {

        HR having(IPredicate predicate);

        HR having(IPredicate predicate1, IPredicate predicate2);

        HR having(Supplier<IPredicate> supplier);

        HR having(Function<Object, IPredicate> operator, Supplier<?> operand);

        HR having(Function<Object, IPredicate> operator, Function<String, ?> operand, String operandKey);

        HR having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        HR having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

        HR having(Consumer<Consumer<IPredicate>> consumer);

        HR ifHaving(Consumer<Consumer<IPredicate>> consumer);

    }

    interface _LockOfTableAliasClause<OR> {

        OR of(String tableAlias);

        OR of(String firstTableAlias, String... restTableAlias);

        OR of(Consumer<Consumer<String>> consumer);

        OR ifOf(Consumer<Consumer<String>> consumer);

    }


    interface _MinLockWaitOptionClause<WR> {

        WR noWait();

        WR skipLocked();

        WR ifNoWait(BooleanSupplier predicate);

        WR ifSkipLocked(BooleanSupplier predicate);

    }

    interface _LockForUpdateClause<LR> {

        LR forUpdate();

        LR ifForUpdate(BooleanSupplier predicate);
    }


    interface _MinLockOptionClause<LR> extends _LockForUpdateClause<LR> {


        LR forShare();

        LR ifForShare(BooleanSupplier predicate);

    }


    interface _QueryUnionClause<SP> {

        SP union();

        SP unionAll();

        SP unionDistinct();
    }


    interface _QueryIntersectClause<SP> {

        SP intersect();

        SP intersectAll();

        SP intersectDistinct();
    }


    interface _QueryExceptClause<SP> {

        SP except();

        SP exceptAll();

        SP exceptDistinct();
    }


    interface _QueryMinusClause<SP> {

        SP minus();

        SP minusAll();

        SP minusDistinct();
    }

    interface _DynamicParensQueryClause<T extends Item, R extends Item> {

        R parens(Function<T, R> function);
    }


    interface _SelectDispatcher<W extends SelectModifier, SR extends Item, SD>
            extends _HintsModifiersListSelectClause<W, SR>, _DynamicHintModifierSelectClause<W, SD> {

    }

    interface _SelectAndCommaDispatcher<SR extends Item> extends _StaticSelectClause<SR>,
            _StaticSelectCommaClause<SR> {

    }


    interface _WithSelectDispatcher<B extends CteBuilderSpec, WE, W extends SelectModifier, SR extends Item, SD>
            extends DialectStatement._DynamicWithClause<B, WE>, _SelectDispatcher<W, SR, SD> {

    }


}
