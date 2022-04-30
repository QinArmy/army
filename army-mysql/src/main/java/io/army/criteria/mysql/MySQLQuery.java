package io.army.criteria.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Query;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL SELECT syntax,this interface is base interface of below:
 * <ul>
 *     <li>{@link MySQL57Query}</li>
 *     <li>{@link MySQL80Query}</li>
 * </ul>
 * </p>
 *
 * @see MySQL57Query
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
    interface _MySQLSelectClause<C, SR> extends DialectStatement.DialectSelectClause<C, SR> {

    }


    /**
     * <p>
     * This interface representing FORM clause for MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type.
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @param <FP> next clause java type
     * @param <FB> next clause java type,it's sub interface of {@link LeftBracketClause}.
     * @since 1.0
     */
    interface _MySQLFromClause<C, FT, FS, FP, FB> extends DialectStatement.DialectFromClause<C, FT, FS, FP, FB> {


    }

    /**
     * @param <C>  criteria type use to create dynamic statement.
     * @param <JT> index hint clause,see {@link _IndexHintClause}
     * @param <JS> on clause,see {@link _OnClause}
     * @param <JP> partition clause, see {@link _PartitionClause}
     */
    interface _MySQLJoinClause<C, JT, JS, JP, JC, JD, JE, JF> extends DialectStatement.DialectJoinClause<C, JT, JS, JP, JC, JD, JE, JF> {

    }

    /**
     * <p>
     * This interface representing nested join left bracket clause in MySQL.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @since 1.0
     */
    interface _MySQLJoinBracketClause<C, JT, JS, JP> extends DialectLeftBracketClause<C, JT, JS, JP> {

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
     * @param <C> java criteria object java type
     * @since 1.0
     */
    interface _PartitionClause<C, PR> {

        PR partition(String partitionName);

        PR partition(String partitionName1, String partitionNam2);

        PR partition(String partitionName1, String partitionNam2, String partitionNam3);

        PR partition(Supplier<List<String>> supplier);

        PR partition(Function<C, List<String>> function);

        PR partition(Consumer<List<String>> consumer);

        PR ifPartition(Supplier<List<String>> supplier);

        PR ifPartition(Function<C, List<String>> function);

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
     * @param <C> java criteria object java type
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
     * @param <C> java criteria object java type
     * @since 1.0
     */
    interface _IndexOrderByClause<C, IC> {

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
     * @param <C> java criteria object java type
     * @since 1.0
     */
    interface _IndexJoinClause<C, IC> {

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
     * @param <C> java criteria object java type
     * @since 1.0
     */
    interface _IndexPurposeClause<C, IC> extends _IndexOrderByClause<C, IC>
            , _IndexJoinClause<C, IC> {

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
     * @param <C> java criteria object java type
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
     * @param <C> java criteria object java type
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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _IntoSpec<C, Q extends Query> extends _IntoClause<C, _QuerySpec<Q>>, _QuerySpec<Q> {

    }


}
