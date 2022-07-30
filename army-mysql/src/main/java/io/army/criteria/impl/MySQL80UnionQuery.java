package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._LateralSubQuery;
import io.army.criteria.impl.inner._SubQuery;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.criteria.mysql.MySQL80Query;
import io.army.criteria.mysql.MySQLDqlValues;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.mysql.MySQLDialect;
import io.army.util._Exceptions;

/**
 * <p>
 * This class is base class all the implementation of MySQL 8.0 UNION clause syntax.
 * </p>
 *
 * @param <C> java type of criteria object for dynamic statement
 * @param <Q> {@link Select} or {@link SubQuery} or {@link ScalarExpression}
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/union.html">UNION Clause</a>
 * @see MySQL80SimpleQuery
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQL80UnionQuery<C, Q extends Query> extends UnionRowSet<
        C,
        Q,
        MySQL80Query._UnionOrderBySpec<C, Q>,
        MySQL80Query._UnionLimitSpec<C, Q>,
        MySQL80Query._UnionSpec<C, Q>,
        MySQL80Query._WithSpec<C, Q>>
        implements MySQL80Query, MySQL80Query._UnionOrderBySpec<C, Q>, _UnionRowSet {

    static <C, Q extends Query> _UnionOrderBySpec<C, Q> bracketQuery(final RowSet rowSet) {

        final CriteriaContext context;
        context = CriteriaContexts.bracketContext((Query) rowSet);
        final _UnionOrderBySpec<C, ?> spec;
        if (rowSet instanceof Select) {
            spec = new BracketSelect<>((Select) rowSet, context);
        } else if (rowSet instanceof ScalarSubQuery) {
            final ScalarExpression left = (ScalarExpression) rowSet;
            if (rowSet instanceof _LateralSubQuery) {
                spec = new LateralBracketScalarSubQuery<>(left, context);
            } else {
                spec = new BracketScalarSubQuery<>(left, context);
            }
        } else if (rowSet instanceof SubQuery) {
            final SubQuery left = (SubQuery) rowSet;
            if (rowSet instanceof _LateralSubQuery) {
                spec = new LateralBracketSubQuery<>(left, context);
            } else {
                spec = new BracketSubQuery<>(left, context);
            }
        } else {
            throw _Exceptions.unknownRowSetType(rowSet);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }

    static <C, Q extends Query> _UnionOrderBySpec<C, Q> unionQuery(Q left, UnionType unionType, RowSet right) {
        left.prepared();
        final _UnionOrderBySpec<C, ?> spec;
        final CriteriaContext context;
        context = CriteriaContexts.unionContext(left, right);

        if (left instanceof Select) {
            if (!((right instanceof Select && (right instanceof MySQLQuery || right instanceof StandardQuery))
                    || (right instanceof Values && right instanceof MySQLDqlValues))) {
                throw errorRight(Select.class);
            }
            spec = new UnionSelect<>((Select) left, unionType, right, context);
        } else if (left instanceof ScalarSubQuery) {
            if (!((right instanceof ScalarSubQuery && (right instanceof MySQLQuery || right instanceof StandardQuery))
                    || (right instanceof SubValues && right instanceof MySQLDqlValues))) {
                throw errorRight(ScalarSubQuery.class);
            }
            if (left instanceof _LateralSubQuery) {
                spec = new LateralUnionScalarSubQuery<>((ScalarExpression) left, unionType, right, context);
            } else {
                spec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType, right, context);
            }
        } else if (left instanceof SubQuery) {
            if (!((right instanceof SubQuery && (right instanceof MySQLQuery || right instanceof StandardQuery))
                    || (right instanceof SubValues && right instanceof MySQLDqlValues))) {
                throw errorRight(SubQuery.class);
            }
            if (left instanceof _LateralSubQuery) {
                spec = new LateralUnionSubQuery<>((SubQuery) left, unionType, right, context);
            } else {
                spec = new UnionSubQuery<>((SubQuery) left, unionType, right, context);
            }
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }

    static <C, Q extends Query> _UnionOrderBySpec<C, Q> noActionQuery(final RowSet rowSet) {
        final CriteriaContext context;
        context = CriteriaContexts.noActionContext(rowSet);
        final _UnionOrderBySpec<C, ?> spec;
        if (rowSet instanceof Select) {
            spec = new NoActionSelect<>((Select) rowSet, context);
        } else if (rowSet instanceof ScalarSubQuery) {
            final ScalarExpression left = (ScalarExpression) rowSet;
            if (rowSet instanceof _LateralSubQuery) {
                spec = new LateralNoActionScalarSubQuery<>(left, context);
            } else {
                spec = new NoActionScalarSubQuery<>(left, context);
            }
        } else if (rowSet instanceof SubQuery) {
            final SubQuery left = (SubQuery) rowSet;
            if (rowSet instanceof _LateralSubQuery) {
                spec = new LateralNoActionSubQuery<>(left, context);
            } else {
                spec = new NoActionSubQuery<>(left, context);
            }
        } else {
            throw _Exceptions.unknownRowSetType(rowSet);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }

    private static CriteriaException errorRight(Class<? extends Query> clazz) {
        String m = String.format("MySQL 8.0 %s api UNION clause support only %s and %s."
                , clazz.getSimpleName(), clazz.getName(), Values.class.getName());
        throw new CriteriaException(m);
    }


    private MySQL80UnionQuery(final Q left, final CriteriaContext context) {
        super(left, context);
    }

    @Override
    public final String toString() {
        final String s;
        if (this instanceof Select && this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    @Override
    final _UnionOrderBySpec<C, Q> createBracketQuery(RowSet rowSet) {
        return MySQL80UnionQuery.bracketQuery(rowSet);
    }

    @Override
    final _UnionOrderBySpec<C, Q> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return MySQL80UnionQuery.unionQuery((Q) left, unionType, right);
    }

    @Override
    final _UnionOrderBySpec<C, Q> getNoActionUnionRowSet(RowSet rowSet) {
        assert rowSet != this;
        return this;
    }

    @Override
    final _WithSpec<C, Q> asUnionAndRowSet(UnionType unionType) {
        return MySQL80SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }


    private static final class BracketSelect<C> extends MySQL80UnionQuery<C, Select> implements Select, BracketRowSet {

        private BracketSelect(Select left, CriteriaContext context) {
            super(left, context);
        }


    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends MySQL80UnionQuery<C, Q>
            implements SubQuery, BracketRowSet {

        private BracketSubQuery(Q left, CriteriaContext context) {
            super(left, context);
        }


    }//BracketSubQuery

    private static final class LateralBracketSubQuery<C> extends BracketSubQuery<C, SubQuery>
            implements _LateralSubQuery {

        private LateralBracketSubQuery(SubQuery left, CriteriaContext context) {
            super(left, context);
        }

    }//LateralBracketSubQuery


    private static class BracketScalarSubQuery<C> extends BracketSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private BracketScalarSubQuery(ScalarExpression left, CriteriaContext context) {
            super(left, context);
        }

    }//BracketScalarSubQuery

    private static final class LateralBracketScalarSubQuery<C> extends BracketScalarSubQuery<C>
            implements _LateralSubQuery {

        private LateralBracketScalarSubQuery(ScalarExpression left, CriteriaContext context) {
            super(left, context);
        }

    }//LateralBracketScalarSubQuery


    private static final class NoActionSelect<C> extends MySQL80UnionQuery<C, Select>
            implements Select, NoActionRowSet {

        private NoActionSelect(Select left, CriteriaContext context) {
            super(left, context);
        }


    }//NoActionSelect

    private static class NoActionSubQuery<C, Q extends SubQuery> extends MySQL80UnionQuery<C, Q>
            implements SubQuery, NoActionRowSet {

        private NoActionSubQuery(Q left, CriteriaContext context) {
            super(left, context);
        }


    }//NoActionSubQuery

    private static final class LateralNoActionSubQuery<C> extends NoActionSubQuery<C, SubQuery>
            implements _LateralSubQuery {

        private LateralNoActionSubQuery(SubQuery left, CriteriaContext context) {
            super(left, context);
        }

    }//LateralNoActionSubQuery


    private static class NoActionScalarSubQuery<C> extends NoActionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private NoActionScalarSubQuery(ScalarExpression left, CriteriaContext context) {
            super(left, context);
        }

    }//NoActionScalarSubQuery

    private static final class LateralNoActionScalarSubQuery<C> extends NoActionScalarSubQuery<C>
            implements _LateralSubQuery {

        private LateralNoActionScalarSubQuery(ScalarExpression left, CriteriaContext context) {
            super(left, context);
        }

    }//LateralNoActionScalarSubQuery

    private static abstract class UnionQuery<C, Q extends Query> extends MySQL80UnionQuery<C, Q>
            implements RowSetWithUnion {

        private final UnionType unionType;

        private final RowSet right;

        UnionQuery(Q left, UnionType unionType, RowSet right, CriteriaContext context) {
            super(left, context);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public final UnionType unionType() {
            return this.unionType;
        }

        @Override
        public final RowSet rightRowSet() {
            return this.right;
        }

    }// UnionQuery

    private static final class UnionSelect<C> extends UnionQuery<C, Select> implements Select {

        private UnionSelect(Select left, UnionType unionType, RowSet right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

    }//UnionSelect

    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q>
            implements _SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, RowSet right, CriteriaContext context) {
            super(left, unionType, right, context);
        }


    }//UnionSubQuery

    private static final class LateralUnionSubQuery<C> extends UnionSubQuery<C, SubQuery>
            implements _LateralSubQuery {

        private LateralUnionSubQuery(SubQuery left, UnionType unionType, RowSet right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

    }//LateralUnionSubQuery


    private static class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType, RowSet right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

    }//UnionScalarSubQuery

    private static final class LateralUnionScalarSubQuery<C> extends UnionScalarSubQuery<C>
            implements _LateralSubQuery {

        private LateralUnionScalarSubQuery(ScalarExpression left, UnionType unionType
                , RowSet right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

    }//LateralUnionScalarSubQuery


}
