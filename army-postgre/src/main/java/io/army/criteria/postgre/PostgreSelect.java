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

    interface PostgreWithQueryAble extends PostgreSelectSQLAble {

    }

    interface PostgreSelectAble extends SelectAble, PostgreSelectSQLAble {

        @Override
        PostgreSelect asSelect();

    }


    interface PostgreWithAble<C> extends PostgreSelectPartAble<C> {

        PostgreSelectPartAble<C> with(Function<C, List<PostgreWithQuery>> function);

        PostgreSelectPartAble<C> withRecursive(Function<C, List<PostgreWithQuery>> function);
    }


    interface PostgreSelectPartAble<C> extends PostgreSelectSQLAble {

        <S extends SelectPart> PostgreFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        PostgreFromAble<C> select(Distinct distinct, SelectPart selectPart);

        PostgreFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> PostgreFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> PostgreFromAble<C> select(List<S> selectPartList);

        <S extends SelectPart> PostgreFromAble<C> selectDistinct(Function<C, List<Expression<?>>> onExpsFunction
                , Function<C, List<S>> selectParsFunction);

        <S extends SelectPart> PostgreFromAble<C> selectDistinct(Function<C, List<Expression<?>>> onFunction
                , S selectPart);
    }


    interface PostgreFromAble<C> extends PostgreWhereAble<C> {

        PostgreTableSampleAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        PostgreJoinAble<C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> fromWithQuery(String withSubQueryName);

        PostgreWhereAble<C> fromFunc(Function<C, PostgreFuncTable> funcFunction);

        PostgreJoinAble<C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);
    }


    interface PostgreTableSampleAble<C> extends PostgreJoinAble<C> {

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreJoinAble<C> extends PostgreWhereAble<C> {

        PostgreTableSampleOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> leftJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreTableSampleOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> joinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> joinWithQuery(String withSubQueryName);

        PostgreOnAble<C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreTableSampleOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> rightJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreTableSampleOnAble<C> fullJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> fullJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> fullJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }


    interface PostgreOnAble<C> extends PostgreSelectSQLAble {

        PostgreJoinAble<C> on(List<IPredicate> predicateList);

        PostgreJoinAble<C> on(IPredicate predicate);

        PostgreJoinAble<C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreTableSampleOnAble<C> extends PostgreOnAble<C> {

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);

    }


    interface PostgreWhereAble<C> extends PostgreGroupByAble<C> {

        PostgreGroupByAble<C> where(List<IPredicate> predicateList);

        PostgreGroupByAble<C> where(Function<C, List<IPredicate>> function);

        PostgreWhereAndAble<C> where(IPredicate predicate);
    }


    interface PostgreWhereAndAble<C> extends PostgreGroupByAble<C> {

        PostgreWhereAndAble<C> and(IPredicate predicate);

        PostgreWhereAndAble<C> and(Function<C, IPredicate> function);

        PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface PostgreGroupByAble<C> extends PostgreWindowAble<C> {


        PostgreHavingAble<C> groupBy(Expression<?> groupExp);


        PostgreHavingAble<C> groupBy(List<Expression<?>> groupExpList);


        PostgreHavingAble<C> groupBy(Function<C, List<Expression<?>>> function);


        PostgreHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);


        PostgreHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreHavingAble<C> extends PostgreNoLockWindowAble<C> {

        PostgreNoLockWindowAble<C> having(IPredicate predicate);


        PostgreNoLockWindowAble<C> having(Function<C, List<IPredicate>> function);


        PostgreNoLockWindowAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);


        PostgreNoLockWindowAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }

    interface PostgreNoLockWindowAble<C> extends PostgreNoLockOrderByAble<C> {

        PostgreNoLockOrderByAble<C> window(Function<C, List<PostgreWindow>> windowListFunction);
    }


    interface PostgreWindowAble<C> extends PostgreOrderByAble<C>, PostgreNoLockWindowAble<C> {

        /**
         * @see Postgres#window()
         * @see Postgres#window(Object)
         */
        PostgreNoLockOrderByAble<C> window(Function<C, List<PostgreWindow>> windowListFunction);

    }


    interface PostgreNoLockOrderByAble<C> extends PostgreNoLockLimitAble<C> {

        /**
         * @see Postgres#nullsFirst(Expression)
         * @see Postgres#nullsLast(Expression)
         * @see Postgres#sortUsing(Expression, SQLOperator)
         */

        PostgreNoLockLimitAble<C> orderBy(Expression<?> orderExp);


        PostgreNoLockLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);


        PostgreNoLockLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);


        PostgreNoLockLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }

    /**
     * @see Postgres#nullsFirst(Expression)
     * @see Postgres#nullsLast(Expression)
     * @see Postgres#sortUsing(Expression, SQLOperator)
     */
    interface PostgreOrderByAble<C> extends PostgreLimitAble<C>, PostgreNoLockOrderByAble<C> {

        @Override
        PostgreLimitAble<C> orderBy(Expression<?> orderExp);

        @Override
        PostgreLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        @Override
        PostgreLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        @Override
        PostgreLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface PostgreNoLockLimitAble<C> extends PostgreComposeAble<C> {


        PostgreComposeAble<C> limit(int rowCount);


        PostgreComposeAble<C> limit(int offset, int rowCount);


        PostgreComposeAble<C> limit(Function<C, Pair<Integer, Integer>> function);


        PostgreComposeAble<C> ifLimit(Predicate<C> predicate, int rowCount);


        PostgreComposeAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);


        PostgreComposeAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }


    interface PostgreLimitAble<C> extends PostgreNoLockLimitAble<C>, PostgreLockAble<C> {

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

    interface PostgreNoComposeLockAble<C> extends PostgreSelectAble {

        PostgreLockOfTablesAble<C> lock(LockMode lockMode);


        PostgreLockOfTablesAble<C> lock(Function<C, LockMode> function);


        PostgreLockOfTablesAble<C> ifLock(Predicate<C> predicate, LockMode lockMode);


        PostgreLockOfTablesAble<C> ifLock(Predicate<C> predicate, Function<C, LockMode> function);

    }


    interface PostgreLockAble<C> extends PostgreComposeAble<C>, PostgreNoComposeLockAble<C> {


    }

    interface PostgreComposeAble<C> extends PostgreSelectAble {

        PostgreComposeAble<C> brackets();

        <S extends Select> PostgreComposeAble<C> union(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> unionAll(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> unionDistinct(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> intersect(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> intersectAll(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> intersectDistinct(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> except(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> exceptAll(Function<C, S> function);

        <S extends Select> PostgreComposeAble<C> exceptDistinct(Function<C, S> function);

    }

    interface PostgreLockOfTablesAble<C> extends PostgreLockOptionAble<C> {

        PostgreLockOptionAble<C> ofTable(TableMeta<?> lockTable);

        PostgreLockOptionAble<C> ofTable(List<TableMeta<?>> lockTableList);

        PostgreLockOptionAble<C> ifOfTable(Predicate<C> predicate, TableMeta<?> lockTable);

        PostgreLockOptionAble<C> ifOfTable(Predicate<C> predicate, Function<C, List<TableMeta<?>>> function);

    }

    interface PostgreLockOptionAble<C> extends PostgreLockAble<C> {

        PostgreNoComposeLockAble<C> noWait();

        PostgreNoComposeLockAble<C> skipLocked();
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

    /**
     * @see Postgres#nullsFirst(Expression)
     * @see Postgres#nullsLast(Expression)
     * @see Postgres#sortUsing(Expression, SQLOperator)
     */
    interface PostgreWindowOrderByAble<C> extends PostgreWindowFrameAble<C> {

        /**
         * @see Postgres#nullsFirst(Expression)
         * @see Postgres#nullsLast(Expression)
         * @see Postgres#sortUsing(Expression, SQLOperator)
         */
        PostgreWindowFrameAble<C> orderBy(Expression<?> expression);

        PostgreWindowFrameAble<C> orderBy(Function<C, List<Expression<?>>> function);

        PostgreWindowFrameAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> expression);

        PostgreWindowFrameAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> function);

    }

    interface PostgreWindowFrameAble<C> extends PostgreSelectSQLAble {

        PostgreWindowFrameRangeAble<C> range();

        PostgreWindowFrameRangeAble<C> rows();

        PostgreWindowFrameRangeAble<C> groups();

    }

    interface PostgreWindowFrameRangeAble<C> extends PostgreSelectSQLAble {

        /**
         * representing  {@code offset PRECEDING}
         */
        PostgreWindowFrameExclusion startPreceding(Long offset);

        /**
         * representing {@code offset FOLLOWING}
         */
        PostgreWindowFrameExclusion startFollowing(Long offset);

        /**
         * representing {@code UNBOUNDED PRECEDING}
         */
        PostgreWindowFrameExclusion startPreceding();

        /**
         * representing {@code CURRENT ROW}
         */
        PostgreWindowFrameExclusion startCurrentRow();

        /**
         * representing {@code BETWEEN UNBOUNDED PRECEDING AND offset PRECEDING}
         */
        PostgreWindowFrameExclusion betweenUnBoundedAndPreceding(Long offset);

        /**
         * representing {@code BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW}
         */
        PostgreWindowFrameExclusion betweenUnBoundedAndCurrentRow();

        /**
         * representing {@code BETWEEN UNBOUNDED PRECEDING AND offset FOLLOWING}
         */
        PostgreWindowFrameExclusion betweenUnBoundedAndFollowing(Long offset);

        /**
         * representing {@code BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING}
         */
        PostgreWindowFrameExclusion betweenUnBoundedAndUnBounded();

        /**
         * representing {@code BETWEEN offset PRECEDING AND CURRENT ROW}
         */
        PostgreWindowFrameExclusion betweenPrecedingAndCurrentRow(Long offset);

        /**
         * representing {@code BETWEEN offset PRECEDING AND offset FOLLOWING}
         */
        PostgreWindowFrameExclusion betweenPrecedingAndFollowing(Long start, Long end);

        /**
         * representing {@code BETWEEN offset PRECEDING AND UNBOUNDED FOLLOWING}
         */
        PostgreWindowFrameExclusion betweenPrecedingAndUnBounded(Long offset);

        /**
         * representing {@code BETWEEN CURRENT ROW AND offset FOLLOWING}
         */
        PostgreWindowFrameExclusion betweenCurrentAndFollowing(Long offset);

        /**
         * representing {@code BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING}
         */
        PostgreWindowFrameExclusion betweenCurrentAndUnBounded();

        /**
         * representing {@code BETWEEN offset FOLLOWING AND UNBOUNDED FOLLOWING}
         */
        PostgreWindowFrameExclusion betweenFollowingAndUnBounded(Long offset);

    }

    interface PostgreWindowFrameExclusion extends PostgreWindowClauseAble {

        PostgreWindowClauseAble excludeCurrentRow();

        PostgreWindowClauseAble excludeGroup();

        PostgreWindowClauseAble excludeTies();

        PostgreWindowClauseAble excludeNoOthers();
    }

}
