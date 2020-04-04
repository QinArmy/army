package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.Postgres;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface PostgreRowSubQuery extends PostgreSubQuery, RowSubQuery {


    /*################################## blow  interfaces  ##################################*/

    interface PostgreRowSubQueryAble extends PostgreSubQueryAble {

        @Override
        PostgreRowSubQuery asSubQuery();
    }


    interface PostgreRowSubQuerySelectPartAble<C> extends PostgreSubQuerySQLAble {

        <S extends SelectPart> PostgreRowSubQueryFromAble<C> selectDistinct(Function<C, List<S>> function);

        PostgreRowSubQueryFromAble<C> selectDistinct(SelectPart selectPart);

        PostgreRowSubQueryFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> PostgreRowSubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> PostgreRowSubQueryFromAble<C> select(List<S> selectPartList);

        <S extends SelectPart> PostgreRowSubQueryFromAble<C> selectDistinct(Function<C, List<Expression<?>>> onExpsFunction
                , Function<C, List<S>> selectParsFunction);

        <S extends SelectPart> PostgreRowSubQueryFromAble<C> selectDistinct(Function<C, List<Expression<?>>> onFunction
                , S selectPart);
    }


    interface PostgreRowSubQueryFromAble<C> extends PostgreRowSubQueryWhereAble<C> {

        PostgreRowSubQueryFromTableSampleAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        PostgreRowSubQueryJoinAble<C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreRowSubQueryJoinAble<C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreRowSubQueryJoinAble<C> fromWithQuery(String withSubQueryName);

        PostgreRowSubQueryWhereAble<C> fromFunc(Function<C, PostgreFuncTable> funcFunction);

        PostgreRowSubQueryJoinAble<C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);
    }


    interface PostgreRowSubQueryJoinAble<C> extends PostgreRowSubQueryWhereAble<C> {

        PostgreRowSubQueryTableSampleOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreRowSubQueryOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreRowSubQueryOnAble<C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreRowSubQueryOnAble<C> leftJoinWithQuery(String withSubQueryName);

        PostgreRowSubQueryOnAble<C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreRowSubQueryTableSampleOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        PostgreRowSubQueryOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        PostgreRowSubQueryOnAble<C> joinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreRowSubQueryOnAble<C> joinWithQuery(String withSubQueryName);

        PostgreRowSubQueryOnAble<C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreRowSubQueryTableSampleOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreRowSubQueryOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreRowSubQueryOnAble<C> rightJoinWithQuery(String withSubQueryName);

        PostgreRowSubQueryOnAble<C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreRowSubQueryTableSampleOnAble<C> fullJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreRowSubQueryOnAble<C> fullJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreRowSubQueryOnAble<C> fullJoinWithQuery(String withSubQueryName);

        PostgreRowSubQueryOnAble<C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }

    interface PostgreRowSubQueryFromTableSampleAble<C> extends PostgreRowSubQueryJoinAble<C> {

        PostgreRowSubQueryJoinAble<C> tableSampleAfterFrom(Function<C, Expression<?>> samplingMethodFunction);

        PostgreRowSubQueryJoinAble<C> tableSampleAfterFrom(Function<C, Expression<?>> samplingMethodFunction
                , Expression<Double> seedExp);

        PostgreRowSubQueryJoinAble<C> tableSampleAfterFrom(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }


    interface PostgreRowSubQueryTableSampleOnAble<C> extends PostgreRowSubQueryOnAble<C> {

        PostgreRowSubQueryOnAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreRowSubQueryOnAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreRowSubQueryOnAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreRowSubQueryOnAble<C> extends PostgreSubQuerySQLAble {

        PostgreRowSubQueryJoinAble<C> on(List<IPredicate> predicateList);

        PostgreRowSubQueryJoinAble<C> on(IPredicate predicate);

        PostgreRowSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreRowSubQueryWhereAble<C> extends PostgreRowSubQueryGroupByAble<C> {

        PostgreRowSubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        PostgreRowSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        PostgreRowSubQueryWhereAndAble<C> where(IPredicate predicate);
    }


    interface PostgreRowSubQueryWhereAndAble<C> extends PostgreRowSubQueryGroupByAble<C> {

        PostgreRowSubQueryWhereAndAble<C> and(IPredicate predicate);

        PostgreRowSubQueryWhereAndAble<C> and(Function<C, IPredicate> function);

        PostgreRowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        PostgreRowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface PostgreRowSubQueryGroupByAble<C> extends PostgreRowSubQueryWindowAble<C> {

        PostgreRowSubQueryHavingAble<C> groupBy(Expression<?> groupExp);


        PostgreRowSubQueryHavingAble<C> groupBy(List<Expression<?>> groupExpList);


        PostgreRowSubQueryHavingAble<C> groupBy(Function<C, List<Expression<?>>> function);


        PostgreRowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);


        PostgreRowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate
                , Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreRowSubQueryHavingAble<C> extends PostgreRowSubQueryWindowAble<C> {

        PostgreRowSubQueryWindowAble<C> having(IPredicate predicate);


        PostgreRowSubQueryWindowAble<C> having(Function<C, List<IPredicate>> function);


        PostgreRowSubQueryWindowAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);


        PostgreRowSubQueryWindowAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }

    interface PostgreRowSubQueryWindowAble<C> extends PostgreRowSubQueryOrderByAble<C> {

        /**
         * @see Postgres#window()
         * @see Postgres#window(Object)
         */
        PostgreRowSubQueryOrderByAble<C> window(Function<C, List<PostgreWindow>> windowListFunction);
    }


    interface PostgreRowSubQueryOrderByAble<C> extends PostgreRowSubQueryLimitAble<C> {
        /**
         * @see Postgres#nullsFirst(Expression)
         * @see Postgres#nullsLast(Expression)
         * @see Postgres#sortUsing(Expression, SQLOperator)
         */

        PostgreRowSubQueryLimitAble<C> orderBy(Expression<?> orderExp);


        PostgreRowSubQueryLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);


        PostgreRowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);


        PostgreRowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate
                , Function<C, List<Expression<?>>> expFunction);

    }

    interface PostgreRowSubQueryLimitAble<C> extends PostgreRowSubQuery {

        PostgreRowSubQuery limitOne();

    }


}
