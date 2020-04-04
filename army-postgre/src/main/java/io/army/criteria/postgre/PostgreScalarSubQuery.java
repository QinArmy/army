package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface PostgreScalarSubQuery<E> extends ScalarSubQuery<E>, PostgreColumnSubQuery<E>, PostgreRowSubQuery {


    interface PostgreScalarSubQueryAble<E> extends PostgreColumnSubQueryAble<E> {

        PostgreScalarSubQuery<E> asSubQuery();

    }

    interface PostgreScalarSubQuerySelectPartAble<E, C> extends PostgreSubQuerySQLAble {

        PostgreScalarSubQueryFromAble<E, C> selectDistinct(Selection selection);

        PostgreScalarSubQueryFromAble<E, C> select(Selection selection);

    }


    interface PostgreScalarSubQueryFromAble<E, C> extends PostgreScalarSubQueryAble<E> {

        PostgreSubQueryTableSampleOnAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        PostgreScalarSubQueryJoinAble<E, C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreScalarSubQueryJoinAble<E, C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreScalarSubQueryJoinAble<E, C> fromWithQuery(String withSubQueryName);

        PostgreSubQueryWhereAble<C> fromFunc(Function<C, PostgreFuncTable> funcFunction);

        PostgreScalarSubQueryJoinAble<E, C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }


    interface PostgreScalarSubQueryJoinAble<E, C> extends PostgreScalarSubQueryWhereAble<E, C> {

        PostgreScalarSubQueryTableSampleOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreScalarSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreScalarSubQueryOnAble<E, C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreScalarSubQueryOnAble<E, C> leftJoinWithQuery(String withSubQueryName);

        PostgreScalarSubQueryOnAble<E, C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreScalarSubQueryTableSampleOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias);

        PostgreScalarSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia);

        PostgreScalarSubQueryOnAble<E, C> joinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreScalarSubQueryOnAble<E, C> joinWithQuery(String withSubQueryName);

        PostgreScalarSubQueryOnAble<E, C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreScalarSubQueryTableSampleOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreScalarSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreScalarSubQueryOnAble<E, C> rightJoinWithQuery(String withSubQueryName);

        PostgreScalarSubQueryOnAble<E, C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreScalarSubQueryTableSampleOnAble<E, C> fullJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreScalarSubQueryOnAble<E, C> fullJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreScalarSubQueryOnAble<E, C> fullJoinWithQuery(String withSubQueryName);

        PostgreScalarSubQueryOnAble<E, C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);
    }


    interface PostgreScalarSubQueryTableSampleOnAble<E, C> extends PostgreScalarSubQueryOnAble<E, C> {

        PostgreScalarSubQueryOnAble<E, C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreScalarSubQueryOnAble<E, C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreScalarSubQueryOnAble<E, C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreScalarSubQueryOnAble<E, C> extends PostgreScalarSubQueryAble<E> {

        PostgreScalarSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        PostgreScalarSubQueryJoinAble<E, C> on(IPredicate predicate);

        PostgreScalarSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreScalarSubQueryWhereAble<E, C> extends PostgreScalarSubQueryGroupByAble<E, C> {

        PostgreScalarSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList);

        PostgreScalarSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function);

        PostgreScalarSubQueryWhereAndAble<E, C> where(IPredicate predicate);
    }


    interface PostgreScalarSubQueryWhereAndAble<E, C> extends PostgreScalarSubQueryGroupByAble<E, C> {

        PostgreScalarSubQueryWhereAndAble<E, C> and(IPredicate predicate);

        PostgreScalarSubQueryWhereAndAble<E, C> and(Function<C, IPredicate> function);

        PostgreScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        PostgreScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface PostgreScalarSubQueryGroupByAble<E, C> extends PostgreScalarSubQueryWindowAble<E, C> {


        PostgreScalarSubQueryHavingAble<E, C> groupBy(Expression<?> groupExp);

        PostgreScalarSubQueryHavingAble<E, C> groupBy(List<Expression<?>> groupExpList);

        PostgreScalarSubQueryHavingAble<E, C> groupBy(Function<C, List<Expression<?>>> function);

        PostgreScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        PostgreScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreScalarSubQueryHavingAble<E, C> extends PostgreScalarSubQueryWindowAble<E, C> {

        PostgreScalarSubQueryWindowAble<E, C> having(IPredicate predicate);

        PostgreScalarSubQueryWindowAble<E, C> having(Function<C, List<IPredicate>> function);

        PostgreScalarSubQueryWindowAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        PostgreScalarSubQueryWindowAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }

    interface PostgreScalarSubQueryWindowAble<E, C> extends PostgreScalarSubQueryOrderByAble<E, C> {

        PostgreScalarSubQueryOrderByAble<E, C> window(Function<C, List<PostgreWindow>> windowListFunction);
    }


    interface PostgreScalarSubQueryOrderByAble<E, C> extends PostgreScalarSubQueryLimitAble<E, C> {

        PostgreScalarSubQueryLimitAble<E, C> orderBy(Expression<?> orderExp);

        PostgreScalarSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function);

        PostgreScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        PostgreScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate
                , Function<C, List<Expression<?>>> expFunction);
    }

    interface PostgreScalarSubQueryLimitAble<E, C> extends PostgreScalarSubQueryAble<E> {

        PostgreScalarSubQueryAble<E> limitOne();

    }

}
