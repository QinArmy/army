package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.function.*;


/**
 * <p>
 * This interface representing postgre SELECT statement.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
 * @since 1.0
 */
public interface PostgreQuery extends Query, DialectStatement {

    interface _PostgreDynamicWithClause<SR>
            extends _DynamicWithCteClause<PostgreCteBuilder, SR> {

    }

    interface _PostgreLockClause<R> extends _MinLockOptionClause<R> {

        R forNoKeyUpdate();

        R forKeyShare();

        R ifForNoKeyUpdate(BooleanSupplier supplier);

        R ifForKeyShare(BooleanSupplier supplier);

    }


    interface _PostgreFetchClause<R> {


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
        R fetch(FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , long count, FetchRow row, FetchOnlyWithTies onlyWithTies);

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
        <N extends Number> R fetch(FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, FetchRow row, FetchOnlyWithTies onlyWithTies);

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
        R fetch(FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Function<String, ?> function, String keyName, FetchRow row, FetchOnlyWithTies onlyWithTies);

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
        R ifFetch(FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , @Nullable Number count, FetchRow row, FetchOnlyWithTies onlyWithTies);

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
        <N extends Number> R ifFetch(FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, FetchRow row, FetchOnlyWithTies onlyWithTies);

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
        R ifFetch(FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Function<String, ?> function, String keyName, FetchRow row, FetchOnlyWithTies onlyWithTies);
    }


    interface _PostgreOffsetClause<R> {


        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R offset(BiFunction<MappingType, Number, Expression> operator, long start, FetchRow row);


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
                , FetchRow row);

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
                , String keyName, FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                      <li>{@link SQLs#param(MappingType, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R ifOffset(BiFunction<MappingType, Number, Expression> operator, @Nullable Number start, FetchRow row);

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
                , FetchRow row);

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
                , String keyName, FetchRow row);

    }


    interface _PostgreLimitClause<LR> extends _RowCountLimitClause<LR> {

        LR limitAll();

        LR ifLimitAll(BooleanSupplier supplier);

    }

    interface _UnionSpec<I extends Item> extends _QueryUnionClause<_UnionAndQuerySpec<I>>
            , _QueryIntersectClause<_UnionAndQuerySpec<I>>
            , _QueryExceptClause<_UnionAndQuerySpec<I>>
            , _QuerySpec<I> {

    }


    interface _UnionAndQuerySpec<I extends Item> extends _WithCteSpec<I>
            , Query._LeftParenClause<_UnionAndQuerySpec<Statement._RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _UnionLockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_UnionLockSpec<I>>
            , _UnionLockSpec<I> {


    }

    interface _UnionLockOfTableSpec<I extends Item> extends _LockOfTableClause<_UnionLockWaitOptionSpec<I>>
            , _UnionLockWaitOptionSpec<I> {


    }


    interface _UnionLockSpec<I extends Item> extends _PostgreLockClause<_UnionLockOfTableSpec<I>>, _QuerySpec<I> {

    }

    interface _UnionFetchSpec<I extends Item> extends _PostgreFetchClause<_UnionLockSpec<I>>, _UnionLockSpec<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _PostgreOffsetClause<_UnionLockSpec<I>>, _UnionLockSpec<I> {


    }

    interface _UnionLimitSpec<I extends Item> extends _PostgreLimitClause<_UnionOffsetSpec<I>>, _UnionOffsetSpec<I> {

        @Override
        _UnionFetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator, long start, FetchRow row);

        @Override
        <N extends Number> _UnionFetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, FetchRow row);

        @Override
        _UnionFetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
                , String keyName, FetchRow row);

        @Override
        _UnionFetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator, @Nullable Number start
                , FetchRow row);

        @Override
        <N extends Number> _UnionFetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, FetchRow row);

        @Override
        _UnionFetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
                , String keyName, FetchRow row);
    }

    interface _UnionOrderBySpec<I extends Item> extends Statement._OrderByClause<_UnionLimitSpec<I>>
            , _UnionLimitSpec<I>, _UnionSpec<I> {

    }


    interface _LockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_LockSpec<I>>, _LockSpec<I> {


    }


    interface _LockOfTableSpec<I extends Item> extends _LockOfTableClause<_LockWaitOptionSpec<I>>
            , _LockWaitOptionSpec<I> {


    }


    interface _LockSpec<I extends Item> extends _PostgreLockClause<_LockOfTableSpec<I>>, _QuerySpec<I> {


    }


    interface _FetchSpec<I extends Item> extends _PostgreFetchClause<_LockSpec<I>>, _LockSpec<I> {


    }


    interface _OffsetSpec<I extends Item> extends _PostgreOffsetClause<_LockSpec<I>>, _LockSpec<I> {


    }


    interface _LimitSpec<I extends Item> extends _PostgreLimitClause<_OffsetSpec<I>>, _OffsetSpec<I> {


        @Override
        _FetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator, long start, FetchRow row);

        @Override
        <N extends Number> _FetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, FetchRow row);

        @Override
        _FetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
                , String keyName, FetchRow row);

        @Override
        _FetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator, @Nullable Number start
                , FetchRow row);

        @Override
        <N extends Number> _FetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, FetchRow row);

        @Override
        _FetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
                , String keyName, FetchRow row);


    }


    interface _OrderBySpec<I extends Item> extends _OrderByClause<_LimitSpec<I>>, _LimitSpec<I>, _UnionSpec<I> {

    }


    interface _HavingSpec<I extends Item> extends _HavingClause<_OrderBySpec<I>>, _OrderBySpec<I> {

    }


    interface _GroupBySpec<I extends Item> extends _GroupClause<_HavingSpec<I>>
            , _OrderBySpec<I> {

    }

    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>, _GroupBySpec<I> {

    }

    interface _WhereSpec<I extends Item> extends _QueryWhereClause<_GroupBySpec<I>, _WhereAndSpec<I>>
            , _GroupBySpec<I> {

    }

    interface _RepeatableClause<RR> {

        RR repeatable(Expression seed);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Number seedValue);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Function<String, ?> function, String keyName);

        RR repeatable(Supplier<Expression> supplier);

        RR ifRepeatable(Supplier<Expression> supplier);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, @Nullable Number seedValue);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier);

        RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _TableSampleClause<TR> {

        TR tableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, T argument);

        <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier);

        TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        TR ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer);

        <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, T argument);

        <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier);

        TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _RepeatableOnClause<I extends Item> extends _RepeatableClause<_OnClause<_JoinSpec<I>>>
            , _OnClause<_JoinSpec<I>> {

    }

    interface _TableSampleOnSpec<I extends Item> extends _TableSampleClause<_RepeatableOnClause<I>>
            , _OnClause<_JoinSpec<I>> {

    }


    interface _RepeatableJoinClause<I extends Item> extends _RepeatableClause<_JoinSpec<I>>, _JoinSpec<I> {

    }


    interface _TableSampleJoinSpec<I extends Item> extends _TableSampleClause<_RepeatableJoinClause<I>>, _JoinSpec<I> {

    }


    interface _JoinSpec<I extends Item> extends _JoinModifierClause<_TableSampleOnSpec<I>, _OnClause<_JoinSpec<I>>>
            , _CrossJoinModifierClause<_TableSampleJoinSpec<I>, _JoinSpec<I>>
            , _JoinCteClause<_OnClause<_JoinSpec<I>>>, _CrossJoinCteClause<_JoinSpec<I>>
            , _WhereSpec<I> {

    }


    interface _PostgreFromClause<FT, FS> extends _FromModifierClause<FT, FS>
            , _FromModifierCteClause<FS> {

    }


    interface _FromSpec<I extends Item> extends _PostgreFromClause<_TableSampleJoinSpec<I>, _JoinSpec<I>>
            , _UnionSpec<I> {

        //TODO function Tabular item
    }


    interface _PostgreSelectClause<I extends Item> extends _SelectClause<_FromSpec<I>>
            , _DynamicModifierSelectClause<Postgres.SelectModifier, _FromSpec<I>> {


    }

    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _PostgreSelectClause<I> {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item> extends _PostgreSelectClause<I> {

    }

    interface _StaticCteAsClause<I extends Item>
            extends Statement._StaticAsClaus<_StaticCteComplexCommandSpec<I>> {

    }

    interface _StaticCteLeftParenSpec<I extends Item>
            extends Statement._LeftParenStringQuadraSpec<_StaticCteAsClause<I>>
            , _StaticCteAsClause<I> {

    }


    /**
     * <p>
     * primary-statement syntax support static WITH clause,it's simple and clear and free
     * </p>
     *
     * @since 1.0
     */
    interface _WithCteSpec<I extends Item> extends _PostgreDynamicWithClause<_PostgreSelectClause<I>>
            , _StaticWithCteClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _PostgreSelectClause<I> {

    }


    /**
     * <p>
     * sub-statement syntax forbid static WITH syntax,because it destroy the simpleness of SQL.
     * </p>
     *
     * @since 1.0
     */
    interface _SubWithCteSpec<I extends Item> extends _PostgreDynamicWithClause<_PostgreSelectClause<I>>
            , _PostgreSelectClause<I> {

    }


}
