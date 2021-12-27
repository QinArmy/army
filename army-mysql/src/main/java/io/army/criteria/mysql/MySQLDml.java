package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface MySQLDml extends DialectStatement, Dml {


    interface SingleIndexHintCommandClause<C, S> {

        SingleIndexWordClause<S> use();

        SingleIndexWordClause<S> ignore();

        SingleIndexWordClause<S> force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        SingleIndexWordClause<S> ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        SingleIndexWordClause<S> ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        SingleIndexWordClause<S> ifForce(Predicate<C> predicate);

    }


    interface SingleIndexWordClause<S> {

        SingleOrderByClause<S> index();

        SingleOrderByClause<S> key();

        S index(List<String> indexNameList);

        S key(List<String> indexNameList);

    }


    interface SingleOrderByClause<S> {

        S forOrderBy(List<String> indexNameList);

    }

    interface SingleWhereAndSpec<C, D extends Dml> extends MySQLDml.SingleOrderBySpec<C, D>, Dml.WhereAndSpec<C, D> {

        @Override
        SingleWhereAndSpec<C, D> and(IPredicate predicate);

        @Override
        SingleWhereAndSpec<C, D> and(Function<C, IPredicate> function);

        @Override
        SingleWhereAndSpec<C, D> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        @Override
        SingleWhereAndSpec<C, D> ifAnd(@Nullable IPredicate predicate);

        @Override
        SingleWhereAndSpec<C, D> ifAnd(Function<C, IPredicate> function);

        @Override
        SingleWhereAndSpec<C, D> ifAnd(Supplier<IPredicate> supplier);

    }

    interface SingleOrderBySpec<C, D extends Dml> extends MySQLDml.SingleLimitSpec<C, D> {

        SingleLimitSpec<C, D> orderBy(SortPart sortPart);

        SingleLimitSpec<C, D> orderBy(SortPart sortPart1, SortPart sortPart2);

        SingleLimitSpec<C, D> orderBy(List<SortPart> sortPartList);

        SingleLimitSpec<C, D> orderBy(Function<C, List<SortPart>> function);

        SingleLimitSpec<C, D> orderBy(Supplier<List<SortPart>> supplier);

        SingleLimitSpec<C, D> ifOrderBy(@Nullable SortPart sortPart);

        SingleLimitSpec<C, D> ifOrderBy(Supplier<List<SortPart>> supplier);

        SingleLimitSpec<C, D> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface SingleLimitSpec<C, D extends Dml> extends Dml.DmlSpec<D> {

        DmlSpec<D> limit(long rowCount);

        DmlSpec<D> limit(Function<C, Long> function);

        DmlSpec<D> limit(Supplier<Long> supplier);

        DmlSpec<D> ifLimit(Function<C, Long> function);

        DmlSpec<D> ifLimit(Supplier<Long> supplier);

    }


    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#multiUpdate80(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     * @param <S> below types:
     *            <ul>
     *               <li>{@link MySQLDelete.JoinSpec}</li>
     *               <li>{@link MySQLDelete.OnSpec}</li>
     *               <li>{@link MySQLDelete.BatchJoinSpec}</li>
     *               <li>{@link MySQLDelete.BatchOnSpec}</li>
     *            </ul>
     */
    interface MultiIndexHintCommandClause<C, S> {

        MultiIndexWordClause<S> use();

        MultiIndexWordClause<S> ignore();

        MultiIndexWordClause<S> force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        MultiIndexWordClause<S> ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        MultiIndexWordClause<S> ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        MultiIndexWordClause<S> ifForce(Predicate<C> predicate);

    }

    /**
     * @param <S> below types:
     *            <ul>
     *               <li>{@link MySQLDelete.JoinSpec}</li>
     *               <li>{@link MySQLDelete.OnSpec}</li>
     *               <li>{@link MySQLDelete.BatchJoinSpec}</li>
     *               <li>{@link MySQLDelete.BatchOnSpec}</li>
     *            </ul>
     */
    interface MultiIndexWordClause<S> {

        IndexPurposeClause<S> index();

        IndexPurposeClause<S> key();

        S index(List<String> indexNameList);

        S key(List<String> indexNameList);

    }

    /**
     * @param <S> below types:
     *            <ul>
     *               <li>{@link MySQLDelete.JoinSpec}</li>
     *               <li>{@link MySQLDelete.OnSpec}</li>
     *               <li>{@link MySQLDelete.BatchJoinSpec}</li>
     *               <li>{@link MySQLDelete.BatchOnSpec}</li>
     *            </ul>
     */
    interface IndexPurposeClause<S> {

        S forOrderBy(List<String> indexNameList);

        S forJoin(List<String> indexNameList);
    }


}
