package io.army.criteria.mysql;

import io.army.criteria.*;

import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This interface representing MySQL SELECT syntax,this interface is base interface of below:
 * <ul>
 *     <li>{@link MySQL80Query}</li>
 * </ul>
 * </p>
 *
 * @see MySQL80Query
 * @since 1.0
 */
public interface MySQLQuery extends Query, DialectStatement {


    /**
     * <p>
     * This interface representing SELECT clause for MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type.
     * @param <SR> next clause java type
     * @since 1.0
     */
    interface _MySQLSelectClause<C, SR> extends _DialectSelectClause<C, MySQLModifier, SR> {

    }


    interface _MySQLFromLateralClause<C, FS> {

        <T extends SubQuery> FS fromLateral(Supplier<T> supplier, String alias);

        <T extends SubQuery> FS fromLateral(Function<C, T> function, String alias);
    }


    interface _MySQLFromClause<C, FT, FS> extends Statement._FromClause<C, FT, FS>
            , DialectStatement._FromCteClause<FS>
            , _MySQLFromLateralClause<C, FS> {

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
     * @param <C>  criteria type use to create dynamic statement.
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @since 1.0
     */
    interface _MySQLJoinClause<C, JT, JS> extends Statement._JoinClause<C, JT, JS>
            , DialectStatement._StraightJoinClause<C, JT, JS>
            , DialectStatement._JoinCteClause<JS>
            , DialectStatement._StraightJoinCteClause<JS>
            , DialectStatement._JoinLateralClause<C, JS>
            , DialectStatement._StraightJoinLateralClause<C, JS> {

    }


    interface _MySQLIfJoinClause<C, FS> extends Statement._IfJoinClause<C, FS>
            , DialectStatement._IfStraightJoinClause<C, FS> {

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

    interface _MySQLCrossJoinClause<C, FT, FS> extends Statement._CrossJoinClause<C, FT, FS>
            , DialectStatement._CrossJoinCteClause<FS>
            , DialectStatement._CrossJoinLateralClause<C, FS> {

    }


    interface _PartitionClause<PR> {

        _LeftParenStringQuadraOptionalSpec<PR> partition();

    }

    interface _PartitionAndAsClause<C, AR> extends _PartitionClause<C, Statement._AsClause<AR>> {

    }


    interface _IndexForJoinSpec<C, RR> extends Statement._LeftParenStringDualOptionalSpec<C, RR> {

        Statement._LeftParenStringDualOptionalSpec<C, RR> forJoin();

    }

    interface _IndexForOrderBySpec<C, RR> extends Statement._LeftParenStringDualOptionalSpec<C, RR> {

        Statement._LeftParenStringDualOptionalSpec<C, RR> forOrderBy();

    }


    interface _IndexPurposeBySpec<C, RR> extends _IndexForJoinSpec<C, RR>, _IndexForOrderBySpec<C, RR> {

        Statement._LeftParenStringDualOptionalSpec<C, RR> forGroupBy();

    }


    interface _IndexHintClause<C, RR> {

        Statement._LeftParenStringDualOptionalSpec<C, RR> useIndex();

        Statement._LeftParenStringDualOptionalSpec<C, RR> ignoreIndex();

        Statement._LeftParenStringDualOptionalSpec<C, RR> forceIndex();
    }


    interface _IndexHintForJoinClause<C, RR> extends _IndexHintClause<C, RR> {

        @Override
        _IndexForJoinSpec<C, RR> useIndex();

        @Override
        _IndexForJoinSpec<C, RR> ignoreIndex();

        @Override
        _IndexForJoinSpec<C, RR> forceIndex();

    }


    interface _IndexHintForOrderByClause<C, RR> extends _IndexHintClause<C, RR> {

        @Override
        _IndexForOrderBySpec<C, RR> useIndex();

        @Override
        _IndexForOrderBySpec<C, RR> ignoreIndex();

        @Override
        _IndexForOrderBySpec<C, RR> forceIndex();

    }


    interface _QueryIndexHintClause<C, RR> extends _IndexHintForJoinClause<C, RR>, _IndexHintForOrderByClause<C, RR> {

        @Override
        _IndexPurposeBySpec<C, RR> useIndex();

        @Override
        _IndexPurposeBySpec<C, RR> ignoreIndex();

        @Override
        _IndexPurposeBySpec<C, RR> forceIndex();

    }


    /**
     * <p>
     * This interface representing PARTITION clause in MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    @Deprecated
    interface _PartitionClause2<C, PR> {

        PR partition(String partitionName);

        PR partition(String partitionName1, String partitionNam2);

        PR partition(String partitionName1, String partitionNam2, String partitionNam3);

        PR partition(Consumer<Consumer<String>> consumer);

        PR partition(BiConsumer<C, Consumer<String>> consumer);

        PR ifPartition(Consumer<Consumer<String>> consumer);

        PR ifPartition(BiConsumer<C, Consumer<String>> consumer);

    }


    @Deprecated
    interface _UserIndexClause<C, RR> {

    }



    /**
     * <p>
     * This interface representing index hint(FOR ORDER BY) clause in MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _IndexForOrderByClause<C, IC> {

        IC forOrderBy(List<String> indexList);

        IC forOrderBy(Function<C, List<String>> function);
    }

    /**
     * <p>
     * This interface representing index hint(FOR JOIN) clause in MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _IndexForJoinClause<C, IC> {

        IC forJoin(List<String> indexList);

        IC forJoin(Function<C, List<String>> function);
    }


    /**
     * <p>
     * This interface representing index hint(FOR GROUP BY) clause in MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _IndexPurposeClause<C, IC> extends _IndexForOrderByClause<C, IC>
            , _IndexForJoinClause<C, IC> {

        IC forGroupBy(List<String> indexList);

        IC forGroupBy(Function<C, List<String>> function);

    }

    /**
     * <p>
     * This interface representing WITH ROLLUP clause in MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _WithRollupClause<C, WU> {

        WU withRollup();

        WU ifWithRollup(Predicate<C> predicate);

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
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _LockClause<C, LO> {

        LO forUpdate();

        LO lockInShareMode();

        LO ifForUpdate(Predicate<C> predicate);

        LO ifLockInShareMode(Predicate<C> predicate);

    }

    /**
     * <p>
     * This interface representing lock FOR UPDATE and LOCK IN SHARE MODE clause As of MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  java criteria object java type
     * @param <LU> next clause java type
     * @param <LS> next clause java type
     * @since 1.0
     */
    interface _Lock80Clause<C, LU, LS> {

        LU forUpdate();

        LU forShare();

        LU ifForUpdate(Predicate<C> predicate);

        LU ifForShare(Predicate<C> predicate);

        LS lockInShareMode();

        LS ifLockInShareMode(Predicate<C> predicate);

    }

    /**
     * <p>
     * This interface representing lock OF clause As of MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  java criteria object java type
     * @param <LO> next clause java type
     * @since 1.0
     */
    interface _Lock80OfClause<C, LO> {

        LO of(String tableAlias);

        LO of(String tableAlias1, String tableAlias2);

        LO of(String tableAlias1, String tableAlias2, String tableAlias3);

        LO of(Supplier<List<String>> supplier);

        LO of(Function<C, List<String>> function);

        LO of(Consumer<List<String>> consumer);

        LO ifOf(Supplier<List<String>> supplier);

        LO ifOf(Function<C, List<String>> function);

    }

    /**
     * <p>
     * This interface representing lock NOWAIT and SKIP LOCKED clause in  MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  java criteria object java type
     * @param <LS> next clause java type
     * @since 1.0
     */
    interface _Lock80OptionClause<C, LS> {

        LS nowait();

        LS skipLocked();

        LS ifNowait(Predicate<C> predicate);

        LS ifSkipLocked(Predicate<C> predicate);

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
     * @param <C>  java criteria object java type
     * @param <IO> next clause java type
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select-into.html">MySQL 5.7 SELECT ... INTO Statement</a>
     * @since 1.0
     */
    interface _IntoClause<C, IO> {

        IO into(String varName);

        IO into(String varName1, String varName2);

        IO into(String varName1, String varName2, String varName3);

        /**
         * @param varNameList non-null and non-empty list.
         */
        IO into(List<String> varNameList);

        /**
         * @param supplier must return non-null and non-empty list.
         */
        IO into(Supplier<List<String>> supplier);

        /**
         * @param function must return non-null and non-empty list.
         */
        IO into(Function<C, List<String>> function);

        IO into(Consumer<List<String>> consumer);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>INTO clause for MySQL</li>
     *          <li>method {@link _QuerySpec#asQuery()}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _IntoSpec<C, Q extends Query> extends _IntoClause<C, _QuerySpec<Q>>, _QuerySpec<Q> {

    }


    /*-------------------below nested item interface -------------------*/


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _IndexHintClause}</li>
     *          <li>the composite {@link _NestedJoinSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _NestedUseIndexJoinSpec<C>
            extends _QueryIndexHintClause<C, _NestedUseIndexJoinSpec<C>>
            , _NestedJoinClause<C> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _OnClause}</li>
     *          <li>the composite {@link _NestedJoinSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _NestedOnSpec<C> extends _OnClause<C, _NestedJoinSpec<C>>, _NestedJoinSpec<C> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _IndexHintClause}</li>
     *          <li>the composite {@link _NestedOnSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _NestedUseIndexOnSpec<C>
            extends _QueryIndexHintClause<C, _NestedUseIndexOnSpec<C>>
            , _NestedOnSpec<C> {

    }

    /**
     * <p>
     * This interface representing nested partition clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _NestedPartitionOnClause<C> extends _PartitionClause<C, _AsClause<_NestedUseIndexOnSpec<C>>> {

    }


    /**
     * <p>
     * This interface representing nested partition clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _NestedPartitionJoinClause<C>
            extends _PartitionClause<C, _AsClause<_NestedUseIndexJoinSpec<C>>> {

    }


    interface _NestedJoinClause<C> extends _MySQLJoinClause<C, _NestedUseIndexOnSpec<C>, _NestedOnSpec<C>>
            , _MySQLCrossJoinClause<C, _NestedUseIndexJoinSpec<C>, _NestedJoinSpec<C>>
            , _MySQLIfJoinClause<C, _NestedJoinSpec<C>>
            , _MySQLDialectJoinClause<_NestedPartitionOnClause<C>>
            , _DialectCrossJoinClause<_NestedPartitionJoinClause<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLJoinClause}</li>
     *          <li>{@link _CrossJoinCteClause}</li>
     *          <li>{@link _CrossJoinClause}</li>
     *          <li>{@link _MySQLDialectJoinClause}</li>
     *          <li>{@link _DialectCrossJoinClause}</li>
     *          <li> {@link _RightParenClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _NestedJoinSpec<C> extends _NestedJoinClause<C>
            , _RightParenClause<NestedItems> {


    }


    /**
     * <p>
     * This interface representing nested LEFT BRACKET clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MySQLNestedLeftParenClause<C>
            extends _LeftParenClause<C, _NestedUseIndexJoinSpec<C>, _NestedJoinSpec<C>>
            , DialectStatement._DialectLeftParenClause<_NestedPartitionJoinClause<C>>
            , DialectStatement._LeftParenCteClause<_NestedJoinSpec<C>>
            , DialectStatement._LeftParenLateralClause<C, _NestedJoinSpec<C>> {

    }

    /*-------------------below if join clause interface -------------------*/


    interface _IfOnClause<C> extends Statement._OnClause<C, JoinItemBlock<C>>, ItemBlock<C> {

    }

    interface _IfUseIndexOnSpec<C> extends _QueryIndexHintClause<C, _IfUseIndexOnSpec<C>>, _IfOnClause<C> {

    }

    interface _IfPartitionAsClause<C> extends _PartitionClause<C, Statement._AsClause<_IfUseIndexOnSpec<C>>> {

    }


}
