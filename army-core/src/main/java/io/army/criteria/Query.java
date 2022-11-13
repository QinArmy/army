package io.army.criteria;


import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.function.ExpressionOperator;
import io.army.function.SqlFunction;
import io.army.function.SqlOneFunction;
import io.army.function.SqlTwoFunction;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
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

    interface TabularModifier extends SQLWords {

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


    interface _AsQueryClause<Q extends Item> extends _RowSetSpec<Q> {

    }


    /*################################## blow select clause  interfaces ##################################*/


    interface _StaticSelectClause<SR extends Item> extends Item {

        //below one argument method

        SR select(NamedExpression exp);


        _AsClause<SR> select(Supplier<Expression> supplier);

        <I extends Item> I select(SqlFunction<_AliasExpression<SR>, SR, I> sqlFunction);


        //below two argument method

        SR select(FieldMeta<?> field1, FieldMeta<?> field2);


        <E extends RightOperand> _AsClause<SR> select(Function<E, Expression> expOperator, Supplier<E> supplier);

        <I extends Item> I select(SqlOneFunction<_AliasExpression<SR>, SR, I> sqlFunction, Expression exp);

        //below three argument method

        SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3);

        SR select(Expression exp, SQLs.WordAs as, String alias);

        SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

        SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias);

        SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        SR select(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field);

        SR select(Supplier<Expression> funcRef, SQLs.WordAs as, String alias);

        <I extends Item> I select(SqlTwoFunction<_AliasExpression<SR>, SR, I> sqlFunction, Expression arg1, Expression arg2);

        //below four argument method

        SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4);

        <T> SR select(Function<T, Expression> valueOperator, T value, SQLs.WordAs as, String alias);

        SR select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
                , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias);

        <I extends Item, T> I select(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc
                , ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, T operand);


        //below five argument method

        SR select(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

        <T> SR select(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias);


        <I extends Item> I select(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc
                , ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String keyName);

        //below six argument method

        SR select(FieldMeta<?> field1, SQLs.WordAs as1, String alias1
                , FieldMeta<?> field2, SQLs.WordAs as2, String alias2);

        SR select(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
                , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2);

        <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
                , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR select(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
                , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2);

        SR select(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period
                , FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR select(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period
                , String fieldAlias, SQLs.WordAs as, String alias);

        SR select(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
                , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2);

        SR select(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String keyName, SQLs.WordAs as, String alias);


    }


    interface _StaticSelectSpaceClause<SR extends Item> {

    }


    interface _ModifierSelectClause<W extends SelectModifier, SR extends Item> extends _StaticSelectClause<SR> {

        _StaticSelectSpaceClause<SR> select(W modifier);

    }

    interface _ModifierListSelectClause<W extends SelectModifier, SR extends Item> extends _ModifierSelectClause<W, SR> {

        _StaticSelectSpaceClause<SR> select(List<W> modifiers);

    }

    interface _HintsModifiersListSelectClause<W extends SelectModifier, SR extends Item> extends _ModifierListSelectClause<W, SR> {

        _StaticSelectSpaceClause<SR> select(Supplier<List<Hint>> hints, List<W> modifiers);
    }


    interface _StaticSelectCommaClause<SR extends Item> extends Item {

        //below one argument method

        SR comma(FieldMeta<?> field);


        _AsClause<SR> comma(Supplier<Expression> supplier);

        <I extends Item> I comma(Function<Function<Expression, _AsClause<SR>>, I> sqlFunc);

        //below two argument method

        SR comma(FieldMeta<?> field1, FieldMeta<?> field2);

        <I extends Item> I comma(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc, Expression expression);

        <E extends RightOperand> _AsClause<SR> comma(Function<E, Expression> expOperator, Supplier<E> supplier);

        //below three argument method

        SR comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3);

        SR comma(Expression exp, SQLs.WordAs as, String alias);

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias);

        SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        SR comma(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field);

        SR comma(Supplier<Expression> funcRef, SQLs.WordAs as, String alias);

        //below four argument method

        SR comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4);

        <T> SR comma(Function<T, Expression> valueOperator, T value, SQLs.WordAs as, String alias);

        SR comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
                , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias);

        //below five argument method

        SR comma(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

        <T> SR comma(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias);


        //below six argument method

        SR comma(FieldMeta<?> field1, SQLs.WordAs as1, String alias1
                , FieldMeta<?> field2, SQLs.WordAs as2, String alias2);

        SR comma(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
                , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2);

        <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
                , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR comma(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
                , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2);

        SR comma(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period
                , FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR comma(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period
                , String fieldAlias, SQLs.WordAs as, String alias);

        SR comma(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
                , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2);

        SR comma(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String keyName, SQLs.WordAs as, String alias);

        <I extends Item, T> I comma(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc
                , ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

        <I extends Item> I comma(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc
                , ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    }


    interface _SelectionsCommaSpec extends _StaticSelectCommaClause<_SelectionsCommaSpec> {

    }


    interface _DynamicSelectClause<SD> {

        SD select(SQLs.SymbolStar star);

        SD selects(Consumer<Selections> consumer);


    }

    interface _DynamicModifierSelectClause<W extends SelectModifier, SD> extends _DynamicSelectClause<SD> {

        SD select(W modifier, SQLs.SymbolStar star);

        SD selects(W modifier, Consumer<Selections> consumer);

    }


    interface _DynamicModifierListSelectClause<W extends SelectModifier, SD>
            extends _DynamicModifierSelectClause<W, SD> {

        SD select(List<W> modifierList, SQLs.SymbolStar star);

        SD selects(List<W> modifierList, Consumer<Selections> consumer);

    }

    interface _DynamicHintModifierSelectClause<W extends SelectModifier, SD>
            extends _DynamicModifierListSelectClause<W, SD> {

        SD select(Supplier<List<Hint>> hints, List<W> modifiers, SQLs.SymbolStar star);

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

        GR groupBy(SortItem sortItem);

        GR groupBy(SortItem sortItem1, SortItem sortItem2);

        GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        GR groupBy(Consumer<Consumer<SortItem>> consumer);

        GR ifGroupBy(Consumer<Consumer<SortItem>> consumer);

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

    interface _LockOfTableClause<OR> {

        OR of(TableMeta<?> table);

        OR of(TableMeta<?> table1, TableMeta<?> table2);

        OR of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3);

        OR of(Consumer<Consumer<TableMeta<?>>> consumer);

        OR ifOf(Consumer<Consumer<TableMeta<?>>> consumer);

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

    interface _RowSetUnionClause<S extends RowSet, UR> {

        UR union(Supplier<S> supplier);

        UR unionAll(Supplier<S> supplier);

        UR unionDistinct(Supplier<S> supplier);
    }

    interface _RowSetIntersectClause<S extends RowSet, UR> {

        UR intersect(Supplier<S> supplier);

        UR intersectAll(Supplier<S> supplier);

        UR intersectDistinct(Supplier<S> supplier);
    }

    interface _RowSetExceptClause<S extends RowSet, UR> {

        UR except(Supplier<S> supplier);

        UR exceptAll(Supplier<S> supplier);

        UR exceptDistinct(Supplier<S> supplier);
    }

    interface _RowSetMinusClause<S extends RowSet, UR> {

        UR minus(Supplier<S> supplier);

        UR minusAll(Supplier<S> supplier);

        UR minusDistinct(Supplier<S> supplier);
    }


    interface _SelectDispatcher<W extends SelectModifier, SR, SD>
            extends _HintsModifiersListSelectClause<W, SR>
            , _DynamicHintModifierSelectClause<W, SD> {

    }

    interface _SelectAndCommaDispatcher<SR> extends _StaticSelectClause<SR>
            , _StaticSelectCommaClause<SR> {

    }


    interface _WithSelectDispatcher<B extends CteBuilderSpec, WE, W extends SelectModifier, SR, SD>
            extends DialectStatement._DynamicWithClause<B, WE>
            , _SelectDispatcher<W, SR, SD> {

    }


}
