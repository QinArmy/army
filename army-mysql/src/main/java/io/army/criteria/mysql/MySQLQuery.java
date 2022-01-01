package io.army.criteria.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.TablePart;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface MySQLQuery extends Query, DialectStatement {

    /**
     * @param <C>  criteria type use to create dynamic statement.
     * @param <JT> index hint clause,see {@link MySQLQuery.IndexHintClause}
     * @param <JR> on clause,see {@link Query.OnClause}
     * @param <IT> partition clause, see {@link MySQLQuery.PartitionClause}
     */
    interface MySQLJoinClause<C, JT, JR, IT> extends Statement.JoinClause<C, JT, JR> {

        IT leftJoin(TableMeta<?> table);

        IT leftJoin(Predicate<C> predicate, TableMeta<?> table);

        IT join(TableMeta<?> table);

        IT ifJoin(Predicate<C> predicate, TableMeta<?> table);

        IT rightJoin(TableMeta<?> table);

        IT ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        IT straightJoin(TableMeta<?> table);

        JT straightJoin(TableMeta<?> table, String tableAlias);

        <T extends TablePart> JR straightJoin(Function<C, T> function, String tableAlias);

        IT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        <T extends TablePart> JR ifStraightJoin(Function<C, T> function, String tableAlias);

        IT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        IT fullJoin(TableMeta<?> table);

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


    interface IndexHintClause<C, IR> {

        IR use();

        IR ignore();

        IR force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifForce(Predicate<C> predicate);

    }

    interface IndexHintWordClause<WP, WR> {

        WP index();

        WP key();

        WR index(List<String> indexNameList);

        WR key(List<String> indexNameList);
    }

    interface IndexHintOrderByClause<PR> {

        PR forOrderBy(List<String> indexNameList);
    }

    interface IndexHintForJoinClause<PR> {

        PR forJoin(List<String> indexNameList);
    }


    interface IndexHintPurposeClause<PR> extends IndexHintOrderByClause<PR>, IndexHintForJoinClause<PR> {

        PR forGroupBy(List<String> indexNameList);

    }


}
