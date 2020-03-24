package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.Postgres;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @see WithSubQuery
 * @see <a href="https://www.postgresql.org/docs/12/sql-select.html">Postgre Select.</a>
 */
@SuppressWarnings("unused")
public interface PostgreSelect extends Select {


    interface PostgreSelectSQLAble extends SelectSQLAble {

    }

    interface PostgreWithAble<C> extends PostgreSelectPartAble<C> {

        PostgreSelectPartAble<C> with();

        PostgreSelectPartAble<C> withRecursive();
    }

    interface PostgreNoJoinWithAble<C> extends PostgreNoJoinSelectPartAble<C> {

        PostgreNoJoinSelectPartAble<C> with();

        PostgreNoJoinSelectPartAble<C> withRecursive();
    }

    interface PostgreNoJoinSelectPartAble<C> extends NoJoinSelectPartAble<C>, PostgreSelectSQLAble {

        @Override
        <S extends SelectPart> PostgreNoJoinFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        @Override
        PostgreNoJoinFromAble<C> select(Distinct distinct, SelectPart selectPart);

        @Override
        PostgreNoJoinFromAble<C> select(SelectPart selectPart);

        @Override
        <S extends SelectPart> PostgreNoJoinFromAble<C> select(Distinct distinct, List<S> selectPartList);

        @Override
        <S extends SelectPart> PostgreNoJoinFromAble<C> select(List<S> selectPartList);


        /**
         * @see <a href="https://www.postgresql.org/docs/12/sql-select.html">Postgre distinct clause of select.</a>
         */
        <S extends SelectPart> PostgreNoJoinFromAble<C> selectDistinct(Function<C, List<IPredicate>> onExpsFunction
                , Function<C, List<S>> selectParsFunction);

        /**
         * @see <a href="https://www.postgresql.org/docs/12/sql-select.html">Postgre distinct clause of select.</a>
         */
        <S extends SelectPart> PostgreNoJoinFromAble<C> selectDistinct(Function<C, List<IPredicate>> onFunction
                , S selectPart);

    }


    interface PostgreSelectPartAble<C> extends SelectPartAble<C>, PostgreNoJoinSelectPartAble<C> {

        @Override
        <S extends SelectPart> PostgreFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        @Override
        PostgreFromAble<C> select(Distinct distinct, SelectPart selectPart);

        @Override
        PostgreFromAble<C> select(SelectPart selectPart);

        @Override
        <S extends SelectPart> PostgreFromAble<C> select(Distinct distinct, List<S> selectPartList);

        @Override
        <S extends SelectPart> PostgreFromAble<C> select(List<S> selectPartList);

        @Override
        <S extends SelectPart> PostgreFromAble<C> selectDistinct(Function<C, List<IPredicate>> onExpsFunction
                , Function<C, List<S>> selectParsFunction);

        @Override
        <S extends SelectPart> PostgreFromAble<C> selectDistinct(Function<C, List<IPredicate>> onFunction
                , S selectPart);
    }


    interface PostgreNoJoinFromAble<C> extends NoJoinFromAble<C>, PostgreWhereAble<C> {

        @Override
        PostgreWhereAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreWhereAble<C> from(Function<C, SubQuery> function, String subQueryAlia);

        PostgreWhereAble<C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreWhereAble<C> fromWithQuery(String withSubQueryName);

        PostgreWhereAble<C> fromFunc(Function<C, PostgreFuncTable> funcFunction);
    }


    interface PostgreFromAble<C> extends FromAble<C>, PostgreNoJoinFromAble<C> {

        @Override
        PostgreTableSampleAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreJoinAble<C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> fromWithQuery(String withSubQueryName);

        PostgreJoinAble<C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }

    interface PostgreTableSampleAble<C> extends PostgreJoinAble<C> {

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);

    }

    interface PostgreJoinAble<C> extends JoinAble<C>, PostgreWhereAble<C> {

        @Override
        PostgreTableSampleOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> leftJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        @Override
        PostgreTableSampleOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> joinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> joinWithQuery(String withSubQueryName);

        PostgreOnAble<C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        @Override
        PostgreTableSampleOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        @Override
        PostgreOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> rightJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreTableSampleOnAble<C> fullJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> fullJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> fullJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }


    interface PostgreOnAble<C> extends OnAble<C>, PostgreSelectSQLAble {

        @Override
        PostgreJoinAble<C> on(List<IPredicate> predicateList);

        @Override
        PostgreJoinAble<C> on(IPredicate predicate);

        @Override
        PostgreJoinAble<C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreTableSampleOnAble<C> extends PostgreOnAble<C> {

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);

    }


    interface PostgreWhereAble<C> extends WhereAble<C>, PostgreGroupByAble<C> {

        @Override
        PostgreGroupByAble<C> where(List<IPredicate> predicateList);

        @Override
        PostgreGroupByAble<C> where(Function<C, List<IPredicate>> function);

        @Override
        PostgreWhereAndAble<C> where(IPredicate predicate);
    }


    interface PostgreWhereAndAble<C> extends WhereAndAble<C>, PostgreGroupByAble<C> {

        @Override
        PostgreWhereAndAble<C> and(IPredicate predicate);

        @Override
        PostgreWhereAndAble<C> and(Function<C, IPredicate> function);

        @Override
        PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        @Override
        PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface PostgreGroupByAble<C> extends GroupByAble<C>, PostgreWindowAble<C> {

        @Override
        PostgreHavingAble<C> groupBy(Expression<?> groupExp);

        @Override
        PostgreHavingAble<C> groupBy(List<Expression<?>> groupExpList);

        @Override
        PostgreHavingAble<C> groupBy(Function<C, List<Expression<?>>> function);

        @Override
        PostgreHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        @Override
        PostgreHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreHavingAble<C> extends HavingAble<C>, PostgreWindowAble<C> {
        @Override
        PostgreOrderByAble<C> having(IPredicate predicate);

        @Override
        PostgreOrderByAble<C> having(Function<C, List<IPredicate>> function);

        @Override
        PostgreOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        @Override
        PostgreOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface PostgreWindowAble<C> extends PostgreOrderByAble<C> {

        PostgreOrderByAble<C> window(Function<C, List<PostgreWindow>> windowListFunction);

    }

    interface PostgreOrderByAble<C> extends OrderByAble<C>, PostgreLimitAble<C> {

        @Override
        PostgreLimitAble<C> orderBy(Expression<?> orderExp);

        @Override
        PostgreLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        @Override
        PostgreLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        @Override
        PostgreLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreLimitAble<C> extends LimitAble<C>, PostgreLockAble<C> {

        @Override
        PostgreLockAble<C> limit(int rowCount);

        @Override
        PostgreLockAble<C> limit(int offset, int rowCount);

        @Override
        PostgreLockAble<C> limit(Function<C, Pair<Integer, Integer>> function);

        @Override
        PostgreLockAble<C> ifLimit(Predicate<C> predicate, int rowCount);

        @Override
        PostgreLockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        @Override
        PostgreLockAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }


    interface PostgreLockAble<C> extends LockAble<C>, PostgreSelectSQLAble {

    }

    interface PostgreWindow extends SelfDescribed {

        String windowName();
    }

    interface PostgreWindowClauseAble extends PostgreSelectSQLAble {

        PostgreWindow asWindow();

    }

    /**
     * @param <C> criteria for dynamic  {@link PostgreWindow window}.
     * @see Postgres#window()
     * @see Postgres#window(Object)
     */
    interface PostgreWindowNameAble<C> extends PostgreWindowRefAble<C> {

        PostgreWindowRefAble<C> windowName(String name);

    }

    interface PostgreWindowRefAble<C> extends PostgreWindowPartitionByAble<C> {

        PostgreWindowPartitionByAble<C> ref(String existingWindowName);

        PostgreWindowPartitionByAble<C> ifRef(Predicate<C> predicate, String existingWindowName);
    }

    interface PostgreWindowPartitionByAble<C> extends PostgreWindowOrderByAble<C> {

        PostgreWindowOrderByAble<C> partitionBy(Expression<?> partitionExp);

        PostgreWindowOrderByAble<C> partitionBy(Function<C, List<Expression<?>>> function);

        PostgreWindowOrderByAble<C> ifPartitionBy(Predicate<C> predicate, Expression<?> partitionExp);

        PostgreWindowOrderByAble<C> ifPartitionBy(Predicate<C> predicate, Function<C, List<Expression<?>>> function);

    }

    interface PostgreWindowOrderByAble<C> extends PostgreWindowFrameAble<C> {

        PostgreWindowFrameAble<C> orderBy(Expression<?> expression);

        PostgreWindowFrameAble<C> orderBy(Function<C, List<Expression<?>>> function);

        PostgreWindowFrameAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> expression);

        PostgreWindowFrameAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> function);

    }

    interface PostgreWindowFrameAble<C> extends PostgreWindowFrameStartEndAble<C> {

        PostgreWindowFrameStartEndAble<C> range();

        PostgreWindowFrameStartEndAble<C> rows();

        PostgreWindowFrameStartEndAble<C> groups();

    }

    interface PostgreWindowFrameStartEndAble<C> extends PostgreWindowFrameExclusion {

        PostgreWindowFrameExclusion startPreceding(Long offset);

        PostgreWindowFrameExclusion startFollowing(Long offset);

        PostgreWindowFrameExclusion startPreceding();

        PostgreWindowFrameExclusion startCurrentRow();

        PostgreWindowFrameExclusion startFollowing();

        PostgreWindowFrameExclusion between(Long startOffset, Long endOffset);

        PostgreWindowFrameExclusion betweenPreceding(Long endOffset);

        PostgreWindowFrameExclusion betweenFollowing(Long startOffset);

        PostgreWindowFrameExclusion betweenPrecedingAndFollowing();

        PostgreWindowFrameExclusion betweenPrecedingAndCurrentRow();

        PostgreWindowFrameExclusion betweenCurrentRowAndFollowing();
    }

    interface PostgreWindowFrameExclusion extends PostgreWindowClauseAble {

        PostgreWindowClauseAble excludeCurrentRow();

        PostgreWindowClauseAble excludeGroup();

        PostgreWindowClauseAble excludeTies();

        PostgreWindowClauseAble excludeNoOthers();
    }

}
