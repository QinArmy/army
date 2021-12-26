package io.army.criteria.mysql;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SortPart;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">MySQL 5.7  UPDATE Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57Update extends MySQLUpdate {

    /*################################## blow single-update clause interface ##################################*/

    interface Update57Spec extends UpdateSpec {

        MySQL57Update asUpdate();
    }

    interface SingleUpdate57Spec<T extends IDomain, C> {

        SingleSet57Spec<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }


    interface SingleSet57Spec<T extends IDomain, C> {


        SingleWhere57Spec<T, C> set(FieldMeta<? super T, ?> field, @Nullable Object value);

        SingleWhere57Spec<T, C> set(FieldMeta<? super T, ?> field, Expression<?> value);

        <F> SingleWhere57Spec<T, C> set(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> SingleWhere57Spec<T, C> set(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        SingleWhere57Spec<T, C> setNull(FieldMeta<? super T, ?> field);

        SingleWhere57Spec<T, C> setDefault(FieldMeta<? super T, ?> field);

        <F extends Number> SingleWhere57Spec<T, C> setPlus(FieldMeta<? super T, F> field, F value);

        <F extends Number> SingleWhere57Spec<T, C> setPlus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SingleWhere57Spec<T, C> setMinus(FieldMeta<? super T, F> field, F value);

        <F extends Number> SingleWhere57Spec<T, C> setMinus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SingleWhere57Spec<T, C> setMultiply(FieldMeta<? super T, F> field, F value);

        <F extends Number> SingleWhere57Spec<T, C> setMultiply(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SingleWhere57Spec<T, C> setDivide(FieldMeta<? super T, F> field, F value);

        <F extends Number> SingleWhere57Spec<T, C> setDivide(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SingleWhere57Spec<T, C> setMod(FieldMeta<? super T, F> field, F value);

        <F extends Number> SingleWhere57Spec<T, C> setMod(FieldMeta<? super T, F> field, Expression<F> value);

        <F> SingleWhere57Spec<T, C> ifSet(FieldMeta<? super T, F> field, @Nullable F value);

        <F> SingleWhere57Spec<T, C> ifSet(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> SingleWhere57Spec<T, C> ifSet(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        <F extends Number> SingleWhere57Spec<T, C> ifSetPlus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SingleWhere57Spec<T, C> ifSetMinus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SingleWhere57Spec<T, C> ifSetMultiply(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SingleWhere57Spec<T, C> ifSetDivide(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SingleWhere57Spec<T, C> ifSetMod(FieldMeta<? super T, F> field, @Nullable F value);

    }


    interface SingleWhere57Spec<T extends IDomain, C> extends SingleSet57Spec<T, C> {

        SingleWhereAnd57Spec<C> where(IPredicate predicate);

        SingleOrderBy57Spec<C> where(List<IPredicate> predicateList);

        SingleOrderBy57Spec<C> where(Function<C, List<IPredicate>> function);

        SingleOrderBy57Spec<C> where(Supplier<List<IPredicate>> supplier);

    }


    interface SingleWhereAnd57Spec<C> extends SingleOrderBy57Spec<C> {

        SingleWhereAnd57Spec<C> and(IPredicate predicate);

        SingleWhereAnd57Spec<C> and(Function<C, IPredicate> function);

        SingleWhereAnd57Spec<C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        SingleWhereAnd57Spec<C> ifAnd(@Nullable IPredicate predicate);

        SingleWhereAnd57Spec<C> ifAnd(Function<C, IPredicate> function);

        SingleWhereAnd57Spec<C> ifAnd(Supplier<IPredicate> supplier);

    }

    interface SingleOrderBy57Spec<C> extends SingleLimit57Spec<C> {

        SingleLimit57Spec<C> orderBy(SortPart sortPart);

        SingleLimit57Spec<C> orderBy(SortPart sortPart1, SortPart sortPart2);

        SingleLimit57Spec<C> orderBy(List<SortPart> sortPartList);

        SingleLimit57Spec<C> orderBy(Function<C, List<SortPart>> function);

        SingleLimit57Spec<C> orderBy(Supplier<List<SortPart>> supplier);

        SingleLimit57Spec<C> ifOrderBy(@Nullable SortPart sortPart);

        SingleLimit57Spec<C> ifOrderBy(Supplier<List<SortPart>> supplier);

        SingleLimit57Spec<C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface SingleLimit57Spec<C> extends Update57Spec {

        Update57Spec limit(long rowCount);

        Update57Spec limit(Function<C, Long> function);

        Update57Spec limit(Supplier<Long> supplier);

        Update57Spec ifLimit(Function<C, Long> function);

        Update57Spec ifLimit(Supplier<Long> supplier);

    }

    interface IndexHint57Spec<Q extends MySQLQuery, C> {

        IndexHintFor57Spec<Q, C> useIndex();

        IndexHintFor57Spec<Q, C> useKey();

        IndexHintFor57Spec<Q, C> ignoreIndex();

        IndexHintFor57Spec<Q, C> ignoreKey();

        IndexHintFor57Spec<Q, C> forceIndex();

        IndexHintFor57Spec<Q, C> forceKey();

        IndexHint57Spec<Q, C> useIndex(List<String> indexNameList);

        IndexHint57Spec<Q, C> useKey(List<String> indexNameList);

        IndexHint57Spec<Q, C> ignoreIndex(List<String> indexNameList);

        IndexHint57Spec<Q, C> ignoreKey(List<String> indexNameList);

        IndexHint57Spec<Q, C> forceIndex(List<String> indexNameList);

        IndexHint57Spec<Q, C> forceKey(List<String> indexNameList);

    }

    interface IndexHintFor57Spec<T extends IDomain, C> {

        IndexHint57Spec<T, C> froOrderBy(List<String> indexNameList);

    }

    /*################################## blow multi-update clause interface ##################################*/


    interface MultiUpdate57Spec<C> {

        MySQLMultiUpdateIndexHintJoinSpec<C> update(TableMeta<?> tableMeta, String tableAlias);

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

    interface MySQLMultiUpdateOnSpec<C> {

        MySQLMultiUpdateJoinSpec<C> on(List<IPredicate> predicateList);

        MySQLMultiUpdateJoinSpec<C> on(IPredicate predicate);

        MySQLMultiUpdateJoinSpec<C> on(Function<C, List<IPredicate>> function);
    }


    interface MySQLMultiUpdateSetSpec<C> {

        <F> MySQLMultiUpdateSingleWhere57Spec<C> set(FieldMeta<?, F> target, F value);

        /**
         * @see SQLs#defaultWord()
         */
        <F> MySQLMultiUpdateSingleWhere57Spec<C> set(FieldMeta<?, F> target, Expression<F> valueExp);

        <F> MySQLMultiUpdateSingleWhere57Spec<C> ifSet(Predicate<C> predicate, FieldMeta<?, F> target, F value);

        <F> MySQLMultiUpdateSingleWhere57Spec<C> ifSet(FieldMeta<?, F> target, Function<C, Expression<F>> function);
    }


    interface MySQLMultiUpdateSingleWhere57Spec<C> extends MySQLMultiUpdateSetSpec<C> {

        MySQLMultiUpdateWhereAndSpec<C> where(IPredicate predicate);

        UpdateSpec where(List<IPredicate> predicateList);

        UpdateSpec where(Function<C, List<IPredicate>> function);
    }

    interface MySQLMultiUpdateWhereAndSpec<C> extends UpdateSpec {

        MySQLMultiUpdateWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        MySQLMultiUpdateWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLMultiUpdateWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }


    /*################################## blow batch single-update clause interface ##################################*/

    interface MySQLBatchSingleUpdateSpec<T extends IDomain, C> {

        MySQLBatchSingleUpdateSetSpec<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }


    interface MySQLBatchSingleUpdateSetSpec<T extends IDomain, C> {

        <F> MySQLBatchSingleUpdateSingleWhere57Spec<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see SQLs#defaultWord()
         */
        <F> MySQLBatchSingleUpdateSingleWhere57Spec<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> MySQLBatchSingleUpdateSingleWhere57Spec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> MySQLBatchSingleUpdateSingleWhere57Spec<T, C> ifSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }


    interface MySQLBatchSingleUpdateSingleWhere57Spec<T extends IDomain, C> extends MySQLBatchSingleUpdateSetSpec<T, C> {

        MySQLBatchSingleUpdateWhereAndSpec<C> where(IPredicate predicate);

        MySQLBatchSingleUpdateOrderBySpec<C> where(List<IPredicate> predicateList);

        MySQLBatchSingleUpdateOrderBySpec<C> where(Function<C, List<IPredicate>> function);

    }


    interface MySQLBatchSingleUpdateWhereAndSpec<C> extends MySQLBatchSingleUpdateOrderBySpec<C> {

        MySQLBatchSingleUpdateWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
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

    interface MySQLBatchSingleUpdateNamedParamSpec<C> {

        UpdateSpec namedParamMaps(List<Map<String, Object>> mapList);

        UpdateSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec namedParamBeans(List<Object> beanList);

        UpdateSpec namedParamBeans(Function<C, List<Object>> function);

    }


    /*################################## blow batch multi-update clause interface ##################################*/


    interface MySQLBatchMultiUpdateSpec<C> {

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

    interface MySQLBatchMultiUpdateOnSpec<C> {

        MySQLBatchMultiUpdateJoinSpec<C> on(List<IPredicate> predicateList);

        MySQLBatchMultiUpdateJoinSpec<C> on(IPredicate predicate);

        MySQLBatchMultiUpdateJoinSpec<C> on(Function<C, List<IPredicate>> function);
    }


    interface MySQLBatchMultiUpdateSetSpec<C> {

        <F> MySQLBatchMultiUpdateSingleWhere57Spec<C> set(FieldMeta<?, F> target, F value);

        /**
         * @see SQLs#defaultWord()
         */
        <F> MySQLBatchMultiUpdateSingleWhere57Spec<C> set(FieldMeta<?, F> target, Expression<F> valueExp);

        <F> MySQLBatchMultiUpdateSingleWhere57Spec<C> ifSet(Predicate<C> predicate, FieldMeta<?, F> target, F value);

        <F> MySQLBatchMultiUpdateSingleWhere57Spec<C> ifSet(FieldMeta<?, F> target, Function<C, Expression<F>> function);
    }


    interface MySQLBatchMultiUpdateSingleWhere57Spec<C> extends MySQLBatchMultiUpdateSetSpec<C> {

        MySQLBatchMultiUpdateWhereAndSpec<C> where(IPredicate predicate);

        MySQLBatchMultiUpdateNamedParamSpec<C> where(List<IPredicate> predicateList);

        MySQLBatchMultiUpdateNamedParamSpec<C> where(Function<C, List<IPredicate>> function);
    }

    interface MySQLBatchMultiUpdateWhereAndSpec<C> extends MySQLBatchMultiUpdateNamedParamSpec<C> {

        MySQLBatchMultiUpdateWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        MySQLBatchMultiUpdateWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        MySQLBatchMultiUpdateWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface MySQLBatchMultiUpdateNamedParamSpec<C> {

        UpdateSpec namedParamMaps(List<Map<String, Object>> mapList);

        UpdateSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec namedParamBeans(List<Object> beanList);

        UpdateSpec namedParamBeans(Function<C, List<Object>> function);
    }


}
