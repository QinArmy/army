package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
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

    interface SelectStar {

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


    interface _DynamicWithClause<B extends CteBuilderSpec, WE> extends Item {
        WE with(Consumer<B> consumer);

        WE withRecursive(Consumer<B> consumer);

        WE ifWith(Consumer<B> consumer);

        WE ifWithRecursive(Consumer<B> consumer);

    }

    interface _StaticWithClause<WS> {

        WS with(String name);

        WS withRecursive(String name);

    }


    interface _StaticWithCommaClause<CR> extends Item {

        CR comma(String name);
    }


    interface _LeftParenClause<LR> {

        LR leftParen();
    }


    /*################################## blow select clause  interfaces ##################################*/


    interface _SelectClause<SR> {

        SR select(SelectItem selectItem);

        SR select(SelectItem selectItem1, SelectItem selectItem2);

        SR select(SelectItem selectItem1, SelectItem selectItem2, SelectItem selectItem3);


    }


    interface _DynamicSelectClause<SR> extends _SelectClause<SR> {

        SR select(Consumer<Consumer<SelectItem>> consumer);


    }

    interface _DynamicModifierSelectClause<W extends SelectModifier, SR> extends _DynamicSelectClause<SR> {

        SR select(W modifier, Consumer<Consumer<SelectItem>> consumer);

    }

    interface _DynamicHintModifierSelectClause<W extends SelectModifier, SR>
            extends _DynamicModifierSelectClause<W, SR> {

        SR select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Consumer<SelectItem>> consumer);

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


    interface _QueryOffsetClause<R> {


        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R offset(BiFunction<MappingType, Number, Expression> operator, long start, Query.FetchRow row);


        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param supplier return non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        <N extends Number> R offset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
                , Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param function {@link Function#apply(Object)} return non-negative integer
         * @param keyName  keyName that is passed to function
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R offset(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
                , String keyName, Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R ifOffset(BiFunction<MappingType, Number, Expression> operator, @Nullable Number start, Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param supplier return non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        <N extends Number> R ifOffset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
                , Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param function {@link Function#apply(Object)} return non-negative integer
         * @param keyName  keyName that is passed to function
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R ifOffset(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
                , String keyName, Query.FetchRow row);

    }


    interface _QueryFetchClause<R> {

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, Expression count, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);


        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , long count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param supplier     return non-negative integer
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-negative integer
         * @param keyName      keyName that is passed to function
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , @Nullable Number count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param supplier     return non-negative integer
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-negative integer
         * @param keyName      keyName that is passed to function
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);
    }


    interface _LimitClause<LR> extends Statement._RowCountLimitClause<LR> {

        LR limit(Expression offset, Expression rowCount);

        LR limit(BiFunction<MappingType, Number, Expression> operator, long offset, long rowCount);

        <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR limit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR limit(Consumer<BiConsumer<Expression, Expression>> consumer);


        <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR ifLimit(Consumer<BiConsumer<Expression, Expression>> consumer);

    }


    interface _StaticSpaceClause<SR> {

        SR space();
    }



}
