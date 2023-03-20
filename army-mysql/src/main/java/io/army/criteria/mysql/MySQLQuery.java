package io.army.criteria.mysql;

import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.MySQLs;
import io.army.lang.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

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
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/with.html">MySQL 8.0 WITH (Common Table Expressions)</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-named-windows.html">MySQL 8.0 Named Windows</a>
 * @since 1.0
 */
public interface MySQLQuery extends Query, MySQLStatement {


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

        IO into(String firstVarName, String... rest);

        IO into(Consumer<Consumer<String>> consumer);

        IO ifInto(Consumer<Consumer<String>> consumer);

    }


    interface _UnionSpec<I extends Item> extends _QueryUnionClause<_QueryWithComplexSpec<I>>, _AsQueryClause<I> {

    }

    interface _UnionLimitSpec<I extends Item> extends _AsQueryClause<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _OrderByClause<_UnionLimitSpec<I>>,
            _UnionLimitSpec<I>, _UnionSpec<I> {

    }


    interface _IntoOptionSpec<I extends Item> extends _IntoOptionClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }

    interface _LockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_IntoOptionSpec<I>>,
            _IntoOptionSpec<I> {

    }

    interface _LockOfTableSpec<I extends Item> extends _LockOfTableAliasClause<_LockWaitOptionSpec<I>>,
            _LockWaitOptionSpec<I> {

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

        _IntoOptionSpec<I> ifLockInShareMode(BooleanSupplier predicate);

    }

    interface _LimitSpec<I extends Item> extends _LimitClause<_LockOptionSpec<I>>, _LockOptionSpec<I> {

    }


    interface _OrderByWithRollupClause<I extends Item> {

        _LimitSpec<I> withRollup();

        _LimitSpec<I> ifWithRollup(BooleanSupplier supplier);
    }

    interface _OrderByWithRollupSpec<I extends Item> extends _OrderByWithRollupClause<I>, _LimitSpec<I> {

    }

    interface _OrderBySpec<I extends Item> extends _OrderByClause<_OrderByWithRollupSpec<I>>,
            _LimitSpec<I>,
            _UnionSpec<I> {

    }

    interface _WindowAsClause<I extends Item> extends Window._WindowAsClause<_WindowCommaSpec<I>> {

        _WindowCommaSpec<I> as(@Nullable String existingWindowName, Consumer<Window._SimplePartitionBySpec> consumer);

        _WindowCommaSpec<I> as(Consumer<Window._SimplePartitionBySpec> consumer);
    }


    interface _WindowCommaSpec<I extends Item> extends _OrderBySpec<I> {

        _WindowAsClause<I> comma(String windowName);


    }


    interface _WindowSpec<I extends Item> extends _OrderBySpec<I>
            , Window._DynamicWindowClause<MySQLWindows, _OrderBySpec<I>> {

        _WindowAsClause<I> window(String windowName);

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


    interface _IndexHintOnSpec<I extends Item> extends _QueryIndexHintSpec<_IndexHintOnSpec<I>>,
            _DynamicIndexHintClause<_IndexPurposeBySpec<Object>, _IndexHintOnSpec<I>>,
            _OnClause<_JoinSpec<I>> {

    }


    interface _PartitionOnSpec<I extends Item> extends _PartitionAsClause<_IndexHintOnSpec<I>> {

    }


    interface _JoinSpec<I extends Item>
            extends _MySQLJoinClause<_IndexHintOnSpec<I>, _AsParensOnClause<_JoinSpec<I>>>,
            _MySQLCrossClause<_IndexHintJoinSpec<I>, _ParensJoinSpec<I>>,
            _MySQLJoinCteClause<_OnClause<_JoinSpec<I>>>,
            _CrossJoinCteClause<_JoinSpec<I>>,
            _MySQLJoinNestedClause<_OnClause<_JoinSpec<I>>>,
            _MySQLCrossNestedClause<_JoinSpec<I>>,
            _MySQLDynamicJoinCrossClause<_JoinSpec<I>>,
            _MySQLDialectJoinClause<_PartitionOnSpec<I>>,
            _DialectCrossJoinClause<_PartitionJoinSpec<I>>,
            _WhereSpec<I> {

    }

    interface _IndexHintJoinSpec<I extends Item> extends _QueryIndexHintSpec<_IndexHintJoinSpec<I>>,
            _DynamicIndexHintClause<_IndexPurposeBySpec<Object>, _IndexHintJoinSpec<I>>,
            _JoinSpec<I> {

    }


    interface _PartitionJoinSpec<I extends Item> extends _PartitionAsClause<_IndexHintJoinSpec<I>> {

    }

    interface _ParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_JoinSpec<I>>, _JoinSpec<I> {

    }


    interface _FromSpec<I extends Item>
            extends _MySQLFromClause<_IndexHintJoinSpec<I>, _ParensJoinSpec<I>>,
            _FromCteClause<_JoinSpec<I>>,
            _FromTableClause<_PartitionJoinSpec<I>>,
            _MySQLFromNestedClause<_JoinSpec<I>>,
            _IntoOptionSpec<I>,
            _UnionSpec<I> {

    }

    interface _MySQLSelectCommaSpec<I extends Item> extends _StaticSelectCommaClause<_MySQLSelectCommaSpec<I>>,
            _FromSpec<I> {

    }

    interface _MySQLSelectClause<I extends Item>
            extends _HintsModifiersListSelectClause<MySQLs.Modifier, _MySQLSelectCommaSpec<I>>,
            _DynamicHintModifierSelectClause<MySQLs.Modifier, _FromSpec<I>> {

    }


    interface _SelectSpec<I extends Item> extends _MySQLSelectClause<I>,
            _DynamicParensQueryClause<_WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteParensSpec<I>>,
            _StaticSpaceClause<I> {

    }

    interface _StaticCteAsClause<I extends Item> {
        _CteComma<I> as(Function<_SelectSpec<_CteComma<I>>, _CteComma<I>> function);

    }

    interface _StaticCteParensSpec<I extends Item>
            extends _OptionalParensStringClause<_StaticCteAsClause<I>>, _StaticCteAsClause<I> {

    }


    interface _WithSpec<I extends Item> extends _MySQLDynamicWithClause<_SelectSpec<I>>,
            _MySQLStaticWithClause<_SelectSpec<I>>,
            _SelectSpec<I> {

    }


    interface _DynamicCteAsClause {

        _CommaClause<MySQLCtes> as(Function<_WithSpec<_CommaClause<MySQLCtes>>, _CommaClause<MySQLCtes>> function);
    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_DynamicCteAsClause>,
            _DynamicCteAsClause {

    }

    /**
     * <p>
     * VALUES statement don't support WITH clause.
     * </p>
     */
    interface _QueryComplexSpec<I extends Item> extends _MySQLSelectClause<I>,
            _DynamicParensQueryClause<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _MySQLDynamicWithClause<_QueryComplexSpec<I>>,
            _MySQLStaticWithClause<_QueryComplexSpec<I>>,
            _QueryComplexSpec<I>,
            MySQLValues._MySQLValuesClause<I> {

    }


}
