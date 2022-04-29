package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._LateralSubQuery;
import io.army.criteria.impl.inner._SubQuery;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.criteria.mysql.MySQL80Query;
import io.army.dialect.Dialect;
import io.army.session.Database;
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

    static <C, Q extends Query> _UnionOrderBySpec<C, Q> bracketQuery(final RowSet query) {
        query.prepared();
        final _UnionOrderBySpec<C, ?> spec;
        if (query instanceof Select) {
            spec = new BracketSelect<>((Select) query);
        } else if (query instanceof ScalarSubQuery) {
            if (query instanceof _LateralSubQuery) {
                spec = new LateralBracketScalarSubQuery<>((ScalarExpression) query);
            } else {
                spec = new BracketScalarSubQuery<>((ScalarExpression) query);
            }
        } else if (query instanceof SubQuery) {
            if (query instanceof _LateralSubQuery) {
                spec = new LateralBracketSubQuery<>((SubQuery) query);
            } else {
                spec = new BracketSubQuery<>((SubQuery) query);
            }
        } else {
            throw _Exceptions.unknownRowSetType(query);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }

    static <C, Q extends Query> _UnionOrderBySpec<C, Q> unionQuery(Q left, UnionType unionType, RowSet right) {
        left.prepared();
        final _UnionOrderBySpec<C, ?> spec;
        if (left instanceof Select) {
            if (!(right instanceof Select || right instanceof Values)) {
                throw errorRight(Select.class);
            }
            spec = new UnionSelect<>((Select) left, unionType, right);
        } else if (left instanceof ScalarSubQuery) {
            if (!(right instanceof ScalarSubQuery || right instanceof Values)) {
                throw errorRight(ScalarSubQuery.class);
            }
            if (left instanceof _LateralSubQuery) {
                spec = new LateralUnionScalarSubQuery<>((ScalarExpression) left, unionType, right);
            } else {
                spec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType, right);
            }
        } else if (left instanceof SubQuery) {
            if (!(right instanceof SubQuery || right instanceof Values)) {
                throw errorRight(SubQuery.class);
            }
            if (left instanceof _LateralSubQuery) {
                spec = new LateralUnionSubQuery<>((SubQuery) left, unionType, right);
            } else {
                spec = new UnionSubQuery<>((SubQuery) left, unionType, right);
            }
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }

    private static CriteriaException errorRight(Class<? extends Query> clazz) {
        String m = String.format("MySQL 8.0 %s api UNION clause support only %s and %s."
                , clazz.getSimpleName(), clazz.getName(), Values.class.getName());
        throw new CriteriaException(m);
    }

    final Q left;

    private MySQL80UnionQuery(final Q left) {
        super(left);
        this.left = left;
    }


    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL80;
    }

    @Override
    final void validateDialect(Dialect mode) {
        if (mode.database() != Database.MySQL || mode.version() < 80) {
            throw _Exceptions.stmtDontSupportDialect(mode);
        }
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
    final _WithSpec<C, Q> asUnionAndRowSet(UnionType unionType) {
        return MySQL80SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }


    private static final class BracketSelect<C> extends MySQL80UnionQuery<C, Select> implements Select, BracketRowSet {

        private BracketSelect(Select left) {
            super(left);
        }


    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends MySQL80UnionQuery<C, Q>
            implements _SubQuery, BracketRowSet {

        private BracketSubQuery(Q left) {
            super(left);
        }


    }//BracketSubQuery

    private static final class LateralBracketSubQuery<C> extends BracketSubQuery<C, SubQuery>
            implements _LateralSubQuery {

        private LateralBracketSubQuery(SubQuery left) {
            super(left);
        }

    }//LateralBracketSubQuery


    private static class BracketScalarSubQuery<C> extends BracketSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private BracketScalarSubQuery(ScalarExpression left) {
            super(left);
        }

    }//BracketScalarSubQuery

    private static final class LateralBracketScalarSubQuery<C> extends BracketScalarSubQuery<C>
            implements _LateralSubQuery {

        private LateralBracketScalarSubQuery(ScalarExpression left) {
            super(left);
        }

    }//LateralBracketScalarSubQuery

    private static abstract class UnionQuery<C, Q extends Query> extends MySQL80UnionQuery<C, Q>
            implements RowSetWithUnion {

        private final UnionType unionType;

        private final RowSet right;

        UnionQuery(Q left, UnionType unionType, RowSet right) {
            super(left);
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

        private UnionSelect(Select left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }

    }//UnionSelect

    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q>
            implements _SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSubQuery

    private static final class LateralUnionSubQuery<C> extends UnionSubQuery<C, SubQuery>
            implements _LateralSubQuery {

        private LateralUnionSubQuery(SubQuery left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }

    }//LateralUnionSubQuery


    private static class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }

    }//UnionScalarSubQuery

    private static final class LateralUnionScalarSubQuery<C> extends UnionScalarSubQuery<C>
            implements _LateralSubQuery {

        private LateralUnionScalarSubQuery(ScalarExpression left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }

    }//LateralUnionScalarSubQuery


}
