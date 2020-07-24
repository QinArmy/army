package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;

/**
 * @param <E> {@link ScalarSubQuery#selection()}'s Java Type.
 */
public interface ScalarSubQuery<E> extends ColumnSubQuery<E>, RowSubQuery, Expression<E> {

    /**
     * @return always true
     */
    @Override
    boolean containsSubQuery();

    /*################################## blow  interfaces  ##################################*/


    interface ScalarSubQuerySQLAble extends ColumnSubQuerySQLAble, RowSubQuerySQLAble {

    }

    interface ScalarSubQueryAble<E> extends ScalarSubQuerySQLAble, ColumnSubQueryAble<E>, RowSubQueryAble {

        ScalarSubQuery<E> asSubQuery();
    }


    /**
     * @param <C> @param <C> developer definite java type for dynamic sql
     */
    interface ScalarSubQuerySelectionAble<E, C> extends ScalarSubQuerySQLAble {

        ScalarSubQuerySelectionAble<E, C> select(Distinct distinct, Selection selection);

        ScalarSubQuerySelectionAble<E, C> select(Selection selection);

    }

    /**
     * @param <C> @param <C> developer definite java type for dynamic sql
     */
    interface ScalarSubQueryFromAble<E, C> extends ScalarSubQueryAble<E> {

        TableRouteJoinAble<E, C> from(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryFromAble<E, C> from(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteJoinAble<E, C> extends ScalarSubQueryJoinAble<E, C> {

        ScalarSubQueryJoinAble<E, C> fromRoute(int databaseIndex, int tableIndex);

        ScalarSubQueryJoinAble<E, C> fromRoute(int tableIndex);
    }

    interface ScalarSubQueryOnAble<E, C> extends ScalarSubQuerySQLAble {

        ScalarSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        ScalarSubQueryJoinAble<E, C> on(IPredicate predicate);

        ScalarSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);

    }

    interface ScalarSubQueryJoinAble<E, C> extends ScalarSubQueryWhereAble<E, C> {

        TableRouteOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        ScalarSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteOnAble<E, C> extends ScalarSubQueryOnAble<E, C> {

        ScalarSubQueryOnAble<E, C> route(int databaseIndex, int tableIndex);

        ScalarSubQueryOnAble<E, C> route(int tableIndex);
    }


    interface ScalarSubQueryWhereAble<E, C> extends ScalarSubQueryGroupByAble<E, C> {

        ScalarSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList);

        ScalarSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function);

        ScalarSubQueryWhereAndAble<E, C> where(IPredicate predicate);
    }

    interface ScalarSubQueryWhereAndAble<E, C> extends ScalarSubQueryGroupByAble<E, C> {

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        ScalarSubQueryWhereAndAble<E, C> and(@Nullable IPredicate predicate);

        ScalarSubQueryWhereAndAble<E, C> ifAnd(Function<C, IPredicate> function);

    }


    interface ScalarSubQueryGroupByAble<E, C> extends ScalarSubQueryOrderByAble<E, C> {

        ScalarSubQueryHavingAble<E, C> groupBy(SortPart sortPart);

        ScalarSubQueryHavingAble<E, C> groupBy(List<SortPart> sortPartList);

        ScalarSubQueryHavingAble<E, C> groupBy(Function<C, List<SortPart>> function);
    }

    interface ScalarSubQueryHavingAble<E, C> extends ScalarSubQueryOrderByAble<E, C> {

        ScalarSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function);

        ScalarSubQueryOrderByAble<E, C> having(IPredicate predicate);

        ScalarSubQueryOrderByAble<E, C> having(List<IPredicate> predicateList);
    }


    interface ScalarSubQueryOrderByAble<E, C> extends ScalarSubQueryLimitAble<E, C> {

        ScalarSubQueryLimitAble<E, C> orderBy(SortPart sortPart);

        ScalarSubQueryLimitAble<E, C> orderBy(List<SortPart> sortPartList);

        ScalarSubQueryLimitAble<E, C> orderBy(Function<C, List<SortPart>> function);

    }


    interface ScalarSubQueryLimitAble<E, C> extends ScalarSubQuerySQLAble, ScalarSubQueryAble<E> {

        ScalarSubQueryAble<E> limitOne();

    }

}
