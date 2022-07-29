package io.army.criteria.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Query;
import io.army.criteria.Statement;

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
    interface _MySQLSelectClause<C, SR> extends _DialectSelectClause<C, MySQLWords, SR> {

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
    interface _MySQLJoinClause<C, JT, JS> extends _JoinClause<C, JT, JS>, _StraightJoinClause<C, JT, JS> {

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
    interface _MySQLDialectJoinClause<C, JP> extends _DialectJoinClause<C, JP>, _DialectStraightJoinClause<C, JP> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *         <li>{@link _JoinCteClause }</li>
     *         <li>{@link  _StraightJoinCteClause}</li>
     *     </ul>
     * </p>
     *
     * @param <JS> next clause java type
     * @since 1.0
     */
    interface _MySQLJoinCteClause<JS> extends _JoinCteClause<JS>, _StraightJoinCteClause<JS> {

    }


    interface _PartitionCommaDualClause<PR> extends Statement._RightParenClause<PR> {

        Statement._RightParenClause<PR> comma(String partitionName);

        _PartitionCommaDualClause<PR> comma(String partitionName1, String partitionName2);
    }

    interface _PartitionCommaQuadraClause<PR> extends Statement._RightParenClause<PR> {

        Statement._RightParenClause<PR> comma(String partitionName);

        Statement._RightParenClause<PR> comma(String partitionName1, String partitionName2);

        Statement._RightParenClause<PR> comma(String partitionName1, String partitionName2, String partitionName3);

        _PartitionCommaQuadraClause<PR> comma(String partitionName1, String partitionName2, String partitionName3, String partitionName4);

    }

    interface _PartitionLeftParenClause<C, PR> {

        Statement._RightParenClause<PR> leftParen(String partitionName);

        _PartitionCommaDualClause<PR> leftParen(String partitionName1, String partitionName2);

        _PartitionCommaQuadraClause<PR> leftParen(String partitionName1, String partitionName2, String partitionName3, String partitionName4);

        Statement._RightParenClause<PR> leftParen(Consumer<Consumer<String>> consumer);

        Statement._RightParenClause<PR> leftParen(BiConsumer<C, Consumer<String>> consumer);

        Statement._RightParenClause<PR> leftParenIf(Consumer<Consumer<String>> consumer);

        Statement._RightParenClause<PR> leftParenIf(BiConsumer<C, Consumer<String>> consumer);

    }


    interface _PartitionClause<C, PR> {

        _PartitionLeftParenClause<C, PR> partition();

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
    interface _PartitionClause2<C, PR> {

        PR partition(String partitionName);

        PR partition(String partitionName1, String partitionNam2);

        PR partition(String partitionName1, String partitionNam2, String partitionNam3);

        PR partition(Consumer<Consumer<String>> consumer);

        PR partition(BiConsumer<C, Consumer<String>> consumer);

        PR ifPartition(Consumer<Consumer<String>> consumer);

        PR ifPartition(BiConsumer<C, Consumer<String>> consumer);

    }


    /**
     * <p>
     * This interface representing index hint clause in MySQL.
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
    interface _IndexHintClause<C, IR, IC> {

        IR useIndex();

        IR ignoreIndex();

        IR forceIndex();

        IR ifUseIndex(Predicate<C> predicate);

        IR ifIgnoreIndex(Predicate<C> predicate);

        IR ifForceIndex(Predicate<C> predicate);

        IC useIndex(List<String> indexList);

        IC ignoreIndex(List<String> indexList);

        IC forceIndex(List<String> indexList);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IC ifUseIndex(Function<C, List<String>> function);


        /**
         * @return clause , clause no action if predicate return false.
         */
        IC ifIgnoreIndex(Function<C, List<String>> function);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IC ifForceIndex(Function<C, List<String>> function);

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


}
