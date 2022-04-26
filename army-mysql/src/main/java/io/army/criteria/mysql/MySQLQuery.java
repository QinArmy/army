package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.meta.TableMeta;

import java.util.List;
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
     * This interface representing MySQL syntax from clause.
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
    interface MySQLFromClause<C, FT, FS, FP, FB> extends DialectStatement.DialectFromClause<C, FT, FS, FP, FB> {


    }

    /**
     * @param <C>  criteria type use to create dynamic statement.
     * @param <JT> index hint clause,see {@link MySQLQuery.IndexHintClause}
     * @param <JS> on clause,see {@link Query.OnClause}
     * @param <JP> partition clause, see {@link MySQLQuery.PartitionClause}
     */
    interface MySQLJoinClause<C, JT, JS, JP, JC, JD, JE, JF> extends DialectStatement.DialectJoinClause<C, JT, JS, JP, JC, JD, JE, JF> {

    }

    interface MySQLJoinBracketClause<C, JT, JS, JP> extends DialectLeftBracketClause<C, JT, JS, JP> {

    }


    interface PartitionClause<C, PR> {

        PR partition(String partitionName);

        PR partition(String partitionName1, String partitionNam2);

        PR partition(List<String> partitionNameList);

        PR partition(Supplier<List<String>> supplier);

        PR partition(Function<C, List<String>> function);

        PR ifPartition(Supplier<List<String>> supplier);

        PR ifPartition(Function<C, List<String>> function);

    }


    interface IndexHintClause<C, IR, IC> {

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

    interface IndexOrderByClause<C, IC> {

        IC forOrderBy(List<String> indexList);

        IC forOrderBy(Function<C, List<String>> function);
    }

    interface IndexJoinClause<C, IC> {

        IC forJoin(List<String> indexList);

        IC forJoin(Function<C, List<String>> function);
    }


    interface IndexPurposeClause<C, IC> extends MySQLQuery.IndexOrderByClause<C, IC>
            , MySQLQuery.IndexJoinClause<C, IC> {

        IC forGroupBy(List<String> indexList);

        IC forGroupBy(Function<C, List<String>> function);

    }

    interface WithRollupClause<C, WU> {

        WU withRollup();

        WU ifWithRollup(Predicate<C> predicate);

    }


    interface LockClause<C, LO> {

        LO forUpdate();

        LO lockInShareMode();

        LO ifForUpdate(Predicate<C> predicate);

        LO ifLockInShareMode(Predicate<C> predicate);

    }

    interface WindowClause<C, WC> {

        WC window(String name, Expression partition);

        WC window(String name, Expression partition, SortItem order);

        WC window(NamedWindow namedWindow);

        WC window(NamedWindow namedWindow1, NamedWindow namedWindow2);

        WC window(Supplier<List<NamedWindow>> supplier);

        WC window(Function<C, List<NamedWindow>> function);

        WC ifWindow(Supplier<List<NamedWindow>> supplier);

        WC ifWindow(Function<C, List<NamedWindow>> function);

    }

    /**
     * <p>
     * This interface representing lock FOR UPDATE and LOCK IN SHARE MODE clause in MySQL 8.0.
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
    interface Lock80Clause<C, LU, LS> {

        LU forUpdate();

        LU forShare();

        LU ifForUpdate(Predicate<C> predicate);

        LU ifForShare(Predicate<C> predicate);

        LS lockInShareMode();

        LS ifLockInShareMode(Predicate<C> predicate);

    }

    /**
     * <p>
     * This interface representing lock OF clause in MySQL 8.0.
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
    interface Lock80OfClause<C, LO> {

        LO of(TableMeta<?> table);

        LO of(TableMeta<?> table1, TableMeta<?> table2);

        LO of(List<TableMeta<?>> tableList);

        LO ifOf(Function<C, List<TableMeta<?>>> function);

        LO ifOf(Supplier<List<TableMeta<?>>> supplier);
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
    interface Lock80OptionClause<C, LS> {

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
     * @since 1.0
     */
    interface IntoClause<C, IO> {

        IO into(String varName);

        IO into(String varName1, String varName2);

        IO into(List<String> varNameList);

        IO into(Supplier<List<String>> supplier);

        IO into(Function<C, List<String>> function);

        IO ifInto(Supplier<List<String>> supplier);

        IO ifInto(Function<C, List<String>> function);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>INTO clause for MySQL</li>
     *          <li>method {@link QuerySpec#asQuery()}</li>
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
    interface IntoSpec<C, Q extends Query> extends MySQLQuery.IntoClause<C, Query.QuerySpec<Q>>, Query.QuerySpec<Q> {

    }


}
