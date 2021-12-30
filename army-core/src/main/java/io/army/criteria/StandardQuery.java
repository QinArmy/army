package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface StandardQuery extends Query, StandardStatement {

    interface SelectSpec<C> extends StandardSelectClauseSpec<C, Select> {

    }

    /**
     * <p>
     * This interface representing select clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     */
    interface StandardSelectClauseSpec<C, Q extends Query> extends SelectClause<C, StandardFromSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing from clause of standard query (SELECT or Sub Query).
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
            extends FromClause<C, StandardJoinSpec<C, Q>, StandardJoinSpec<C, Q>>
            , StandardUnionClause<C, Q> {


    }

    /**
     * <p>
     * This interface representing on clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardOnSpec<C, Q extends Query> extends OnClause<C, StandardJoinSpec<C, Q>> {


    }

    /**
     * <p>
     * This interface representing join clause of standard query (SELECT or Sub Query).
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
            extends JoinClause<C, StandardQuery.StandardOnSpec<C, Q>, StandardQuery.StandardOnSpec<C, Q>>
            , StandardWhereSpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing where clause of standard query (SELECT or Sub Query).
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
            extends WhereClause<C, StandardGroupBySpec<C, Q>, StandardWhereAndSpec<C, Q>>
            , StandardGroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing where and clause of standard query (SELECT or Sub Query).
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
            extends WhereAndClause<C, StandardQuery.StandardWhereAndSpec<C, Q>>
            , StandardGroupBySpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing group by clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardGroupBySpec<C, Q extends Query> extends GroupClause<C, StandardHavingSpec<C, Q>>
            , StandardOrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing having clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardHavingSpec<C, Q extends Query> extends HavingClause<C, StandardOrderBySpec<C, Q>>
            , StandardOrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing order by clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardOrderBySpec<C, Q extends Query> extends StandardOrderByClause<C, Q>, StandardLimitSpec<C, Q> {

        @Override
        StandardLimitSpec<C, Q> orderBy(SortPart sortPart);

        @Override
        StandardLimitSpec<C, Q> orderBy(SortPart sortPart1, SortPart sortPart2);

        @Override
        StandardLimitSpec<C, Q> orderBy(List<SortPart> sortPartList);

        @Override
        StandardLimitSpec<C, Q> orderBy(Function<C, List<SortPart>> function);

        @Override
        StandardLimitSpec<C, Q> orderBy(Supplier<List<SortPart>> supplier);

        @Override
        StandardLimitSpec<C, Q> ifOrderBy(@Nullable SortPart sortPart);

        @Override
        StandardLimitSpec<C, Q> ifOrderBy(Supplier<List<SortPart>> supplier);

        @Override
        StandardLimitSpec<C, Q> ifOrderBy(Function<C, List<SortPart>> function);
    }

    /**
     * <p>
     * This interface representing limit clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardLimitSpec<C, Q extends Query> extends StandardLockSpec<C, Q>
            , StandardQuery.StandardLimitClause<C, Q> {
        @Override
        StandardLockSpec<C, Q> limit(long rowCount);

        @Override
        StandardLockSpec<C, Q> limit(long offset, long rowCount);

        @Override
        StandardLockSpec<C, Q> limit(Function<C, LimitOption> function);

        @Override
        StandardLockSpec<C, Q> limit(Supplier<LimitOption> supplier);

        @Override
        StandardLockSpec<C, Q> ifLimit(Function<C, LimitOption> function);

        @Override
        StandardLockSpec<C, Q> ifLimit(Supplier<LimitOption> supplier);
    }

    /**
     * <p>
     * This interface representing order by clause(after union) of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardOrderByClause<C, Q extends Query> extends StandardLimitClause<C, Q>
            , Query.OrderByClause<C, StandardLimitClause<C, Q>> {

    }

    /**
     * <p>
     * This interface representing limit clause(after union) of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardLimitClause<C, Q extends Query>
            extends Query.LimitClause<C, StandardUnionClause<C, Q>>, StandardUnionClause<C, Q> {

    }

    interface StandardUnionClause<C, Q extends Query> extends QuerySpec<Q>
            , Query.UnionClause<C, StandardOrderByClause<C, Q>, StandardSelectClauseSpec<C, Q>, Q> {

    }

    /**
     * <p>
     * This interface representing lock clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#standardSelect(Object)
     * @see SQLs#standardSubQuery(Object)
     * @see SQLs#standardRowSubQuery(Object)
     * @see SQLs#standardColumnSubQuery(Object)
     * @see SQLs#standardScalarSubQuery(Object)
     */
    interface StandardLockSpec<C, Q extends Query> extends StandardUnionClause<C, Q> {

        StandardUnionClause<C, Q> lock(LockMode lockMode);

        StandardUnionClause<C, Q> lock(Function<C, LockMode> function);

        StandardUnionClause<C, Q> ifLock(@Nullable LockMode lockMode);

        StandardUnionClause<C, Q> ifLock(Supplier<LockMode> supplier);

        StandardUnionClause<C, Q> ifLock(Function<C, LockMode> function);


    }


}
