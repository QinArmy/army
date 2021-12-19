package io.army.criteria.oracle;

import io.army.criteria.Expression;
import io.army.criteria.Select;

public interface OracleSelect extends Select {


    /*################################## blow oracle select clause  interfaces ##################################*/

/*

    interface WithAble<C> e {

        PLSQLDeclarationAble<C> with();
    }


    interface PLSQLDeclarationAble<C> extends QueryFactoryAble<C> {

        QueryFactoryClauseAble<C> declaration(OracleDeclaration declaration);

        QueryFactoryClauseAble<C> declaration(Function<C, OracleDeclaration> function);

        QueryFactoryClauseAble<C> declarationList(List<OracleDeclaration> declarationList);

        QueryFactoryClauseAble<C> declarationList(Function<C, List<OracleDeclaration>> function);
    }

    interface QueryFactoryAble<C> extends SelectSQLAble {

        SelectPartAble<C> factory(QueryFactory factory);

        SelectPartAble<C> factory(Function<C, QueryFactory> function);

        SelectPartAble<C> factoryList(List<QueryFactory> factoryList);

        SelectPartAble<C> factoryList(Function<C, List<QueryFactory>> function);
    }


    interface QueryFactoryClauseAble<C> extends QueryFactoryAble<C>, SelectPartAble<C> {

    }


    interface SelectPartAble<C> extends SelectSQLAble {

        <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        <S extends SelectPart> FromAble<C> select(Function<C, List<S>> function);

        FromAble<C> select(Distinct distinct, SelectPart selectPart);

        FromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> FromAble<C> select(List<S> selectPartList);
    }


    interface FromAble<C> extends UnionClause<C> {

        TableRouteAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        TableRouteAble<C> from(TableMeta<?> tableMeta, Function<C, TableOptionalClause> function, String tableAlias);

        JoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia);

        JoinAble<C> from(Function<C, SubQuery> function, SubQueryOptionalClause optionalClause, String subQueryAlia);

        JoinAble<C> from(String queryName, String queryAlias);

        JoinAble<C> from(String queryName, Function<C, TableOptionalClause> function, String queryAlias);

        JoinAble<C> fromCollection(Function<C, TableCollection> function, String collectionAlias);

        JoinAble<C> fromCollection(Function<C, TableCollection> function, Function<C, TableOptionalClause> clauseFunction, String collectionAlias);

    }

    interface TableRouteAble<C> extends JoinAble<C> {

        JoinAble<C> route(int databaseIndex, int tableIndex);

        JoinAble<C> route(int tableIndex);
    }


    interface OnAble<C> extends SelectSQLAble {

        JoinAble<C> on(List<IPredicate> predicateList);

        JoinAble<C> on(IPredicate predicate);

        JoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface JoinAble<C> extends WhereAble<C> {

        OnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        OnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface WhereAble<C> extends GroupByAble<C> {

        GroupByAble<C> where(List<IPredicate> predicateList);

        GroupByAble<C> where(Function<C, List<IPredicate>> function);

        WhereAndAble<C> where(IPredicate predicate);
    }

    interface WhereAndAble<C> extends GroupByAble<C> {

        */
/**
 * @see Expression#ifEqual(Object)
 *//*

        WhereAndAble<C> and(@Nullable IPredicate predicate);

        WhereAndAble<C> and(Function<C, IPredicate> function);

        WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface GroupByAble<C> extends OrderByAble<C> {

        HavingAble<C> groupBy(SortPart sortPart);

        HavingAble<C> groupBy(List<SortPart> sortPartList);

        HavingAble<C> groupBy(Function<C, List<SortPart>> function);

        HavingAble<C> ifGroupBy(Predicate<C> test, SortPart sortPart);

        HavingAble<C> ifGroupBy(Predicate<C> test, Function<C, List<SortPart>> function);

    }

    interface HavingAble<C> extends OrderByAble<C> {

        OrderByAble<C> having(IPredicate predicate);

        OrderByAble<C> having(Function<C, List<IPredicate>> function);

        OrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        OrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface OrderByAble<C> extends OrderByClause<C>, LimitAble<C> {

        @Override
        LimitAble<C> orderBy(SortPart sortPart);

        @Override
        LimitClause<C> orderBy(List<SortPart> sortPartList);

        @Override
        LimitAble<C> orderBy(Function<C, List<SortPart>> function);

        @Override
        LimitAble<C> ifOrderBy(Predicate<C> test, SortPart sortPart);

        @Override
        LimitAble<C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function);
    }


    interface LimitAble<C> extends LimitClause<C>, LockAble<C> {

        LockAble<C> limit(int rowCount);

        LockAble<C> limit(int offset, int rowCount);

        LockAble<C> limit(Function<C, Pair<Integer, Integer>> function);

        LockAble<C> ifLimit(Predicate<C> predicate, int rowCount);

        LockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        LockAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

    interface LockAble<C> extends SelectAble, UnionClause<C> {

        SelectAble lock(LockMode lockMode);

        SelectAble lock(Function<C, LockMode> function);

        SelectAble ifLock(Predicate<C> predicate, LockMode lockMode);

        SelectAble ifLock(Predicate<C> predicate, Function<C, LockMode> function);

    }


    interface UnionAble<C> extends UnionClause<C>, OrderByClause<C> {

    }

    interface UnionClause<C> extends SelectAble {

        UnionAble<C> brackets();

        <S extends Select> UnionAble<C> union(Function<C, S> function);

        <S extends Select> UnionAble<C> unionAll(Function<C, S> function);

        <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function);

    }

    interface OrderByClause<C> extends LimitClause<C> {

        LimitClause<C> orderBy(SortPart sortPart);

        LimitClause<C> orderBy(List<SortPart> sortPartList);

        LimitClause<C> orderBy(Function<C, List<SortPart>> function);

        LimitClause<C> ifOrderBy(Predicate<C> test, SortPart sortPart);

        LimitClause<C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function);
    }

    interface LimitClause<C> extends  {

        SelectAble limit(int rowCount);

        SelectAble limit(int offset, int rowCount);

        SelectAble limit(Function<C, Pair<Integer, Integer>> function);

        SelectAble ifLimit(Predicate<C> predicate, int rowCount);

        SelectAble ifLimit(Predicate<C> predicate, int offset, int rowCount);

        SelectAble ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }


    interface TableOptionalClause {

    }

    interface SubQueryOptionalClause {

    }
*/

}
