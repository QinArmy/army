package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.function.ExpressionOperator;
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


    interface _SelectClause<SR> extends Item {

        SR select(FieldMeta<?> field);

        SR select(FieldMeta<?> field1, FieldMeta<?> field2);

        SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3);

        SR select(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4);

        SR select(Expression exp, SQLs.WordAs as, String alias);

        SR select(Expression exp1, SQLs.WordAs as1, String alias1, Expression exp2, SQLs.WordAs as2, String alias2);

        SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias);

        SR select(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

        SR select(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1, String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2);

        SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent, String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);


        SR select(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field);

        SR select(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1, String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2);

        _AsClause<SR> select(Supplier<Expression> supplier);

        SR select(Supplier<Expression> funcRef, SQLs.WordAs as, String alias);

        SR select(Function<Expression, Expression> funcRef, FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR select(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR select(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

        SR select(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1, Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2);

        <I extends Item> I select(Function<Function<Expression, _AsClause<SR>>, I> sqlFunc);

        <I extends Item> I select(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc, Expression expression);

        <E extends RightOperand> _AsClause<SR> select(Function<E, Expression> expOperator, Supplier<E> supplier);

        SR select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator, BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias);

        <T> SR select(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias);

        <T> SR select(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias);

        SR select(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName, SQLs.WordAs as, String alias);

    }


    interface _SelectCommaClause<SR> {

        SR comma(FieldMeta<?> field);

        SR comma(FieldMeta<?> field1, FieldMeta<?> field2);

        SR comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3);

        SR comma(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4);

        SR comma(Expression exp, SQLs.WordAs as, String alias);

        SR comma(Expression exp1, SQLs.WordAs as1, String alias1, Expression exp2, SQLs.WordAs as2, String alias2);

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias);

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

        SR comma(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1, String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2);

        SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent, String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);


        SR comma(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field);

        SR comma(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1, String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2);

        _AsClause<SR> comma(Supplier<Expression> supplier);

        SR comma(Supplier<Expression> funcRef, SQLs.WordAs as, String alias);

        SR comma(Function<Expression, Expression> funcRef, FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR comma(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias);

        SR comma(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

        SR comma(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1, Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2);

        <I extends Item> I comma(Function<Function<Expression, _AsClause<SR>>, I> sqlFunc);

        <I extends Item> I comma(BiFunction<Expression, Function<Expression, _AsClause<SR>>, I> sqlFunc, Expression expression);

        <E extends RightOperand> _AsClause<SR> comma(Function<E, Expression> expOperator, Supplier<E> supplier);

        SR comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator, BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias);

        <T> SR comma(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias);

        <T> SR comma(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias);

        SR comma(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName, SQLs.WordAs as, String alias);

    }


    interface _SelectCommaSpec extends _SelectCommaClause<_SelectCommaSpec> {

    }


    interface _DynamicSelectClause<SR, SD> extends _SelectClause<SR> {

        SD select(SQLs.SymbolStar star);

        SD selects(Consumer<Selections> consumer);


    }

    interface _DynamicModifierSelectClause<W extends SelectModifier, SR, SD> extends _DynamicSelectClause<SR, SD> {

        SD select(W modifier, SQLs.SymbolStar star);

        SD selects(W modifier, Consumer<Selections> consumer);

    }

    interface _DynamicHintModifierSelectClause<W extends SelectModifier, SR, SD>
            extends _DynamicModifierSelectClause<W, SR, SD> {

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


    interface _WithSelectDispatcher<B extends CteBuilderSpec, WE, W extends SelectModifier, SR, SD>
            extends DialectStatement._DynamicWithClause<B, WE>
            , _DynamicHintModifierSelectClause<W, SR, SD> {

    }


}
