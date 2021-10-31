package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.Sqls;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">MySQL 5.7  UPDATE Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57Update extends Update {

    /*################################## blow single-update clause interface ##################################*/

    interface MySQLSingleUpdateSpec<T extends IDomain, C> extends UpdateSQLSpec {

        MySQLSingleUpdateTableRouteSpec<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }

    interface MySQLSingleUpdateTableRouteSpec<T extends IDomain, C> extends MySQLSingleUpdateSetSpec<T, C> {

        MySQLSingleUpdateSetSpec<T, C> route(int databaseIndex, int tableIndex);

        MySQLSingleUpdateSetSpec<T, C> route(int tableIndex);
    }

    interface MySQLSingleUpdateSetSpec<T extends IDomain, C> extends UpdateSQLSpec {

        <F> MySQLSingleUpdateWhereSpec<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see Sqls#defaultValue()
         */
        <F> MySQLSingleUpdateWhereSpec<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> MySQLSingleUpdateWhereSpec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> MySQLSingleUpdateWhereSpec<T, C> ifSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }


    interface MySQLSingleUpdateWhereSpec<T extends IDomain, C> extends MySQLSingleUpdateSetSpec<T, C> {

        MySQLSingleUpdateWhereAndSpec<C> where(IPredicate predicate);

        MySQLSingleUpdateOrderBySpec<C> where(List<IPredicate> predicateList);

        MySQLSingleUpdateOrderBySpec<C> where(Function<C, List<IPredicate>> function);

    }


    interface MySQLSingleUpdateWhereAndSpec<C> extends MySQLSingleUpdateOrderBySpec<C> {

        MySQLSingleUpdateWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        MySQLSingleUpdateWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLSingleUpdateWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface MySQLSingleUpdateOrderBySpec<C> extends MySQLSingleUpdateLimitSpec<C> {

        MySQLSingleUpdateLimitSpec<C> orderBy(SortPart... sortParts);

        MySQLSingleUpdateLimitSpec<C> orderBy(List<SortPart> sortPartList);

        MySQLSingleUpdateLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface MySQLSingleUpdateLimitSpec<C> extends UpdateSpec {

        UpdateSpec limit(int rowCount);

        UpdateSpec ifLimit(Function<C, Integer> function);
    }

    /*################################## blow multi-update clause interface ##################################*/


    interface MySQLMultiUpdateSpec<C> extends UpdateSQLSpec {

        MySQLMultiUpdateTableRoutJoinSpec<C> update(TableMeta<?> tableMeta, String tableAlias);


    }

    interface MySQLMultiUpdateTableRoutJoinSpec<C> extends MySQLMultiUpdateIndexHintJoinSpec<C> {

        MySQLMultiUpdateIndexHintJoinSpec<C> route(int databaseIndex, int tableIndex);

        MySQLMultiUpdateIndexHintJoinSpec<C> route(int tableIndex);

    }

    interface MySQLMultiUpdateIndexHintJoinSpec<C> extends MySQLMultiUpdateJoinSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLMultiUpdateJoinSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);

    }

    interface MySQLMultiUpdateJoinSpec<C> extends MySQLMultiUpdateSetSpec<C> {

        MySQLMultiUpdateTableRouteOneSpec<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiUpdateOnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLMultiUpdateTableRouteOneSpec<C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiUpdateOnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        MySQLMultiUpdateTableRouteOneSpec<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiUpdateOnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLMultiUpdateTableRouteOneSpec<C> straightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLMultiUpdateOnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface MySQLMultiUpdateTableRouteOneSpec<C> extends MySQLMultiUpdateIndexHintOnSpec<C> {

        MySQLMultiUpdateIndexHintOnSpec<C> route(int databaseIndex, int tableIndex);

        MySQLMultiUpdateIndexHintOnSpec<C> route(int tableIndex);

    }

    interface MySQLMultiUpdateIndexHintOnSpec<C> extends MySQLMultiUpdateOnSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLMultiUpdateOnSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);
    }

    interface MySQLMultiUpdateOnSpec<C> extends UpdateSQLSpec {

        MySQLMultiUpdateJoinSpec<C> on(List<IPredicate> predicateList);

        MySQLMultiUpdateJoinSpec<C> on(IPredicate predicate);

        MySQLMultiUpdateJoinSpec<C> on(Function<C, List<IPredicate>> function);
    }


    interface MySQLMultiUpdateSetSpec<C> extends UpdateSQLSpec {

        <F> MySQLMultiUpdateWhereSpec<C> set(FieldMeta<?, F> target, F value);

        /**
         * @see Sqls#defaultValue()
         */
        <F> MySQLMultiUpdateWhereSpec<C> set(FieldMeta<?, F> target, Expression<F> valueExp);

        <F> MySQLMultiUpdateWhereSpec<C> ifSet(Predicate<C> predicate, FieldMeta<?, F> target, F value);

        <F> MySQLMultiUpdateWhereSpec<C> ifSet(FieldMeta<?, F> target, Function<C, Expression<F>> function);
    }


    interface MySQLMultiUpdateWhereSpec<C> extends MySQLMultiUpdateSetSpec<C> {

        MySQLMultiUpdateWhereAndSpec<C> where(IPredicate predicate);

        UpdateSpec where(List<IPredicate> predicateList);

        UpdateSpec where(Function<C, List<IPredicate>> function);
    }

    interface MySQLMultiUpdateWhereAndSpec<C> extends UpdateSpec {

        MySQLMultiUpdateWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        MySQLMultiUpdateWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLMultiUpdateWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }


    /*################################## blow batch single-update clause interface ##################################*/

    interface MySQLBatchSingleUpdateSpec<T extends IDomain, C> extends UpdateSQLSpec {

        MySQLBatchSingleUpdateSetSpec<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }


    interface MySQLBatchSingleUpdateSetSpec<T extends IDomain, C> extends UpdateSQLSpec {

        <F> MySQLBatchSingleUpdateWhereSpec<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see Sqls#defaultValue()
         */
        <F> MySQLBatchSingleUpdateWhereSpec<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> MySQLBatchSingleUpdateWhereSpec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> MySQLBatchSingleUpdateWhereSpec<T, C> ifSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }


    interface MySQLBatchSingleUpdateWhereSpec<T extends IDomain, C> extends MySQLBatchSingleUpdateSetSpec<T, C> {

        MySQLBatchSingleUpdateWhereAndSpec<C> where(IPredicate predicate);

        MySQLBatchSingleUpdateOrderBySpec<C> where(List<IPredicate> predicateList);

        MySQLBatchSingleUpdateOrderBySpec<C> where(Function<C, List<IPredicate>> function);

    }


    interface MySQLBatchSingleUpdateWhereAndSpec<C> extends MySQLBatchSingleUpdateOrderBySpec<C> {

        MySQLBatchSingleUpdateWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        MySQLBatchSingleUpdateWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLBatchSingleUpdateWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface MySQLBatchSingleUpdateOrderBySpec<C> extends MySQLBatchSingleUpdateLimitSpec<C> {

        MySQLBatchSingleUpdateLimitSpec<C> orderBy(SortPart... sortParts);

        MySQLBatchSingleUpdateLimitSpec<C> orderBy(List<SortPart> sortPartList);

        MySQLBatchSingleUpdateLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface MySQLBatchSingleUpdateLimitSpec<C> extends MySQLBatchSingleUpdateNamedParamSpec<C> {

        MySQLBatchSingleUpdateNamedParamSpec<C> limit(int rowCount);

        MySQLBatchSingleUpdateNamedParamSpec<C> ifLimit(Function<C, Integer> function);
    }

    interface MySQLBatchSingleUpdateNamedParamSpec<C> extends UpdateSQLSpec {

        UpdateSpec namedParamMaps(List<Map<String, Object>> mapList);

        UpdateSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec namedParamBeans(List<Object> beanList);

        UpdateSpec namedParamBeans(Function<C, List<Object>> function);

    }


    /*################################## blow batch multi-update clause interface ##################################*/


    interface MySQLBatchMultiUpdateSpec<C> extends UpdateSQLSpec {

        MySQLBatchAfterUpdateIndexHintSpec<C> update(TableMeta<?> tableMeta, String tableAlias);
    }

    interface MySQLBatchAfterUpdateIndexHintSpec<C> extends MySQLBatchMultiUpdateJoinSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLBatchMultiUpdateJoinSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);

    }

    interface MySQLBatchMultiUpdateJoinSpec<C> extends MySQLBatchMultiUpdateSetSpec<C> {

        MySQLBatchMultiUpdateAfterJoinIndexHintSpec<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiUpdateOnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLBatchMultiUpdateAfterJoinIndexHintSpec<C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiUpdateOnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        MySQLBatchMultiUpdateAfterJoinIndexHintSpec<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiUpdateOnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLBatchMultiUpdateAfterJoinIndexHintSpec<C> straightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLBatchMultiUpdateOnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

    }


    interface MySQLBatchMultiUpdateAfterJoinIndexHintSpec<C> extends MySQLBatchMultiUpdateOnSpec<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLBatchMultiUpdateOnSpec<C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);
    }

    interface MySQLBatchMultiUpdateOnSpec<C> extends UpdateSQLSpec {

        MySQLBatchMultiUpdateJoinSpec<C> on(List<IPredicate> predicateList);

        MySQLBatchMultiUpdateJoinSpec<C> on(IPredicate predicate);

        MySQLBatchMultiUpdateJoinSpec<C> on(Function<C, List<IPredicate>> function);
    }


    interface MySQLBatchMultiUpdateSetSpec<C> extends UpdateSQLSpec {

        <F> MySQLBatchMultiUpdateWhereSpec<C> set(FieldMeta<?, F> target, F value);

        /**
         * @see Sqls#defaultValue()
         */
        <F> MySQLBatchMultiUpdateWhereSpec<C> set(FieldMeta<?, F> target, Expression<F> valueExp);

        <F> MySQLBatchMultiUpdateWhereSpec<C> ifSet(Predicate<C> predicate, FieldMeta<?, F> target, F value);

        <F> MySQLBatchMultiUpdateWhereSpec<C> ifSet(FieldMeta<?, F> target, Function<C, Expression<F>> function);
    }


    interface MySQLBatchMultiUpdateWhereSpec<C> extends MySQLBatchMultiUpdateSetSpec<C> {

        MySQLBatchMultiUpdateWhereAndSpec<C> where(IPredicate predicate);

        MySQLBatchMultiUpdateNamedParamSpec<C> where(List<IPredicate> predicateList);

        MySQLBatchMultiUpdateNamedParamSpec<C> where(Function<C, List<IPredicate>> function);
    }

    interface MySQLBatchMultiUpdateWhereAndSpec<C> extends MySQLBatchMultiUpdateNamedParamSpec<C> {

        MySQLBatchMultiUpdateWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        MySQLBatchMultiUpdateWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLBatchMultiUpdateWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface MySQLBatchMultiUpdateNamedParamSpec<C> extends UpdateSQLSpec {

        UpdateSpec namedParamMaps(List<Map<String, Object>> mapList);

        UpdateSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec namedParamBeans(List<Object> beanList);

        UpdateSpec namedParamBeans(Function<C, List<Object>> function);
    }


}
