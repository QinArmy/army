package io.army.criteria;

import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <E> {@link ScalarSubQuery#selection()}'s Java Type.
 */
public interface ScalarSubQuery<E> extends ColumnSubQuery<E>, RowSubQuery, Expression<E> {


    interface ScalarSubQueryAble<E> extends SubQueryAble {

        ScalarSubQuery<E> asSubQuery();
    }

    interface ScalarSubQuerySQLAble extends SQLAble {

    }

    interface ScalarSubQuerySelectionAble<E, C> extends ScalarSubQuerySQLAble {

        ScalarSubQuerySelectionAble<E, C> select(Distinct distinct, Selection selection);

        ScalarSubQuerySelectionAble<E, C> select(Selection selection);

    }

    interface ScalarSubQueryFromAble<E, C> extends ScalarSubQuerySQLAble, ScalarSubQueryAble<E> {

        ScalarSubQueryFromAble<E, C> from(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryFromAble<E, C> from(Function<C, SubQuery> function, String subQueryAlia);
    }


    interface ScalarSubQueryOnAble<E, C> extends ScalarSubQuerySQLAble {

        ScalarSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        ScalarSubQueryJoinAble<E, C> on(IPredicate predicate);

        ScalarSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);

    }

    interface ScalarSubQueryJoinAble<E, C> extends ScalarSubQueryWhereAble<E, C> {

        ScalarSubQueryOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        ScalarSubQueryOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia);

        ScalarSubQueryOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface ScalarSubQueryWhereAble<E, C> extends ScalarSubQueryGroupByAble<E, C> {

        ScalarSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList);

        ScalarSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function);

        ScalarSubQueryWhereAndAble<E, C> where(IPredicate predicate);
    }

    interface ScalarSubQueryWhereAndAble<E, C> extends ScalarSubQueryGroupByAble<E, C> {

        ScalarSubQueryWhereAndAble<E, C> and(IPredicate predicate);

        ScalarSubQueryWhereAndAble<E, C> and(Function<C, IPredicate> function);

        ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface ScalarSubQueryGroupByAble<E, C> extends ScalarSubQueryOrderByAble<E, C> {

        ScalarSubQueryHavingAble<E, C> groupBy(Expression<?> groupExp);

        ScalarSubQueryHavingAble<E, C> groupBy(Function<C, List<Expression<?>>> function);

        ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface ScalarSubQueryHavingAble<E, C> extends ScalarSubQueryOrderByAble<E, C> {

        ScalarSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function);

        ScalarSubQueryOrderByAble<E, C> having(IPredicate predicate);

        ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface ScalarSubQueryOrderByAble<E, C> extends ScalarSubQueryLimitAble<E, C> {

        ScalarSubQueryLimitAble<E, C> orderBy(Expression<?> groupExp);

        ScalarSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function);

        ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp);

        ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface ScalarSubQueryLimitAble<E, C> extends ScalarSubQuerySQLAble, ScalarSubQueryAble<E> {

        ScalarSubQueryAble<E> limit(int rowCount);

        ScalarSubQueryAble<E> limit(int offset, int rowCount);

        ScalarSubQueryAble<E> limit(Function<C, Pair<Integer, Integer>> function);

        ScalarSubQueryAble<E> ifLimit(Predicate<C> predicate, int rowCount);

        ScalarSubQueryAble<E> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        ScalarSubQueryAble<E> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

}
