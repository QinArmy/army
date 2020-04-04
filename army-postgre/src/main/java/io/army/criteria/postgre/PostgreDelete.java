package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface PostgreDelete extends Delete {

    /*################################## blow interfaces ##################################*/

    interface PostgreDeleteSQLAble extends DeleteSQLAble {

    }

    interface PostgreWithQueryAble extends PostgreDeleteSQLAble {

        PostgreWithQuery asWithQuery(String withQueryName) throws PostgreWithQueryException;
    }

    interface PostgreDeleteAble extends DeleteAble {

        @Override
        PostgreDelete asDelete();

    }

    interface PostgreWithAble<C> extends PostgreDeleteSQLAble {

        PostgreSingleDeleteAble<C> with(Function<C, List<PostgreWithQuery>> function);

        PostgreSingleDeleteAble<C> withRecursive(Function<C, List<PostgreWithQuery>> function);

    }


    interface PostgreSingleDeleteAble<C> extends PostgreDeleteSQLAble {

        PostgreUsingListAble<C> deleteFrom(TableMeta<?> tableMeta, String tableAlias);
    }

    interface PostgreUsingListAble<C> extends PostgreWhereAble<C> {

        PostgreUsingTableSampleAble<C> usingList(TableMeta<?> tableMeta, String tableAlias);

        PostgreJoinAble<C> usingList(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> usingListLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> usingListWithQuery(String withSubQueryName);

        PostgreJoinAble<C> usingListAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

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

    interface PostgreOnAble<C> extends PostgreDeleteSQLAble {

        PostgreJoinAble<C> on(List<IPredicate> predicateList);

        PostgreJoinAble<C> on(IPredicate predicate);

        PostgreJoinAble<C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreUsingTableSampleAble<C> extends PostgreSelect.PostgreJoinAble<C> {

        PostgreSelect.PostgreJoinAble<C> tableSampleAfterUsing(Function<C, Expression<?>> samplingMethodFunction);

        PostgreSelect.PostgreJoinAble<C> tableSampleAfterUsing(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreSelect.PostgreJoinAble<C> tableSampleAfterUsing(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreTableSampleOnAble<C> extends PostgreOnAble<C> {

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreWhereAble<C> extends PostgreDeleteSQLAble {

        PostgreReturningAble<C> where(List<IPredicate> predicateList);

        PostgreReturningAble<C> where(Function<C, List<IPredicate>> function);

        PostgreWhereAndAble<C> where(IPredicate predicate);
    }


    interface PostgreWhereAndAble<C> extends PostgreReturningAble<C> {

        PostgreReturningAble<C> and(IPredicate predicate);

        PostgreReturningAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        PostgreReturningAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface PostgreReturningAble<C> extends PostgreDeleteAble {

        PostgreWithQueryAble returning(List<SelectPart> selectPartList);

        PostgreWithQueryAble returning(Function<C, List<SelectPart>> function);

    }

}
