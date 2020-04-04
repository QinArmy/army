package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface PostgreUpdate extends Update {


    /*################################## blow interfaces  ##################################*/

    interface PostgreUpdateSQLAble extends UpdateSQLAble {

    }

    interface PostgreWithQueryAble extends PostgreUpdateSQLAble {

        PostgreWithQuery asWithQuery(String withQueryName) throws PostgreWithQueryException;
    }

    interface PostgreUpdateAble extends UpdateAble, PostgreUpdateSQLAble {

        @Override
        PostgreUpdate asUpdate();

    }

    interface PostgreWithAble<C> extends PostgreSingleUpdateAble<C> {

        PostgreSingleUpdateAble<C> with(Function<C, List<PostgreWithQuery>> function);

        PostgreSingleUpdateAble<C> withRecursive(Function<C, List<PostgreWithQuery>> function);

    }


    interface PostgreSingleUpdateAble<C> extends PostgreUpdateSQLAble {

        PostgreSetAble<C> update(TableMeta<?> tableMeta, String tableAlias);
    }

    interface PostgreSetAble<C> extends PostgreUpdateSQLAble {

        <F> PostgreSetWhereAble<C> set(FieldMeta<? extends IDomain, F> target, F value);

        /**
         * @see SQLS#defaultValue()
         */
        <F> PostgreSetWhereAble<C> set(FieldMeta<? extends IDomain, F> target, Expression<F> valueExp);

        /**
         * @see SQLS#scalarSubQuery(Class)
         */
        <F> PostgreSetWhereAble<C> set(FieldMeta<? extends IDomain, F> target, Function<C, Expression<F>> function);

        <T extends IDomain, S extends RowSubQuery> PostgreFromListAble<C> set(List<FieldMeta<T, ?>> targetFieldList
                , Function<C, S> function);

    }

    interface PostgreSetWhereAble<C> extends PostgreSetAble<C>, PostgreFromListAble<C> {


        <F> PostgreSetWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target, F value);

        <F> PostgreSetWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
                , Expression<F> valueExp);

        <F> PostgreSetWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
                , Function<C, Expression<F>> valueExpFunction);

        <T extends IDomain, S extends RowSubQuery> PostgreSetWhereAble<C> ifSet(Predicate<C> predicate
                , List<FieldMeta<T, ?>> targetFieldList, Function<C, S> function);
    }


    interface PostgreFromListAble<C> extends PostgreWhereAble<C> {

        PostgreTableSampleAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        PostgreJoinAble<C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> fromLateral(Function<C, SubQuery> subQueryFunction, String subQueryAlia);

        PostgreJoinAble<C> fromWithQuery(String withSubQueryName);

        PostgreJoinAble<C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }

    interface PostgreJoinAble<C> extends PostgreWhereAble<C> {

        PostgreTableSampleAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> leftJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreTableSampleAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> joinLateral(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> joinWithQuery(String withSubQueryName);

        PostgreOnAble<C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreTableSampleAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> rightJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

        PostgreTableSampleAble<C> fullJoin(TableMeta<?> tableMeta, String tableAlias);

        PostgreOnAble<C> fullJoin(Function<C, SubQuery> function, String subQueryAlia);

        PostgreOnAble<C> fullJoinWithQuery(String withSubQueryName);

        PostgreOnAble<C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction);

    }

    interface PostgreOnAble<C> extends PostgreUpdateSQLAble {

        PostgreJoinAble<C> on(List<IPredicate> predicateList);

        PostgreJoinAble<C> on(IPredicate predicate);

        PostgreJoinAble<C> on(Function<C, List<IPredicate>> function);
    }

    interface PostgreTableSampleAble<C> extends PostgreOnAble<C> {

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction, Expression<Double> seedExp);

        PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
                , Function<C, Expression<Double>> seedFunction);
    }

    interface PostgreWhereAble<C> extends PostgreUpdateSQLAble {

        PostgreReturningAble<C> where(List<IPredicate> predicateList);

        PostgreReturningAble<C> where(Function<C, List<IPredicate>> function);

        PostgreWhereAndAble<C> where(IPredicate predicate);
    }


    interface PostgreWhereAndAble<C> extends PostgreReturningAble<C> {

        PostgreWhereAndAble<C> and(IPredicate predicate);

        PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }

    interface PostgreReturningAble<C> extends PostgreUpdateAble {

        PostgreWithQueryAble returning(List<SelectPart> selectPartList);

        PostgreWithQueryAble returning(Function<C, List<SelectPart>> function);

    }

}
