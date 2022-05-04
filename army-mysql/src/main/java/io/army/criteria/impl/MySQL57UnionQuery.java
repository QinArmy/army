package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.criteria.mysql.MySQL57Query;
import io.army.dialect.Dialect;
import io.army.session.Database;
import io.army.util._Exceptions;

/**
 * <p>
 * This class is implementation of MySQL 5.7 union statement.
 * </p>
 *
 * @param <C> criteria object java type
 * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQL57UnionQuery<C, Q extends Query> extends UnionRowSet<
        C,
        Q,
        MySQL57Query._UnionOrderBySpec<C, Q>,
        MySQL57Query._UnionLimitSpec<C, Q>,
        MySQL57Query._UnionSpec<C, Q>,
        MySQL57Query._Select57Clause<C, Q>>
        implements MySQL57Query, MySQL57Query._UnionOrderBySpec<C, Q>, _UnionRowSet {

    static <C, Q extends Query> _UnionOrderBySpec<C, Q> bracketQuery(final RowSet query) {
        query.prepared();
        final _UnionOrderBySpec<C, ?> spec;
        if (query instanceof Select) {
            spec = new BracketSelect<>((Select) query);
        } else if (query instanceof ScalarSubQuery) {
            spec = new BracketScalarSubQuery<>((ScalarExpression) query);
        } else if (query instanceof SubQuery) {
            spec = new BracketSubQuery<>((SubQuery) query);
        } else {
            throw _Exceptions.unknownRowSetType(query);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }


    static <C, Q extends Query> _UnionOrderBySpec<C, Q> unionQuery(Q left, UnionType unionType, RowSet right) {
        switch (unionType) {
            case UNION:
            case UNION_ALL:
            case UNION_DISTINCT:
                break;
            default:
                throw _Exceptions.castCriteriaApi();
        }
        left.prepared();
        final _UnionOrderBySpec<C, ?> spec;
        if (left instanceof Select) {
            if (!(right instanceof Select)) {
                throw errorRight(Select.class);
            }
            spec = new UnionSelect<>((Select) left, unionType, (Select) right);
        } else if (left instanceof ScalarSubQuery) {
            if (!(right instanceof ScalarExpression)) {
                throw errorRight(ScalarSubQuery.class);
            }
            spec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType, (ScalarExpression) right);
        } else if (left instanceof SubQuery) {
            if (!(right instanceof SubQuery)) {
                throw errorRight(SubQuery.class);
            }
            spec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right);
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }

    private static CriteriaException errorRight(Class<? extends Query> clazz) {
        String m = String.format("MySQL 5.7 %s api UNION clause support only %s."
                , clazz.getSimpleName(), clazz.getName());
        throw new CriteriaException(m);
    }


    MySQL57UnionQuery(Q left) {
        super(left);
    }


    @Override
    final _Select57Clause<C, Q> asUnionAndRowSet(UnionType unionType) {
        return MySQL57SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final _UnionOrderBySpec<C, Q> createBracketQuery(RowSet rowSet) {
        return MySQL57UnionQuery.bracketQuery(rowSet);
    }

    @Override
    final _UnionOrderBySpec<C, Q> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return MySQL57UnionQuery.unionQuery((Q) left, unionType, right);
    }

    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    final void validateDialect(Dialect mode) {
        if (mode.database() != Database.MySQL) {
            throw new IllegalArgumentException(String.format("Don't support dialect[%s]", mode));
        }
    }


    private static final class BracketSelect<C> extends MySQL57UnionQuery<C, Select>
            implements Select, BracketRowSet {

        private BracketSelect(Select select) {
            super(select);
        }

    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends MySQL57UnionQuery<C, Q>
            implements SubQuery, BracketRowSet {

        private BracketSubQuery(Q left) {
            super(left);
        }


    }//BracketSubQuery


    private static final class BracketScalarSubQuery<C> extends BracketSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private BracketScalarSubQuery(ScalarExpression left) {
            super(left);
        }


    }//BracketScalarSubQuery


    private static class UnionQuery<C, Q extends Query> extends MySQL57UnionQuery<C, Q>
            implements RowSetWithUnion {

        private final UnionType unionType;

        private final Q right;

        private UnionQuery(Q left, UnionType unionType, Q right) {
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

        private UnionSelect(Select left, UnionType unionType, Select right) {
            super(left, unionType, right);
        }

    }//UnionSelect

    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q>
            implements SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, Q right) {
            super(left, unionType, right);
        }

    }//UnionSubQuery


    private static final class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType, ScalarExpression right) {
            super(left, unionType, right);
        }

    }//UnionScalarSubQuery


}
