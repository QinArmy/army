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
            extends _DynamicWithClause<PostgreCteBuilder, SR> {

    }

    interface _PostgreOrderByClause<OR> extends _OrderByClause<OR> {
        //TODO add dialect method
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


    interface _FrameExclusionClause<R> {

        R excludeCurrentRow();

        R excludeGroup();

        R excludeTies();

        R excludeNoOthers();

        R ifExcludeCurrentRow(BooleanSupplier supplier);

        R ifExcludeGroup(BooleanSupplier supplier);

        R ifExcludeTies(BooleanSupplier supplier);

        R ifExcludeNoOthers(BooleanSupplier supplier);

    }


    interface _RepeatableClause<RR> {

        RR repeatable(Expression seed);

        RR repeatable(Supplier<Expression> supplier);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Number seedValue);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier);

        RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Function<String, ?> function, String keyName);

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

    interface _CteMaterializedClause<R> {

        R materialized();

        R notMaterialized();

        R ifMaterialized(BooleanSupplier predicate);

        R ifNotMaterialized(BooleanSupplier predicate);
    }

    interface _FrameExclusionSpec<I extends Item>
            extends _FrameExclusionClause<_RightParenClause<I>>, _RightParenClause<I> {

    }

    interface _PostgreFrameEndNonExpBoundClause<I extends Item> extends Window._FrameNonExpBoundClause {

        @Override
        _FrameExclusionSpec<I> currentRow();

        @Override
        _FrameExclusionSpec<I> unboundedPreceding();

        @Override
        _FrameExclusionSpec<I> unboundedFollowing();
    }

    interface _PostgreFrameEndExpBoundClause<I extends Item> extends Window._FrameExpBoundClause {

        @Override
        _FrameExclusionSpec<I> preceding();

        @Override
        _FrameExclusionSpec<I> following();
    }

    interface _PostgreFrameBetweenAndClause<I extends Item>
            extends Window._FrameBetweenAndExpClause<_PostgreFrameEndExpBoundClause<I>>
            , _StaticAndClause<_PostgreFrameEndNonExpBoundClause<I>> {

    }


    interface _PostgreFrameStartNonExpBoundClause<I extends Item> extends Window._FrameNonExpBoundClause {

        @Override
        _PostgreFrameBetweenAndClause<I> currentRow();

        @Override
        _PostgreFrameBetweenAndClause<I> unboundedPreceding();

        @Override
        _PostgreFrameBetweenAndClause<I> unboundedFollowing();
    }

    interface _PostgreFrameStartExpBoundClause<I extends Item> extends Window._FrameExpBoundClause {

        @Override
        _PostgreFrameBetweenAndClause<I> preceding();

        @Override
        _PostgreFrameBetweenAndClause<I> following();

    }

    interface _PostgreFrameBetweenSpec<I extends Item>
            extends Window._FrameBetweenExpClause<_PostgreFrameStartExpBoundClause<I>>
            , _StaticBetweenClause<_PostgreFrameStartNonExpBoundClause<I>>
            , _PostgreFrameStartNonExpBoundClause<I> {

    }

    interface _PostgreFrameUnitSpec<I extends Item>
            extends Window._FrameUnitExpClause<_PostgreFrameEndExpBoundClause<I>>
            , Window._FrameUnitNoExpClause<_PostgreFrameBetweenSpec<I>>
            , Statement._RightParenClause<I> {

        _PostgreFrameBetweenSpec<I> groups();

        _PostgreFrameBetweenSpec<I> ifGroups(BooleanSupplier predicate);

        _PostgreFrameEndExpBoundClause<I> groups(Expression expression);

        _PostgreFrameEndExpBoundClause<I> groups(Supplier<Expression> supplier);

        <E> _PostgreFrameEndExpBoundClause<I> groups(Function<E, Expression> valueOperator, @Nullable E value);

        <E> _PostgreFrameEndExpBoundClause<I> groups(Function<E, Expression> valueOperator, Supplier<E> supplier);

        _PostgreFrameEndExpBoundClause<I> groups(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        _PostgreFrameEndExpBoundClause<I> ifGroups(Supplier<Expression> supplier);

        <E> _PostgreFrameEndExpBoundClause<I> ifGroups(Function<E, Expression> valueOperator, @Nullable E value);

        <E> _PostgreFrameEndExpBoundClause<I> ifGroups(Function<E, Expression> valueOperator, Supplier<E> supplier);

        _PostgreFrameEndExpBoundClause<I> ifGroups(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _WindowOrderBySpec<I extends Item> extends _PostgreOrderByClause<_PostgreFrameUnitSpec<I>>
            , _PostgreFrameUnitSpec<I> {

    }

    interface _WindowPartitionBySpec<I extends Item> extends Window._PartitionByExpClause<_WindowOrderBySpec<I>>
            , _WindowOrderBySpec<I> {

    }

    interface _WindowLeftParenClause<I extends Item>
            extends Window._LeftParenNameClause<_WindowPartitionBySpec<I>> {

    }

    interface _WindowAsClause<I extends Item> extends Statement._StaticAsClaus<_WindowLeftParenClause<I>> {

    }


    interface _PostgreJoinClause<JT, JS> extends _JoinModifierClause<JT, JS>
            , _JoinCteClause<JS> {

    }

    interface _PostgreCrossClause<FT, FS> extends _CrossJoinModifierClause<FT, FS>
            , _CrossJoinCteClause<FS> {

    }


    interface _PostgreDynamicJoinClause<JD> extends _DynamicJoinClause<PostgreJoins, JD> {

    }

    interface _PostgreDynamicCrossJoinClause<JD> extends _DynamicCrossJoinClause<PostgreCrosses, JD> {

    }


    interface _PostgreNestedJoinClause<I extends Item>
            extends _PostgreJoinClause<_NestedIndexHintOnSpec<I>, _NestedOnSpec<I>>
            , _PostgreCrossClause<_NestedIndexHintCrossSpec<I>, _NestedJoinSpec<I>>
            , _JoinNestedClause<_NestedLeftParenSpec<_NestedOnSpec<I>>>
            , _CrossJoinNestedClause<_NestedLeftParenSpec<_NestedJoinSpec<I>>>
            , _PostgreDynamicJoinClause<_NestedJoinSpec<I>>
            , _PostgreDynamicCrossJoinClause<_NestedJoinSpec<I>> {

    }


    interface _NestedJoinSpec<I extends Item> extends _PostgreNestedJoinClause<I>
            , _RightParenClause<I> {

    }

    interface _NestedOnSpec<I extends Item> extends _OnClause<_NestedJoinSpec<I>>, _NestedJoinSpec<I> {

    }

    interface _NestedIndexHintOnSpec<I extends Item> extends _QueryIndexHintClause<_NestedIndexHintOnSpec<I>>
            , _NestedOnSpec<I> {

    }

    interface _NestedPartitionOnSpec<I extends Item> extends _PartitionAndAsClause<_NestedIndexHintOnSpec<I>> {

    }

    interface _NestedIndexHintCrossSpec<I extends Item> extends _QueryIndexHintClause<_NestedIndexHintCrossSpec<I>>
            , _NestedJoinSpec<I> {

    }

    interface _NestedPartitionCrossSpec<I extends Item> extends _PartitionAndAsClause<_NestedIndexHintCrossSpec<I>> {

    }

    interface _NestedIndexHintJoinSpec<I extends Item> extends _QueryIndexHintClause<_NestedIndexHintJoinSpec<I>>
            , _MySQLNestedJoinClause<I> {

    }

    interface _NestedPartitionJoinSpec<I extends Item> extends _PartitionAndAsClause<_NestedIndexHintJoinSpec<I>> {

    }

    interface _MySQLNestedLeftParenClause<LT, LS> extends _LeftParenModifierTabularClause<LT, LS>
            , _LeftParenCteClause<LS> {

    }

    interface _NestedLeftParenSpec<I extends Item>
            extends _MySQLNestedLeftParenClause<_NestedIndexHintJoinSpec<I>, _MySQLNestedJoinClause<I>>
            , _NestedDialectLeftParenClause<_NestedPartitionJoinSpec<I>>
            , _LeftParenClause<_NestedLeftParenSpec<_MySQLNestedJoinClause<I>>> {

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

    interface _UnionOrderBySpec<I extends Item> extends _PostgreOrderByClause<_UnionLimitSpec<I>>
            , _UnionLimitSpec<I>, _UnionSpec<I> {

    }


    interface _LockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_LockSpec<I>>, _LockSpec<I> {//TODO validate multi-lock clause


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


    interface _OrderBySpec<I extends Item> extends _PostgreOrderByClause<_LimitSpec<I>>, _LimitSpec<I>, _UnionSpec<I> {

    }

    interface _WindowCommaSpec<I extends Item>
            extends Window._StaticWindowCommaClause<_WindowAsClause<_WindowCommaSpec<I>>>
            , _OrderBySpec<I> {

    }

    interface _WindowSpec<I extends Item> extends Window._DynamicWindowClause<PostgreWindows, _OrderBySpec<I>>
            , Window._StaticWindowClause<_WindowAsClause<_WindowCommaSpec<I>>>
            , _OrderBySpec<I> {

    }


    interface _HavingSpec<I extends Item> extends _HavingClause<_WindowSpec<I>>, _WindowSpec<I> {

    }


    interface _GroupBySpec<I extends Item> extends _GroupByClause<_HavingSpec<I>>
            , _WindowSpec<I> {
        //TODO add dialect method
    }

    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>, _GroupBySpec<I> {

    }

    interface _WhereSpec<I extends Item> extends _QueryWhereClause<_GroupBySpec<I>, _WhereAndSpec<I>>
            , _GroupBySpec<I> {

    }


    interface _RepeatableOnClause<I extends Item> extends _RepeatableClause<_OnClause<_JoinSpec<I>>>
            , _OnClause<_JoinSpec<I>> {

    }

    interface _TableSampleOnSpec<I extends Item> extends _TableSampleClause<_RepeatableOnClause<I>>
            , _OnClause<_JoinSpec<I>> {

    }


    interface _JoinFunctionClause<JF> {
        //TODO add dialect function tabular
    }


    interface _JoinSpec<I extends Item> extends _JoinModifierClause<_TableSampleOnSpec<I>, _OnClause<_JoinSpec<I>>>
            , _CrossJoinModifierClause<_TableSampleJoinSpec<I>, _JoinSpec<I>>
            , _JoinCteClause<_OnClause<_JoinSpec<I>>>, _CrossJoinCteClause<_JoinSpec<I>>
            , _WhereSpec<I> {

        //TODO add dialect function tabular
    }

    interface _RepeatableJoinClause<I extends Item> extends _RepeatableClause<_JoinSpec<I>>, _JoinSpec<I> {

    }


    interface _TableSampleJoinSpec<I extends Item> extends _TableSampleClause<_RepeatableJoinClause<I>>, _JoinSpec<I> {

    }


    interface _PostgreFromClause<FT, FS> extends _FromModifierClause<FT, FS>
            , _FromModifierCteClause<FS> {
        //TODO add dialect function tabular
    }


    interface _FromSpec<I extends Item> extends _PostgreFromClause<_TableSampleJoinSpec<I>, _JoinSpec<I>>
            , _FromNestedClause<_NestedLeftParenSpec<_JoinSpec<I>>>
            , _UnionSpec<I> {

    }


    interface _PostgreSelectClause<I extends Item> extends _SelectClause<_FromSpec<I>>
            , _DynamicModifierSelectClause<Postgres.SelectModifier, _FromSpec<I>> {


    }

    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _PostgreSelectClause<I> {

    }

    interface _CyclePathColumnClause<I extends Item> {

        _CteSpec<I> using(String cyclePathColumnName);

    }

    interface _CycleToMarkValueSpec<I extends Item> extends _CyclePathColumnClause<I> {

        _CyclePathColumnClause<I> to(Expression cycleMarkValue, SQLs.WordDefault wordDefault, Expression cycleMarkDefault);

        _CyclePathColumnClause<I> to(Consumer<BiConsumer<Expression, Expression>> consumer);

        _CyclePathColumnClause<I> ifTo(Consumer<BiConsumer<Expression, Expression>> consumer);

    }

    interface _SetCycleMarkColumnClause<I extends Item> {

        _CycleToMarkValueSpec<I> set(String cycleMarkColumnName);
    }

    interface _CteCycleSpec<I extends Item> extends _CteSpec<I> {

        _SetCycleMarkColumnClause<I> cycle(String columnName);

        _SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2);

        _SetCycleMarkColumnClause<I> cycle(String columnName1, String columnName2, String columnName3);

        _CommaStringQuadraSpec<_SetCycleMarkColumnClause<I>> cycle(String columnName1, String columnName2, String columnName3, String columnName4);

        _SetCycleMarkColumnClause<I> cycle(Consumer<Consumer<String>> consumer);

        _SetCycleMarkColumnClause<I> ifCycle(Consumer<Consumer<String>> consumer);


    }


    interface _SetSearchSeqColumnClause<I extends Item> {

        _CteCycleSpec<I> set(String searchSeqColumnName);

    }

    interface _SearchFirstByClause<I extends Item> {

        _SetSearchSeqColumnClause<I> firstBy(String columnName);

        _SetSearchSeqColumnClause<I> firstBy(String columnName1, String columnName2);

        _SetSearchSeqColumnClause<I> firstBy(String columnName1, String columnName2, String columnName3);

        _CommaStringQuadraSpec<_SetSearchSeqColumnClause<I>> firstBy(String columnName1, String columnName2, String columnName3, String columnName4);

        _SetSearchSeqColumnClause<I> firstBy(Consumer<Consumer<String>> consumer);

        _SetSearchSeqColumnClause<I> ifFirstBy(Consumer<Consumer<String>> consumer);

    }

    interface _CteSearchSpec<I extends Item> extends _CteCycleSpec<I> {

        _SearchFirstByClause<I> searchBreadth();

        _SearchFirstByClause<I> searchDepth();

        _SearchFirstByClause<I> searchBreadth(BooleanSupplier predicate);

        _SearchFirstByClause<I> searchDepth(BooleanSupplier predicate);

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item> extends _PostgreSelectClause<_CteSearchSpec<I>>
            , PostgreInsert._SubInsertIntoClause<_CteSpec<I>, _CteSpec<I>> {

    }


    interface _StaticCteMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_StaticCteComplexCommandSpec<I>>
            , _StaticCteComplexCommandSpec<I> {

    }

    interface _StaticCteAsClause<I extends Item>
            extends Statement._StaticAsClaus<_StaticCteMaterializedSpec<I>> {

    }

    interface _StaticCteLeftParenSpec<I extends Item>
            extends Statement._LeftParenStringQuadraSpec<_StaticCteAsClause<I>>
            , _StaticCteAsClause<I> {

    }

    interface _MinWithSpec<I extends Item> extends _PostgreDynamicWithClause<_PostgreSelectClause<I>>
            , _PostgreSelectClause<I> {

    }

    /**
     * <p>
     * primary-statement syntax support static WITH clause,it's simple and clear and free
     * </p>
     *
     * @since 1.0
     */
    interface _WithCteSpec<I extends Item> extends _MinWithSpec<I>
            , _StaticWithClause<_StaticCteLeftParenSpec<_CteComma<I>>> {

    }


}
