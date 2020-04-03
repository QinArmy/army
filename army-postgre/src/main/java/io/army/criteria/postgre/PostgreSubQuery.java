package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.Postgres;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface PostgreSubQuery extends SubQuery {



    /*################################## blow interfaces  ##################################*/

    interface PostgreSubQuerySQLAble extends SubQuerySQLAble {

    }

    interface PostgreSubQueryAble extends SubQueryAble, PostgreSubQuerySQLAble {

        PostgreSubQuery asSubQuery();
    }

    interface PostgreSubQuerySelectPartAble<C> extends PostgreSubQuerySQLAble {

        <S extends SelectPart> PostgreSubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        PostgreSubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart);

        PostgreSubQueryFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> PostgreSubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> PostgreSubQueryFromAble<C> select(List<S> selectPartList);

        <S extends SelectPart> PostgreSubQueryFromAble<C> selectDistinct(Function<C, List<Expression<?>>> onExpsFunction
                , Function<C, List<S>> selectParsFunction);

        <S extends SelectPart> PostgreSubQueryFromAble<C> selectDistinct(Function<C, List<Expression<?>>> onFunction
                , S selectPart);
    }


    interface PostgreSubQueryFromAble<C> extends PostgreSubQueryWhereAble<C> {

        PostgreSubQueryTableSampleOnAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        PostgreSubQueryJoinAble<C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreSubQueryJoinAble<C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreSubQueryJoinAble<C> fromWithQuery(String withSubQueryName);

        PostgreSubQueryWhereAble<C> fromFunc(Function<C, PostgreFuncTable> funcFunction);

        PostgreSubQueryJoinAble<C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);
    }


    interface PostgreSubQueryJoinAble<C> extends PostgreSubQueryWhereAble<C> {

        PostgreSubQueryTableSampleOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreSubQueryOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreSubQueryOnAble<C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreSubQueryOnAble<C> leftJoinWithQuery(String withSubQueryName);

        PostgreSubQueryOnAble<C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreSubQueryTableSampleOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        PostgreSubQueryOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        PostgreSubQueryOnAble<C> joinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreSubQueryOnAble<C> joinWithQuery(String withSubQueryName);

        PostgreSubQueryOnAble<C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreSubQueryTableSampleOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreSubQueryOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreSubQueryOnAble<C> rightJoinWithQuery(String withSubQueryName);

        PostgreSubQueryOnAble<C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreSubQueryTableSampleOnAble<C> fullJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreSubQueryOnAble<C> fullJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreSubQueryOnAble<C> fullJoinWithQuery(String withSubQueryName);

        PostgreSubQueryOnAble<C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }


    interface PostgreSubQueryTableSampleOnAble<C> extends PostgreSubQueryOnAble<C> {

        PostgreSubQueryJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreSubQueryJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreSubQueryJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreSubQueryOnAble<C> extends PostgreSubQuerySQLAble {

        PostgreSubQueryJoinAble<C> on(List<IPredicate> predicateList);

        PostgreSubQueryJoinAble<C> on(IPredicate predicate);

        PostgreSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreSubQueryWhereAble<C> extends PostgreSubQueryGroupByAble<C> {

        PostgreSubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        PostgreSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        PostgreSubQueryWhereAndAble<C> where(IPredicate predicate);
    }


    interface PostgreSubQueryWhereAndAble<C> extends PostgreSubQueryGroupByAble<C> {

        PostgreSubQueryWhereAndAble<C> and(IPredicate predicate);

        PostgreSubQueryWhereAndAble<C> and(Function<C, IPredicate> function);

        PostgreSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        PostgreSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface PostgreSubQueryGroupByAble<C> extends PostgreSubQueryWindowAble<C> {

        PostgreSubQueryHavingAble<C> groupBy(Expression<?> groupExp);


        PostgreSubQueryHavingAble<C> groupBy(List<Expression<?>> groupExpList);


        PostgreSubQueryHavingAble<C> groupBy(Function<C, List<Expression<?>>> function);


        PostgreSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);


        PostgreSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate
                , Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreSubQueryHavingAble<C> extends PostgreSubQueryWindowAble<C> {

        PostgreSubQueryWindowAble<C> having(IPredicate predicate);


        PostgreSubQueryWindowAble<C> having(Function<C, List<IPredicate>> function);


        PostgreSubQueryWindowAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);


        PostgreSubQueryWindowAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }

    interface PostgreSubQueryWindowAble<C> extends PostgreSubQueryOrderByAble<C> {

        /**
         * @see Postgres#window()
         * @see Postgres#window(Object)
         */
        PostgreSubQueryOrderByAble<C> window(Function<C, List<PostgreWindow>> windowListFunction);
    }


    interface PostgreSubQueryOrderByAble<C> extends PostgreSubQueryLimitAble<C> {
        /**
         * @see Postgres#nullsFirst(Expression)
         * @see Postgres#nullsLast(Expression)
         * @see Postgres#sortUsing(Expression, SQLOperator)
         */

        PostgreSubQueryLimitAble<C> orderBy(Expression<?> orderExp);


        PostgreSubQueryLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);


        PostgreSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);


        PostgreSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate
                , Function<C, List<Expression<?>>> expFunction);

    }

    interface PostgreSubQueryLimitAble<C> extends PostgreSubQueryComposeAble<C> {

        PostgreSubQueryComposeAble<C> limit(int rowCount);

        PostgreSubQueryComposeAble<C> limit(int offset, int rowCount);

        PostgreSubQueryComposeAble<C> limit(Function<C, Pair<Integer, Integer>> function);

        PostgreSubQueryComposeAble<C> ifLimit(Predicate<C> predicate, int rowCount);

        PostgreSubQueryComposeAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        PostgreSubQueryComposeAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

    interface PostgreSubQueryComposeAble<C> extends PostgreSubQueryAble {

        PostgreSubQueryComposeAble<C> brackets();

        <S extends Select> PostgreSubQueryComposeAble<C> union(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> unionAll(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> unionDistinct(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> intersect(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> intersectAll(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> intersectDistinct(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> except(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> exceptAll(Function<C, S> function);

        <S extends Select> PostgreSubQueryComposeAble<C> exceptDistinct(Function<C, S> function);

    }


}
