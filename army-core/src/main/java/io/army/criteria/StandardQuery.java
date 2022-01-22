package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public interface StandardQuery extends Query, StandardStatement {

    /**
     * <p>
     * This interface representing select clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     */
    interface StandardSelectSpec<C, Q extends Query> extends SelectClause<C, StandardFromSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing from clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
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
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardOnSpec<C, Q extends Query> extends OnClause<C, StandardJoinSpec<C, Q>> {


    }

    /**
     * <p>
     * This interface representing join clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardJoinSpec<C, Q extends Query>
            extends JoinClause<C, StandardOnSpec<C, Q>, StandardOnSpec<C, Q>>
            , StandardWhereSpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing where clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
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
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardWhereAndSpec<C, Q extends Query>
            extends WhereAndClause<C, StandardWhereAndSpec<C, Q>>, StandardGroupBySpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing group by clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
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
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
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
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardOrderBySpec<C, Q extends Query> extends StandardLimitSpec<C, Q>
            , Query.OrderByClause<C, StandardLimitSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing limit clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardLimitSpec<C, Q extends Query> extends StandardLockSpec<C, Q>
            , Query.LimitClause<C, StandardLockSpec<C, Q>> {

    }

    interface StandardUnionSpec<C, Q extends Query> extends StandardOrderByClause<C, Q> {

    }

    /**
     * <p>
     * This interface representing order by clause(after union) of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
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
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardLimitClause<C, Q extends Query>
            extends Query.LimitClause<C, StandardUnionClause<C, Q>>, StandardUnionClause<C, Q> {

    }

    interface StandardUnionClause<C, Q extends Query> extends QuerySpec<Q>
            , Query.UnionClause<C, StandardUnionSpec<C, Q>, StandardSelectSpec<C, Q>, Q> {

    }

    /**
     * <p>
     * This interface representing lock clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#rowSubQuery(Object)
     * @see SQLs#columnSubQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardLockSpec<C, Q extends Query> extends StandardUnionClause<C, Q> {

        StandardUnionClause<C, Q> lock(LockMode lockMode);

        StandardUnionClause<C, Q> lock(Function<C, LockMode> function);

        StandardUnionClause<C, Q> ifLock(@Nullable LockMode lockMode);

        StandardUnionClause<C, Q> ifLock(Supplier<LockMode> supplier);

        StandardUnionClause<C, Q> ifLock(Function<C, LockMode> function);


    }


}
