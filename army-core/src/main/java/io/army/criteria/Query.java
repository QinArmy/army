package io.army.criteria;


import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.function.*;
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

    interface _SelectClauseOfQuery extends Item {
        //NO method
    }


    interface _StaticSelectClause<SR extends Item> extends _SelectClauseOfQuery {

        //below one argument method

        SR select(NamedExpression exp);


        _AliasExpression<SR> select(Supplier<Expression> supplier);


        <R extends Item> R select(SqlFunction<_AliasExpression<SR>, SR, R> function);

        //below two argument method

        SR select(NamedExpression exp1, NamedExpression exp2);

        <T> _AliasExpression<SR> select(Function<T, Expression> operator, Supplier<T> supplier);

        _AliasExpression<SR> select(Function<Expression, Expression> operator, Expression exp);

        _AliasExpression<SR> select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                    BiFunction<DataField, String, Expression> namedOperator);

        <R extends Item> R select(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Expression exp);

        <R extends Item> R select(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Supplier<Expression> supplier);

        //below three argument method

        SR select(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

        SR select(Expression exp, SQLs.WordAs as, String alias);

        SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

        SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        SR select(Supplier<Expression> supplier, SQLs.WordAs as, String alias);

        <T> _AliasExpression<SR> select(ExpressionOperator<Expression, T, Expression> expOperator,
                                        BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

        <R extends Item> R select(SqlTwoFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                  Expression exp2);

        //below four argument method

        SR select(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);

        <T> SR select(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as, String alias);

        SR select(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as, String alias);

        SR select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                  BiFunction<DataField, String, Expression> namedOperator,
                  SQLs.WordAs as, String alias);

        _AliasExpression<SR> select(ExpressionOperator<Expression, Object, Expression> expOperator,
                                    BiFunction<Expression, Object, Expression> operator,
                                    Function<String, ?> function, String keyName);


        <R extends Item> R select(SqlThreeFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                  Expression exp2, Expression exp3);


        //below five argument method


        //below six argument method

        SR select(NamedExpression exp1, SQLs.WordAs as1, String alias1,
                  NamedExpression exp2, SQLs.WordAs as2, String alias2);

        <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                      String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR select(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                  Supplier<Expression> function2, SQLs.WordAs as2, String alias2);

        SR select(ExpressionOperator<Expression, Object, Expression> expOperator,
                  BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                  String keyName, SQLs.WordAs as, String alias);

    }


    interface _StaticSelectSpaceClause<SR extends Item> {

        //below one argument method

        SR space(NamedExpression exp);


        _AliasExpression<SR> space(Supplier<Expression> supplier);


        <R extends Item> R space(SqlFunction<_AliasExpression<SR>, SR, R> function);

        //below two argument method

        SR space(NamedExpression exp1, NamedExpression exp2);

        <T> _AliasExpression<SR> space(Function<T, Expression> operator, Supplier<T> supplier);

        _AliasExpression<SR> space(Function<Expression, Expression> operator, Expression exp);

        _AliasExpression<SR> space(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                   BiFunction<DataField, String, Expression> namedOperator);

        <R extends Item> R space(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Expression exp);

        <R extends Item> R space(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Supplier<Expression> supplier);

        //below three argument method

        SR space(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

        SR space(Expression exp, SQLs.WordAs as, String alias);

        SR space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

        SR space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        SR space(Supplier<Expression> supplier, SQLs.WordAs as, String alias);

        <T> _AliasExpression<SR> space(ExpressionOperator<Expression, T, Expression> expOperator,
                                       BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

        <R extends Item> R space(SqlTwoFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                 Expression exp2);

        //below four argument method

        SR space(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);

        <T> SR space(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as, String alias);

        SR space(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as, String alias);

        SR space(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                 BiFunction<DataField, String, Expression> namedOperator,
                 SQLs.WordAs as, String alias);

        _AliasExpression<SR> space(ExpressionOperator<Expression, Object, Expression> expOperator,
                                   BiFunction<Expression, Object, Expression> operator,
                                   Function<String, ?> function, String keyName);


        <R extends Item> R space(SqlThreeFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                 Expression exp2, Expression exp3);


        //below five argument method


        //below six argument method

        SR space(NamedExpression exp1, SQLs.WordAs as1, String alias1,
                 NamedExpression exp2, SQLs.WordAs as2, String alias2);

        <P> SR space(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                     String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR space(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                 Supplier<Expression> function2, SQLs.WordAs as2, String alias2);

        SR space(ExpressionOperator<Expression, Object, Expression> expOperator,
                 BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                 String keyName, SQLs.WordAs as, String alias);
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

        //below one argument method

        SR comma(NamedExpression exp);


        _AliasExpression<SR> comma(Supplier<Expression> supplier);


        <R extends Item> R comma(SqlFunction<_AliasExpression<SR>, SR, R> function);

        //below two argument method

        SR comma(NamedExpression exp1, NamedExpression exp2);

        <T> _AliasExpression<SR> comma(Function<T, Expression> operator, Supplier<T> supplier);

        _AliasExpression<SR> comma(Function<Expression, Expression> operator, Expression exp);

        _AliasExpression<SR> comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                   BiFunction<DataField, String, Expression> namedOperator);

        <R extends Item> R comma(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Expression exp);

        <R extends Item> R comma(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Supplier<Expression> supplier);

        //below three argument method

        SR comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

        SR comma(Expression exp, SQLs.WordAs as, String alias);

        SR comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

        SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        SR comma(Supplier<Expression> supplier, SQLs.WordAs as, String alias);

        <T> _AliasExpression<SR> comma(ExpressionOperator<Expression, T, Expression> expOperator,
                                       BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

        <R extends Item> R comma(SqlTwoFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                 Expression exp2);

        //below four argument method

        SR comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);

        <T> SR comma(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as, String alias);

        SR comma(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as, String alias);

        SR comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                 BiFunction<DataField, String, Expression> namedOperator,
                 SQLs.WordAs as, String alias);

        _AliasExpression<SR> comma(ExpressionOperator<Expression, Object, Expression> expOperator,
                                   BiFunction<Expression, Object, Expression> operator,
                                   Function<String, ?> function, String keyName);


        <R extends Item> R comma(SqlThreeFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                 Expression exp2, Expression exp3);


        //below five argument method


        //below six argument method

        SR comma(NamedExpression exp1, SQLs.WordAs as1, String alias1,
                 NamedExpression exp2, SQLs.WordAs as2, String alias2);

        <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                     String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

        SR comma(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                 Supplier<Expression> function2, SQLs.WordAs as2, String alias2);

        SR comma(ExpressionOperator<Expression, Object, Expression> expOperator,
                 BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                 String keyName, SQLs.WordAs as, String alias);
    }


    interface _SelectionsCommaSpec extends _StaticSelectCommaClause<_SelectionsCommaSpec> {

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

    interface _LeftParenRowSetClause<R> {


        <S extends RowSet> R leftParen(Supplier<S> supplier);

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
