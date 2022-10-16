package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * <p>
 * This interface representing MySQL 8.0 SELECT syntax.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/select.html">MySQL 8.0 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/union.html">MySQL 8.0 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/join.html">MySQL 8.0 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/select-into.html">MySQL 8.0 SELECT ... INTO Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/nested-join-optimization.html">MySQL 8.0 Nested Join Optimization</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/partitioning-selection.html">MySQL 8.0 Partition Selection</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/innodb-locking-reads.html#innodb-locking-reads-nowait-skip-locked">MySQL 8.0 Locking Read Concurrency with NOWAIT and SKIP LOCKED</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/parenthesized-query-expressions.html">MySQL 8.0 Parenthesized Query Expressions</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/index-hints.html">MySQL 8.0 Index Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/with.html">MySQL 8.0 WITH (Common Table Expressions)</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-named-windows.html">MySQL 8.0 Named Windows</a>
 * @since 1.0
 */
public interface MySQLQuery extends Query, DialectStatement {

    interface _MySQLDynamicWithCteClause<WE> extends _DynamicWithCteClause<MySQLCteBuilder, WE> {

    }


    interface _MySQLFromClause<FT, FS> extends _FromModifierTabularClause<FT, FS>
            , _FromCteClause<FS> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _JoinClause }</li>
     *         <li>{@link  _StraightJoinClause}</li>
     *     </ul>
     * </p>
     *
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @since 1.0
     */
    interface _MySQLJoinClause<JT, JS> extends Statement._JoinModifierTabularClause<JT, JS>
            , DialectStatement._StraightJoinModifierTabularClause<JT, JS>
            , DialectStatement._JoinCteClause<JS>
            , DialectStatement._StraightJoinCteClause<JS> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _DialectJoinClause }</li>
     *         <li>{@link  _DialectStraightJoinClause}</li>
     *     </ul>
     * </p>
     *
     * @param <JP> next clause java type
     * @since 1.0
     */
    interface _MySQLDialectJoinClause<JP> extends DialectStatement._DialectJoinClause<JP>
            , DialectStatement._DialectStraightJoinClause<JP> {

    }

    interface _MySQLCrossJoinClause<FT, FS> extends Statement._CrossJoinModifierTabularClause<FT, FS>
            , DialectStatement._CrossJoinCteClause<FS> {
    }


    interface _PartitionClause<PR> {

        _LeftParenStringQuadraOptionalSpec<PR> partition();

    }

    interface _PartitionAndAsClause<AR> extends _PartitionClause<Statement._AsClause<AR>> {

    }


    interface _IndexForJoinSpec<RR> extends Statement._LeftParenStringDualOptionalSpec<RR> {

        Statement._LeftParenStringDualOptionalSpec<RR> forJoin();

    }

    interface _IndexForOrderBySpec<RR> extends Statement._LeftParenStringDualOptionalSpec<RR> {

        Statement._LeftParenStringDualOptionalSpec<RR> forOrderBy();

    }


    interface _IndexPurposeBySpec<RR> extends _IndexForJoinSpec<RR>, _IndexForOrderBySpec<RR> {

        Statement._LeftParenStringDualOptionalSpec<RR> forGroupBy();

    }


    interface _IndexHintClause<RR> {

        Statement._LeftParenStringDualOptionalSpec<RR> useIndex();

        Statement._LeftParenStringDualOptionalSpec<RR> ignoreIndex();

        Statement._LeftParenStringDualOptionalSpec<RR> forceIndex();
    }


    interface _IndexHintForJoinClause<RR> extends _IndexHintClause<RR> {

        @Override
        _IndexForJoinSpec<RR> useIndex();

        @Override
        _IndexForJoinSpec<RR> ignoreIndex();

        @Override
        _IndexForJoinSpec<RR> forceIndex();

    }


    interface _IndexHintForOrderByClause<RR> extends _IndexHintClause<RR> {

        @Override
        _IndexForOrderBySpec<RR> useIndex();

        @Override
        _IndexForOrderBySpec<RR> ignoreIndex();

        @Override
        _IndexForOrderBySpec<RR> forceIndex();

    }


    interface _QueryIndexHintClause<RR> extends _IndexHintForJoinClause<RR>, _IndexHintForOrderByClause<RR> {

        @Override
        _IndexPurposeBySpec<RR> useIndex();

        @Override
        _IndexPurposeBySpec<RR> ignoreIndex();

        @Override
        _IndexPurposeBySpec<RR> forceIndex();

    }


    /**
     * <p>
     * This interface representing INTO clause in  MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <IO> next clause java type
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select-into.html">MySQL 5.7 SELECT ... INTO Statement</a>
     * @since 1.0
     */
    interface _IntoOptionClause<IO> {

        IO into(String varName);

        IO into(String varName1, String varName2);

        IO into(String varName1, String varName2, String varName3);

        IO into(String varName1, String varName2, String varName3, String varName4);

        /**
         * @param varNameList non-null and non-empty list.
         */
        IO into(List<String> varNameList);

        IO into(Consumer<Consumer<String>> consumer);

    }


    interface _UnionSpec<I extends Item> extends _QueryUnionClause<_UnionAndQuerySpec<I>>, _QuerySpec<I> {

    }

    interface _UnionLimitSpec<I extends Item> extends _QuerySpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _OrderByClause<_UnionLimitSpec<I>>
            , _UnionLimitSpec<I>, _UnionSpec<I> {

    }


    interface _IntoOptionSpec<I extends Item> extends _IntoOptionClause<_QuerySpec<I>>, _QuerySpec<I> {

    }

    interface _LockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_IntoOptionSpec<I>>
            , _IntoOptionSpec<I> {

    }

    interface _LockOfTableSpec<I extends Item> extends _LockOfTableClause<_LockWaitOptionSpec<I>>
            , _LockWaitOptionSpec<I> {

    }

    /**
     * <p>
     * This interface representing LOCK clause Prior to MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _LockOptionSpec<I extends Item> extends _MinLockOptionClause<_LockOfTableSpec<I>>, _IntoOptionSpec<I> {

        _IntoOptionSpec<I> lockInShareMode();

        _IntoOptionSpec<I> ifLockInShareMode(BooleanSupplier supplier);

    }

    interface _LimitSpec<I extends Item> extends _LimitClause<_LockOptionSpec<I>>, _LockOptionSpec<I> {

    }


    interface _OrderByWithRollupClause<I extends Item> {

        _LimitSpec<I> withRollup();

        _LimitSpec<I> ifWithRollup(BooleanSupplier supplier);
    }

    interface _OrderByWithRollupSpec<I extends Item> extends _OrderByWithRollupClause<I>, _LimitSpec<I> {

    }

    interface _OrderBySpec<I extends Item> extends _OrderByClause<_OrderByWithRollupSpec<I>>, _LimitSpec<I>
            , _UnionSpec<I> {

    }


    interface _WindowCommaSpec<I extends Item> extends _OrderBySpec<I> {

        Window._SimpleAsClause<_WindowCommaSpec<I>> comma(String windowName);

    }


    interface _WindowSpec<I extends Item> extends _OrderBySpec<I> {

        _OrderBySpec<I> window(Consumer<MySQLWindowBuilder> consumer);

        Window._SimpleAsClause<_WindowCommaSpec<I>> window(String windowName);

    }

    interface _HavingSpec<I extends Item> extends _HavingClause<_WindowSpec<I>>, _WindowSpec<I> {

    }

    interface _GroupByWithRollupSpec<I extends Item> extends _OrderByWithRollupClause<I>, _HavingSpec<I> {

        @Override
        _HavingSpec<I> withRollup();

        @Override
        _HavingSpec<I> ifWithRollup(BooleanSupplier supplier);

    }

    interface _GroupBySpec<I extends Item> extends _GroupByClause<_GroupByWithRollupSpec<I>>, _WindowSpec<I> {

    }

    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>, _GroupBySpec<I> {

    }

    interface _WhereSpec<I extends Item> extends _QueryWhereClause<_GroupBySpec<I>, _WhereAndSpec<I>>, _GroupBySpec<I> {

    }


    interface _IndexHintOnSpec<I extends Item> extends _QueryIndexHintClause<_IndexHintOnSpec<I>>
            , _OnClause<_JoinSpec<I>> {

    }


    interface _PartitionOnSpec<I extends Item> extends _PartitionAndAsClause<_IndexHintOnSpec<I>> {

    }


    interface _JoinSpec<I extends Item>
            extends _MySQLJoinClause<_IndexHintOnSpec<I>, _OnClause<_JoinSpec<I>>>
            , _MySQLCrossJoinClause<_IndexHintJoinSpec<I>, _JoinSpec<I>>
            , _MySQLDialectJoinClause<_PartitionOnSpec<I>>
            , _WhereSpec<I> {

    }

    interface _IndexHintJoinSpec<I extends Item> extends _QueryIndexHintClause<_IndexHintJoinSpec<I>>
            , _JoinSpec<I> {

    }


    interface _PartitionJoinSpec<I extends Item> extends _PartitionAndAsClause<_IndexHintJoinSpec<I>> {

    }


    interface _FromSpec<I extends Item> extends _MySQLFromClause<_IndexHintJoinSpec<I>, _JoinSpec<I>>
            , _DialectFromClause<_PartitionJoinSpec<I>>
            , _IntoOptionSpec<I>
            , _UnionSpec<I> {

    }


    interface _MySQLSelectClause<I extends Item>
            extends _DynamicHintModifierSelectClause<MySQLs.Modifier, _FromSpec<I>> {

    }


    interface _StaticCteAsClause<I extends Item> extends Statement._StaticAsClaus<_MySQLSelectClause<_CteSpec<I>>> {

    }

    interface _StaticCteLeftParenSpec<I extends Item>
            extends _LeftParenStringQuadraOptionalSpec<_StaticCteAsClause<I>>
            , _StaticCteAsClause<I> {

    }


    interface _CteComma<I extends Item>
            extends _StaticWithCommaClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _MySQLSelectClause<I> {

    }

    interface _WithCteSpec<I extends Item> extends _MySQLDynamicWithCteClause<_MySQLSelectClause<I>>
            , _StaticWithCteClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _MySQLSelectClause<I> {

    }

    interface _UnionAndQuerySpec<I extends Item> extends _MySQLSelectClause<I>
            , Query._LeftParenClause<_UnionAndQuerySpec<Statement._RightParenClause<_UnionOrderBySpec<I>>>> {

    }


    interface _DynamicCteWithSpec extends _MySQLDynamicWithCteClause<_MySQLSelectClause<_CteSpec<MySQLCteBuilder>>>
            , _MySQLSelectClause<_CteSpec<MySQLCteBuilder>> {

    }


}
