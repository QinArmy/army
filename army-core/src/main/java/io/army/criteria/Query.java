package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing query,is base interface of below:
 *     <ul>
 *         <li>{@link Select}</li>
 *         <li>{@link SubQuery}</li>
 *         <li>{@link RowSubQuery}</li>
 *         <li>{@link ColumnSubQuery}</li>
 *         <li>{@link ScalarSubQuery}</li>
 *     </ul>
 * </p>
 *
 * @see Select
 * @see SubQuery
 * @see RowSubQuery
 * @see ColumnSubQuery
 * @see ScalarSubQuery
 */
public interface Query extends Statement {

    boolean requiredBrackets();


    interface QuerySpec<Q extends Query> {

        Q asQuery();
    }


    /*################################## blow select clause  interfaces ##################################*/


    /**
     * <p>
     * This interface representing select clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     */
    interface StandardSelectClauseSpec<C, Q extends Query> extends Query.SelectClause<C, Query.StandardFromSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing from clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardFromSpec<C, Q extends Query>
            extends Statement.FromClause<C, Query.StandardJoinSpec<C, Q>, Query.StandardJoinSpec<C, Q>> {


    }

    /**
     * <p>
     * This interface representing on clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardOnSpec<C, Q extends Query> extends Statement.OnClause<C, Query.StandardJoinSpec<C, Q>> {


    }


    /**
     * <p>
     * This interface representing join clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardJoinSpec<C, Q extends Query>
            extends Statement.JoinClause<C, Query.StandardOnSpec<C, Q>, Query.StandardOnSpec<C, Q>>
            , Query.StandardWhereSpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing where clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardWhereSpec<C, Q extends Query>
            extends Statement.WhereClause<C, Query.StandardGroupBySpec<C, Q>, Query.StandardWhereAndSpec<C, Q>>
            , Query.StandardGroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing where and clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardWhereAndSpec<C, Q extends Query>
            extends Statement.WhereAndClause<C, Query.StandardWhereAndSpec<C, Q>>
            , Query.StandardGroupBySpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing group by clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardGroupBySpec<C, Q extends Query> extends Query.GroupClause<C, Query.StandardHavingSpec<C, Q>>
            , Query.StandardOrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing having clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardHavingSpec<C, Q extends Query> extends Query.HavingClause<C, Query.StandardOrderBySpec<C, Q>>
            , Query.StandardOrderBySpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing order by clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardOrderBySpec<C, Q extends Query> extends Query.OrderByClause<C, Query.StandardLimitSpec<C, Q>>
            , Query.StandardLimitSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing limit clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardLimitSpec<C, Q extends Query> extends Query.StandardLimitClause<C, Q>
            , Query.StandardLockSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing union (after union) clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardUnionSpec<C, Q extends Query> extends Query.StandardUnionClause<C, Q>
            , Query.StandardOrderByClause<C, Q> {

    }

    interface StandardUnionClause<C, Q extends Query> extends QuerySpec<Q>
            , UnionClause<C, Query.StandardUnionSpec<C, Q>, StandardSelectClauseSpec<C, Q>, Q> {

    }

    /**
     * <p>
     * This interface representing order by clause(after union) of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardOrderByClause<C, Q extends Query> extends Query.StandardLimitClause<C, Q>
            , Query.OrderByClause<C, Query.StandardLimitClause<C, Q>> {

    }

    /**
     * <p>
     * This interface representing limit clause(after union) of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardLimitClause<C, Q extends Query> extends Query.LimitClause<C, QuerySpec<Q>>, QuerySpec<Q> {


    }

    /**
     * <p>
     * This interface representing lock clause of standard query (SELECT ro Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardLockSpec<C, Q extends Query> extends Query.StandardUnionClause<C, Q>, QuerySpec<Q> {

        StandardUnionClause<C, Q> lock(LockMode lockMode);

        StandardUnionClause<C, Q> lock(Function<C, LockMode> function);

        StandardUnionClause<C, Q> ifLock(@Nullable LockMode lockMode);

        StandardUnionClause<C, Q> ifLock(Supplier<LockMode> supplier);

        StandardUnionClause<C, Q> ifLock(Function<C, LockMode> function);


    }

    interface SelectClause<C, FC> {

        <S extends SelectPart> FC select(Function<C, Hint> hints, List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectPart> FC select(List<Hint> hints, List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectPart> FC select(List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectPart> FC select(List<SQLModifier> modifiers, Supplier<List<S>> supplier);

        <S extends SelectPart> FC select(Function<C, List<S>> function);

        <S extends SelectPart> FC select(Supplier<List<S>> supplier);

        FC select(Distinct distinct, SelectPart selectPart);

        FC select(SelectPart selectPart);

        FC select(SelectPart selectPart1, SelectPart selectPart2);

        FC select(SelectPart selectPart1, SelectPart selectPart2, SelectPart selectPart3);

        <S extends SelectPart> FC select(List<SQLModifier> modifiers, List<S> selectPartList);

        <S extends SelectPart> FC select(List<S> selectPartList);

        <S extends SelectPart> FC select(Distinct distinct, List<S> selectPartList);

    }


    interface GroupClause<C, GR> {

        GR groupBy(SortPart sortPart);

        GR groupBy(SortPart sortPart1, SortPart sortPart2);

        GR groupBy(List<SortPart> sortPartList);

        GR groupBy(Function<C, List<SortPart>> function);

        GR groupBy(Supplier<List<SortPart>> supplier);

        GR ifGroupBy(@Nullable SortPart sortPart);

        GR ifGroupBy(Supplier<List<SortPart>> supplier);

        GR ifGroupBy(Function<C, List<SortPart>> function);
    }


    interface HavingClause<C, HR> {

        HR having(IPredicate predicate);

        HR having(IPredicate predicate1, IPredicate predicate2);

        HR having(List<IPredicate> predicateList);

        HR having(Supplier<List<IPredicate>> supplier);

        HR having(Function<C, List<IPredicate>> function);

        HR ifHaving(@Nullable IPredicate predicate);

        HR ifHaving(Supplier<List<IPredicate>> supplier);

        HR ifHaving(Function<C, List<IPredicate>> function);

    }

    interface LockClause<C, LC> {

        LC lock(SQLModifier lockMode);

        LC lock(Function<C, SQLModifier> function);

        LC ifLock(@Nullable SQLModifier lockMode);

        LC ifLock(Supplier<SQLModifier> supplier);

        LC ifLock(Function<C, SQLModifier> function);
    }


    interface UnionClause<C, UR, SP, Q extends Query> {

        UR bracketsQuery();

        UR union(Function<C, Q> function);

        UR union(Supplier<Q> supplier);

        UR union();

        UR unionAll();

        UR unionDistinct();

        UR unionAll(Function<C, Q> function);

        UR unionDistinct(Function<C, Q> function);

        UR unionAll(Supplier<Q> supplier);

        UR unionDistinct(Supplier<Q> supplier);


    }

    interface OrderByClause<C, OR> {

        OR orderBy(SortPart sortPart);

        OR orderBy(SortPart sortPart1, SortPart sortPart2);

        OR orderBy(List<SortPart> sortPartList);

        OR orderBy(Function<C, List<SortPart>> function);

        OR orderBy(Supplier<List<SortPart>> supplier);

        OR ifOrderBy(@Nullable SortPart sortPart);

        OR ifOrderBy(Supplier<List<SortPart>> supplier);

        OR ifOrderBy(Function<C, List<SortPart>> function);

    }

    interface LimitClause<C, LR> {

        LR limit(long rowCount);

        LR limit(long offset, long rowCount);


        LR limit(Function<C, LimitOption> function);

        LR limit(Supplier<LimitOption> supplier);

        LR ifLimit(Function<C, LimitOption> function);

        LR ifLimit(Supplier<LimitOption> supplier);

    }


}
