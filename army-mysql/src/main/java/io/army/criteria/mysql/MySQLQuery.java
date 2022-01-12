package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface MySQLQuery extends Query, DialectStatement {

    interface WithClause<C, WE> {

        WE with(String cteName, Supplier<SubQuery> supplier);

        WE with(String cteName, Function<C, SubQuery> function);

        WE with(Supplier<List<Cte>> supplier);

        WE with(Function<C, List<Cte>> function);

        WE withRecursive(String cteName, Supplier<SubQuery> supplier);

        WE withRecursive(String cteName, Function<C, SubQuery> function);

        WE withRecursive(Supplier<List<Cte>> supplier);

        WE withRecursive(Function<C, List<Cte>> function);

    }

    interface MySQLFromClause<C, FT, FS, FP> extends Statement.FromClause<C, FT, FS> {

        FP from(TableMeta<?> table);

    }

    /**
     * @param <C>  criteria type use to create dynamic statement.
     * @param <JT> index hint clause,see {@link MySQLQuery.IndexHintClause}
     * @param <JS> on clause,see {@link Query.OnClause}
     * @param <JP> partition clause, see {@link MySQLQuery.PartitionClause}
     */
    interface MySQLJoinClause<C, JT, JS, JP> extends Statement.JoinClause<C, JT, JS> {

        JT straightJoin(TableMeta<?> table, String tableAlias);

        JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias);

        <T extends TablePart> JS straightJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS straightJoin(Supplier<T> supplier, String alias);

        <T extends TablePart> JS ifStraightJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS ifStraightJoin(Supplier<T> supplier, String alias);

        JP leftJoin(TableMeta<?> table);

        JP ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        JP join(TableMeta<?> table);

        JP ifJoin(Predicate<C> predicate, TableMeta<?> table);

        JP rightJoin(TableMeta<?> table);

        JP ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        JP straightJoin(TableMeta<?> table);

        JP ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        JP fullJoin(TableMeta<?> table);

        JP ifFullJoin(Predicate<C> predicate, TableMeta<?> table);


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

        WC window(String name, Expression<?> partition);

        WC window(String name, Expression<?> partition, SortPart order);

        WC window(NamedWindow namedWindow);

        WC window(NamedWindow namedWindow1, NamedWindow namedWindow2);

        WC window(Supplier<List<NamedWindow>> supplier);

        WC window(Function<C, List<NamedWindow>> function);

        WC ifWindow(Supplier<List<NamedWindow>> supplier);

        WC ifWindow(Function<C, List<NamedWindow>> function);

    }

    interface Lock80Clause<C, LU, LS> {

        LU forUpdate();

        LU forShare();

        LU ifForUpdate(Predicate<C> predicate);

        LU ifForShare(Predicate<C> predicate);

        LS lockInShareMode();

        LS ifLockInShareMode(Predicate<C> predicate);

    }

    interface Lock80LockOfOptionClause<C, LO> {

        LO of(TableMeta<?> table);

        LO of(TableMeta<?> table1, TableMeta<?> table2);

        LO of(List<TableMeta<?>> tableList);

        LO ifOf(Function<C, List<TableMeta<?>>> function);

        LO ifOf(Supplier<List<TableMeta<?>>> supplier);
    }

    interface Lock80LockOptionClause<C, LS> {

        LS nowait();

        LS skipLocked();

        LS ifNowait(Predicate<C> predicate);

        LS ifSkipLocked(Predicate<C> predicate);

    }


}
