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

        WE with(String cteName, Function<C, SubQuery> supplier);

        WE with(Supplier<List<MySQLCte>> supplier);

        WE with(Function<C, List<MySQLCte>> function);

    }

    interface MySQLFromClause<C, FT, FS, FP> extends Statement.FromClause<C, FT, FS> {

        FP from(TableMeta<?> table);

    }

    /**
     * @param <C>  criteria type use to create dynamic statement.
     * @param <JT> index hint clause,see {@link MySQLQuery.IndexHintClause}
     * @param <JS> on clause,see {@link Query.OnClause}
     * @param <IT> partition clause, see {@link MySQLQuery.PartitionClause}
     */
    interface MySQLJoinClause<C, JT, JS, IT> extends Statement.JoinClause<C, JT, JS> {

        JT straightJoin(TableMeta<?> table, String tableAlias);

        JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias);

        <T extends TablePart> JS straightJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS straightJoin(Supplier<T> supplier, String alias);

        <T extends TablePart> JS ifStraightJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS ifStraightJoin(Supplier<T> supplier, String alias);

        IT leftJoin(TableMeta<?> table);

        IT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        IT join(TableMeta<?> table);

        IT ifJoin(Predicate<C> predicate, TableMeta<?> table);

        IT rightJoin(TableMeta<?> table);

        IT ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        IT straightJoin(TableMeta<?> table);

        IT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        IT fullJoin(TableMeta<?> table);

        IT ifFullJoin(Predicate<C> predicate, TableMeta<?> table);


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

        IR useIndex(Predicate<C> predicate);

        IR ignoreIndex(Predicate<C> predicate);

        IR forceIndex(Predicate<C> predicate);

        IC useIndex(Function<C, List<String>> function);

        IC ignoreIndex(Function<C, List<String>> function);

        IC forceIndex(Function<C, List<String>> function);

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

    interface IndexPurposeClause<C, IC> {

        IC forJoin(List<String> indexList);

        IC forOrderBy(List<String> indexList);

        IC forGroupBy(List<String> indexList);

        IC forJoin(Function<C, List<String>> function);

        IC forOrderBy(Function<C, List<String>> function);

        IC forGroupBy(Function<C, List<String>> function);

    }

    interface WithRollupClause<C, WU> {

        WU withRollup();

        WU withRollup(Predicate<C> predicate);

    }

    interface IntoOptionClause<C, IO> {

        IO into(String varName);

        IO into(String varName1, String varName2);

        IO into(List<String> varNames);

        IO into(Supplier<List<String>> supplier);

        IO into(Function<C, List<String>> function);
    }

    interface LockClause<C, LO> {

        LO forUpdate();

        LO lockInShareMode();

        LO ifForUpdate(Predicate<C> predicate);

        LO ifLockInShareMode(Predicate<C> predicate);

    }

    interface WindowClause<C, WC> {

        WC window(NamedWindow namedWindow);

        WC window(NamedWindow namedWindow1, NamedWindow namedWindow2);

        WC window(Supplier<List<NamedWindow>> supplier);

        WC window(Function<C, List<NamedWindow>> function);

        WC ifWindow(Supplier<List<NamedWindow>> supplier);

        WC ifWindow(Function<C, List<NamedWindow>> function);

    }

    interface Lock80Clause<C, LU, LS> {

        LU forUpdate();

        LU ifForUpdate(Predicate<C> predicate);

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

        LS skipLocked(Predicate<C> predicate);

        LS ifNowait();

        LS ifSkipLocked(Predicate<C> predicate);

    }


}
