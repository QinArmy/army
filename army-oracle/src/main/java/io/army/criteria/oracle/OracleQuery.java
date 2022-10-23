package io.army.criteria.oracle;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing Oracle SELECT syntax.
 * </p>
 *
 * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6">Oracle SELECT syntax</a>
 * @since 1.0
 */
public interface OracleQuery extends Query, OracleStatement {

    /**
     * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6">Lock wait syntax</a>
     */
    interface _OracleLockWaitOptionClause<LR> extends _MinLockOptionClause<LR> {

        LR wait(int seconds);

        LR wait(Supplier<Integer> supplier);

        LR ifWait(Supplier<Integer> supplier);
    }


    /**
     * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6">Lock of colunn syntax</a>
     */
    interface _OracleLockOfColumnClause<OR> {

        OR of(TableField field);

        OR of(TableField field1, TableField field2);

        OR of(TableField field1, TableField field2, TableField field3);

        OR of(TableField field1, TableField field2, TableField field3, TableField field4);

        OR of(Consumer<Consumer<TableField>> consumer);

        OR ifOf(Consumer<Consumer<TableField>> consumer);

    }


    interface _OracleFetchClause<R> extends _QueryFetchClause<R> {

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param percent      the percentage of the total number of selected rows
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, Expression percent, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);


        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param percent      non-null,the percentage of the total number of selected rows
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param supplier     {@link  Supplier#get()} return non-null percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-null percent
         * @param keyName      keyName that is passed to function
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent
                , Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param percent      nullable,percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , @Nullable Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param supplier     return nullable percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(MappingType, Object)}</li>
         *                          <li>{@link SQLs#param(MappingType, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return nullable percent
         * @param keyName      keyName that is passed to function
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent
                , Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

    }


    interface _OracleStaticOrderByClause<OR> extends _StaticOrderByClause<OR> {

        OR orderSiblingsBy(Expression exp1);

        OR orderSiblingsBy(Expression exp1, Expression exp2);

        OR orderSiblingsBy(Expression exp1, Expression exp2, Expression exp3);

        OR orderSiblingsBy(Expression exp1, AscDesc ascDesc);

        OR orderSiblingsBy(Expression exp1, AscDesc ascDesc, NullsFirstLast nullOption);

        OR orderSiblingsBy(Expression exp1, AscDesc ascDesc1, Expression exp2, AscDesc ascDesc2);

    }

    interface _OracleDynamicOrderByClause<OR> extends _DynamicOrderByClause<OR> {

        OR orderSiblingsBy(Consumer<SortNullItems> consumer);

        OR ifOrderSiblingsBy(Consumer<SortNullItems> consumer);
    }


    interface _WindowOrderByCommaSpec<I extends Item>
            extends _StaticOrderByCommaClause<_WindowOrderByCommaSpec<I>>
            , Window._SimpleFrameUnitsSpec<I> {

    }

    interface _WindowOrderBySpec<I extends Item> extends _OracleStaticOrderByClause<_WindowOrderByCommaSpec<I>>
            , _OracleDynamicOrderByClause<Window._SimpleFrameUnitsSpec<I>>
            , Window._SimpleFrameUnitsSpec<I> {

    }

    interface _WindowPartitionBySpec<I extends Item> extends Window._PartitionByExpClause<_WindowOrderBySpec<I>>
            , _WindowOrderBySpec<I> {

    }


    interface _WindowLeftParenSpec<I extends Item> extends Window._LeftParenNameClause<_WindowPartitionBySpec<I>>
            , _WindowPartitionBySpec<I> {

    }

    interface _WindowAsClause<I extends Item> extends _StaticAsClaus<_WindowLeftParenSpec<I>> {

    }


    interface _UnionLockWaitOptionSpec<I extends Item>
            extends _OracleLockWaitOptionClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }


    interface _UnionLockOfColumnSpec<I extends Item>
            extends _OracleLockOfColumnClause<_UnionLockWaitOptionSpec<I>>
            , _UnionLockWaitOptionSpec<I> {

    }


    interface _UnionLockSpec<I extends Item>
            extends _LockForUpdateClause<_UnionLockOfColumnSpec<I>>
            , _AsQueryClause<I> {


    }

    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_UnionLockSpec<I>>
            , _UnionLockSpec<I> {

    }


    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>
            , _UnionLockSpec<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item>
            extends _StaticOrderByNullsCommaClause<_UnionOrderByCommaSpec<I>>
            , _UnionOffsetSpec<I> {

    }


    interface _UnionSpec<I extends Item> extends _QueryUnionClause<_UnionAndQuerySpec<I>>
            , _QueryIntersectClause<_UnionAndQuerySpec<I>>
            , _QueryMinusClause<_UnionAndQuerySpec<I>>
            , _AsQueryClause<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _OracleStaticOrderByClause<_UnionOrderByCommaSpec<I>>
            , _OracleDynamicOrderByClause<_UnionOffsetSpec<I>>
            , _UnionOffsetSpec<I>
            , _UnionSpec<I> {

    }


    interface _LockWaitOptionSpec<I extends Item>
            extends _OracleLockWaitOptionClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }


    interface _LockOfColumnSpec<I extends Item>
            extends _OracleLockOfColumnClause<_LockWaitOptionSpec<I>>
            , _LockWaitOptionSpec<I> {

    }


    interface _LockSpec<I extends Item>
            extends _LockForUpdateClause<_LockOfColumnSpec<I>>
            , _AsQueryClause<I> {


    }

    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_LockSpec<I>>
            , _LockSpec<I> {

    }


    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>
            , _LockSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item>
            extends _StaticOrderByNullsCommaClause<_OrderByCommaSpec<I>>
            , _OffsetSpec<I> {

    }

    interface _OrderBySpec<I extends Item> extends _OracleStaticOrderByClause<_OrderByCommaSpec<I>>
            , _OracleDynamicOrderByClause<_OffsetSpec<I>>
            , _OffsetSpec<I> {

    }

    interface _WindowCommaSpec<I extends Item> extends Window._StaticWindowCommaClause<_WindowCommaSpec<I>>
            , _OrderBySpec<I> {

    }

    interface _WindowsSpec<I extends Item> extends Window._StaticWindowClause<_WindowCommaSpec<I>>
            , Window._DynamicWindowClause<OracleWindowBuilder, _OrderBySpec<I>>
            , _OrderBySpec<I> {

    }


    interface _MinWithSpec<I extends Item> {

    }


    interface _UnionAndQuerySpec<I extends Item> extends _MinWithSpec<I>
            , Query._LeftParenClause<_UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

}