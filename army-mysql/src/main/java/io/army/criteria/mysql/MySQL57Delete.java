package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">MySQL 5.7  DELETE Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57Delete extends Delete {


    /*################################## blow single-delete clause interface ##################################*/

    interface MySQLSingleDeleteSpec<C> {

        MySQLSingleDeleteTableRouteSpec<C> deleteFrom(TableMeta<?> tableMeta);
    }

    interface MySQLSingleDeleteTableRouteSpec<C> extends MySQLSingleDeleteWhereSpec<C> {

        MySQLSingleDeleteWhereSpec<C> route(int databaseIndex, int tableIndex);

        MySQLSingleDeleteWhereSpec<C> route(int tableIndex);
    }

    interface MySQLSingleDeleteWhereSpec<C> {

        MySQLSingleDeleteWhereAndSpec<C> where(IPredicate predicate);

        MySQLSingleDeleteOrderBySpec<C> where(List<IPredicate> predicateList);

        MySQLSingleDeleteOrderBySpec<C> where(Function<C, List<IPredicate>> function);
    }

    interface MySQLSingleDeleteWhereAndSpec<C> extends MySQLSingleDeleteOrderBySpec<C> {

        MySQLSingleDeleteWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        MySQLSingleDeleteWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLSingleDeleteWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);
    }

    interface MySQLSingleDeleteOrderBySpec<C> extends MySQLSingleDeleteLimitSpec<C> {

        MySQLSingleDeleteLimitSpec<C> orderBy(SortPart... sortParts);

        MySQLSingleDeleteLimitSpec<C> orderBy(List<SortPart> sortPartList);

        MySQLSingleDeleteLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface MySQLSingleDeleteLimitSpec<C> extends DeleteSpec {

        DeleteSpec limit(int rowCount);

        DeleteSpec ifLimit(Function<C, Integer> function);
    }


    /*################################## blow multi-delete clause interface ##################################*/

    interface MySQLMultiDeleteSpec<C> {

        MySQLMultiDeleteUsingSpec<C> deleteFrom(List<TableMeta<?>> tableMetaList);

        MySQLMultiDeleteUsingSpec<C> deleteFrom(Function<C, List<TableMeta<?>>> function);

    }

    interface MySQLMultiDeleteUsingSpec<C> {

        MySQLMultiDeleteTableRouteJoinSpec<C> using(TableMeta<?> tableMeta, String tableAlias);
    }

    interface MySQLMultiDeleteTableRouteJoinSpec<C> extends MySQLMultiDeleteIndexHintJoinSpec<C> {

        MySQLMultiDeleteIndexHintJoinSpec<C> route(int databaseIndex, int tableIndex);

        MySQLMultiDeleteIndexHintJoinSpec<C> route(int tableIndex);
    }

    interface MySQLMultiDeleteIndexHintJoinSpec<C> extends MySQLMultiDeleteJoinSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLMultiDeleteJoinSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);
    }

    interface MySQLMultiDeleteJoinSpec<C> extends MySQLMultiDeleteWhereSpec<C> {

        MySQLMultiDeleteTableRouteOnSpec<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiDeleteOnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLMultiDeleteTableRouteOnSpec<C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiDeleteOnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        MySQLMultiDeleteTableRouteOnSpec<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiDeleteOnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLMultiDeleteTableRouteOnSpec<C> straightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiDeleteOnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

    }


    interface MySQLMultiDeleteTableRouteOnSpec<C> extends MySQLMultiDeleteIndexHintOnSpec<C> {

        MySQLMultiDeleteIndexHintOnSpec<C> route(int databaseIndex, int tableIndex);

        MySQLMultiDeleteIndexHintOnSpec<C> route(int tableIndex);
    }

    interface MySQLMultiDeleteIndexHintOnSpec<C> extends MySQLMultiDeleteOnSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLMultiDeleteOnSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);
    }

    interface MySQLMultiDeleteOnSpec<C> {

        MySQLMultiDeleteJoinSpec<C> on(List<IPredicate> predicateList);

        MySQLMultiDeleteJoinSpec<C> on(IPredicate predicate);

        MySQLMultiDeleteJoinSpec<C> on(Function<C, List<IPredicate>> function);
    }

    interface MySQLMultiDeleteWhereSpec<C> {

        MySQLMultiDeleteWhereAndSpec<C> where(IPredicate predicate);

        DeleteSpec where(List<IPredicate> predicateList);

        DeleteSpec where(Function<C, List<IPredicate>> function);
    }

    interface MySQLMultiDeleteWhereAndSpec<C> extends DeleteSpec {

        MySQLMultiDeleteWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        MySQLMultiDeleteWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLMultiDeleteWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);
    }



    /*################################## blow batch single-delete clause interface ##################################*/

    interface MySQLBatchSingleDeleteSpec<C> {

        MySQLBatchSingleDeleteWhereSpec<C> deleteFrom(TableMeta<?> tableMeta);
    }

    interface MySQLBatchSingleDeleteWhereSpec<C> {

        MySQLBatchSingleDeleteWhereAndSpec<C> where(IPredicate predicate);

        MySQLBatchSingleDeleteOrderBySpec<C> where(List<IPredicate> predicateList);

        MySQLBatchSingleDeleteOrderBySpec<C> where(Function<C, List<IPredicate>> function);
    }

    interface MySQLBatchSingleDeleteWhereAndSpec<C> extends MySQLBatchSingleDeleteOrderBySpec<C> {

        MySQLBatchSingleDeleteWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        MySQLBatchSingleDeleteWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLBatchSingleDeleteWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);
    }

    interface MySQLBatchSingleDeleteOrderBySpec<C> extends MySQLBatchSingleDeleteLimitSpec<C> {

        MySQLBatchSingleDeleteLimitSpec<C> orderBy(SortPart... sortParts);

        MySQLBatchSingleDeleteLimitSpec<C> orderBy(List<SortPart> sortPartList);

        MySQLBatchSingleDeleteLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface MySQLBatchSingleDeleteLimitSpec<C> extends MySQLBatchSingleDeleteNamedParamSpec<C> {

        MySQLBatchSingleDeleteNamedParamSpec<C> limit(int rowCount);

        MySQLBatchSingleDeleteNamedParamSpec<C> ifLimit(Function<C, Integer> function);
    }

    interface MySQLBatchSingleDeleteNamedParamSpec<C> {

        DeleteSpec namedParamMaps(List<Map<String, Object>> mapList);

        DeleteSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        DeleteSpec namedParamBeans(List<Object> beanList);

        DeleteSpec namedParamBeans(Function<C, List<Object>> function);

    }



    /*################################## blow batch multi-delete clause interface ##################################*/

    interface MySQLBatchMultiDeleteSpec<C> {

        MySQLBatchMultiDeleteUsingSpec<C> deleteFrom(List<TableMeta<?>> tableMetaList);

        MySQLBatchMultiDeleteUsingSpec<C> deleteFrom(Function<C, List<TableMeta<?>>> function);

    }

    interface MySQLBatchMultiDeleteUsingSpec<C> {

        MySQLBatchMultiDeleteIndexHintJoinSpec<C> using(TableMeta<?> tableMeta, String tableAlias);
    }


    interface MySQLBatchMultiDeleteIndexHintJoinSpec<C> extends MySQLBatchMultiDeleteJoinSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLBatchMultiDeleteJoinSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);
    }

    interface MySQLBatchMultiDeleteJoinSpec<C> extends MySQLBatchMultiDeleteWhereSpec<C> {

        MySQLBatchMultiDeleteIndexHintOnSpec<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiDeleteOnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLBatchMultiDeleteIndexHintOnSpec<C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiDeleteOnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        MySQLBatchMultiDeleteIndexHintOnSpec<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiDeleteOnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLBatchMultiDeleteIndexHintOnSpec<C> straightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiDeleteOnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface MySQLBatchMultiDeleteIndexHintOnSpec<C> extends MySQLBatchMultiDeleteOnSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLBatchMultiDeleteOnSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);
    }

    interface MySQLBatchMultiDeleteOnSpec<C> {

        MySQLBatchMultiDeleteJoinSpec<C> on(List<IPredicate> predicateList);

        MySQLBatchMultiDeleteJoinSpec<C> on(IPredicate predicate);

        MySQLBatchMultiDeleteJoinSpec<C> on(Function<C, List<IPredicate>> function);
    }

    interface MySQLBatchMultiDeleteWhereSpec<C> {

        MySQLBatchMultiDeleteWhereAndSpec<C> where(IPredicate predicate);

        MySQLBatchMultiDeleteNamedParamSpec<C> where(List<IPredicate> predicateList);

        MySQLBatchMultiDeleteNamedParamSpec<C> where(Function<C, List<IPredicate>> function);
    }

    interface MySQLBatchMultiDeleteWhereAndSpec<C> extends MySQLBatchMultiDeleteNamedParamSpec<C> {

        MySQLBatchMultiDeleteWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        MySQLBatchMultiDeleteWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLBatchMultiDeleteWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);
    }

    interface MySQLBatchMultiDeleteNamedParamSpec<C> {

        DeleteSpec namedParamMaps(List<Map<String, Object>> mapList);

        DeleteSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        DeleteSpec namedParamBeans(List<Object> beanList);

        DeleteSpec namedParamBeans(Function<C, List<Object>> function);

    }


}
