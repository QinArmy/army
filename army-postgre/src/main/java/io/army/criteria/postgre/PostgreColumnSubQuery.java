package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface PostgreColumnSubQuery<E> extends PostgreSubQuery, ColumnSubQuery<E> {


    interface PostgreColumnSubQueryAble<E> extends ColumnSubQueryAble<E>, PostgreSubQueryAble {

        PostgreColumnSubQuery<E> asSubQuery();


    }

    interface PostgreColumnSubQuerySelectPartAble<E, C> extends PostgreSubQuerySQLAble {

        PostgreColumnSubQueryFromAble<E, C> select(Distinct distinct, Selection selection);

        PostgreColumnSubQueryFromAble<E, C> select(Selection selection);

    }


    interface PostgreColumnSubQueryFromAble<E, C> extends PostgreSubQueryFromAble<C> {

        @Override
        PostgreColumnSubQueryFromTableSampleAble<E, C> from(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> fromWithQuery(String withSubQueryName);

        @Override
        PostgreSubQueryWhereAble<C> fromFunc(Function<C, PostgreFuncTable> funcFunction);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }


    interface PostgreColumnSubQueryJoinAble<E, C> extends PostgreSubQueryJoinAble<C> {

        @Override
        PostgreColumnSubQueryTableSampleOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreColumnSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        @Override
        PostgreColumnSubQueryOnAble<E, C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia);

        @Override
        PostgreColumnSubQueryOnAble<E, C> leftJoinWithQuery(String withSubQueryName);

        @Override
        PostgreColumnSubQueryOnAble<E, C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        @Override
        PostgreColumnSubQueryTableSampleOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreColumnSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia);

        @Override
        PostgreColumnSubQueryOnAble<E, C> joinLateral(Function<C, SubQuery> function, String subQueryAlia);

        @Override
        PostgreColumnSubQueryOnAble<E, C> joinWithQuery(String withSubQueryName);

        @Override
        PostgreColumnSubQueryOnAble<E, C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        @Override
        PostgreColumnSubQueryTableSampleOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreColumnSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        @Override
        PostgreColumnSubQueryOnAble<E, C> rightJoinWithQuery(String withSubQueryName);

        @Override
        PostgreColumnSubQueryOnAble<E, C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        @Override
        PostgreColumnSubQueryTableSampleOnAble<E, C> fullJoin(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreColumnSubQueryOnAble<E, C> fullJoin(Function<C, SubQuery> function, String subQueryAlia);

        @Override
        PostgreColumnSubQueryOnAble<E, C> fullJoinWithQuery(String withSubQueryName);

        @Override
        PostgreColumnSubQueryOnAble<E, C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);
    }

    interface PostgreColumnSubQueryFromTableSampleAble<E, C> extends PostgreSubQueryFromTableSampleAble<C> {

        @Override
        PostgreColumnSubQueryJoinAble<E, C> tableSampleAfterFrom(Function<C, Expression<?>> samplingMethodFunction);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> tableSampleAfterFrom(Function<C, Expression<?>> samplingMethodFunction
                , Expression<Double> seedExp);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> tableSampleAfterFrom(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }


    interface PostgreColumnSubQueryTableSampleOnAble<E, C> extends PostgreSubQueryTableSampleOnAble<C>
            , PostgreColumnSubQueryOnAble<E, C> {

        @Override
        PostgreColumnSubQueryOnAble<E, C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        @Override
        PostgreColumnSubQueryOnAble<E, C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        @Override
        PostgreColumnSubQueryOnAble<E, C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreColumnSubQueryOnAble<E, C> extends PostgreSubQueryOnAble<C> {

        @Override
        PostgreColumnSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> on(IPredicate predicate);

        @Override
        PostgreColumnSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreColumnSubQueryWhereAble<E, C> extends PostgreSubQueryWhereAble<C>, PostgreColumnSubQueryGroupByAble<E, C> {

        @Override
        PostgreColumnSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList);

        @Override
        PostgreColumnSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function);

        @Override
        PostgreColumnSubQueryWhereAndAble<E, C> where(IPredicate predicate);
    }


    interface PostgreColumnSubQueryWhereAndAble<E, C> extends PostgreSubQueryWhereAndAble<C>
            , PostgreColumnSubQueryGroupByAble<E, C> {

        @Override
        PostgreColumnSubQueryWhereAndAble<E, C> and(IPredicate predicate);

        @Override
        PostgreColumnSubQueryWhereAndAble<E, C> and(Function<C, IPredicate> function);

        @Override
        PostgreColumnSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        @Override
        PostgreColumnSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface PostgreColumnSubQueryGroupByAble<E, C> extends PostgreSubQueryGroupByAble<C>
            , PostgreColumnSubQueryWindowAble<E, C> {

        @Override
        PostgreColumnSubQueryHavingAble<E, C> groupBy(Expression<?> groupExp);

        @Override
        PostgreColumnSubQueryHavingAble<E, C> groupBy(List<Expression<?>> groupExpList);

        @Override
        PostgreColumnSubQueryHavingAble<E, C> groupBy(Function<C, List<Expression<?>>> function);

        @Override
        PostgreColumnSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        @Override
        PostgreColumnSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreColumnSubQueryHavingAble<E, C> extends PostgreSubQueryHavingAble<C>
            , PostgreColumnSubQueryWindowAble<E, C> {

        @Override
        PostgreColumnSubQueryWindowAble<E, C> having(IPredicate predicate);

        @Override
        PostgreColumnSubQueryWindowAble<E, C> having(Function<C, List<IPredicate>> function);

        @Override
        PostgreColumnSubQueryWindowAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        @Override
        PostgreColumnSubQueryWindowAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }

    interface PostgreColumnSubQueryWindowAble<E, C> extends PostgreSubQueryWindowAble<C>
            , PostgreColumnSubQueryOrderByAble<E, C> {

        @Override
        PostgreColumnSubQueryOrderByAble<E, C> window(Function<C, List<PostgreWindow>> windowListFunction);
    }


    interface PostgreColumnSubQueryOrderByAble<E, C> extends PostgreSubQueryOrderByAble<C>
            , PostgreColumnSubQueryLimitAble<E, C> {

        @Override
        PostgreColumnSubQueryLimitAble<E, C> orderBy(Expression<?> orderExp);

        @Override
        PostgreColumnSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function);

        @Override
        PostgreColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        @Override
        PostgreColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate
                , Function<C, List<Expression<?>>> expFunction);
    }

    interface PostgreColumnSubQueryLimitAble<E, C> extends PostgreSubQueryLimitAble<C>
            , PostgreColumnSubQueryComposeAble<E, C> {


        @Override
        PostgreColumnSubQueryComposeAble<E, C> limit(int rowCount);

        @Override
        PostgreColumnSubQueryComposeAble<E, C> limit(int offset, int rowCount);

        @Override
        PostgreColumnSubQueryComposeAble<E, C> limit(Function<C, Pair<Integer, Integer>> function);

        @Override
        PostgreColumnSubQueryComposeAble<E, C> ifLimit(Predicate<C> predicate, int rowCount);

        @Override
        PostgreColumnSubQueryComposeAble<E, C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        @Override
        PostgreColumnSubQueryComposeAble<E, C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);

    }

    interface PostgreColumnSubQueryComposeAble<E, C> extends PostgreSubQueryComposeAble<C>
            , PostgreColumnSubQueryAble<E> {

        PostgreColumnSubQueryComposeAble<E, C> brackets();

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> union(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> unionAll(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> unionDistinct(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> intersect(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> intersectAll(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> intersectDistinct(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> except(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> exceptAll(Function<C, S> function);

        <S extends SubQuery> PostgreColumnSubQueryComposeAble<E, C> exceptDistinct(Function<C, S> function);

    }


}
