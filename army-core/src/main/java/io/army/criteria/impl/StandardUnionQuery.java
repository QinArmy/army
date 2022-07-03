package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.Dialect;
import io.army.util._Exceptions;

/**
 * @see StandardSimpleQuery
 */
@SuppressWarnings("unchecked")
abstract class StandardUnionQuery<C, Q extends Query> extends UnionRowSet<
        C,
        Q,
        StandardQuery._UnionOrderBySpec<C, Q>, //UR
        StandardQuery._UnionLimitSpec<C, Q>,//OR
        StandardQuery._UnionSpec<C, Q>,//LR
        StandardQuery._StandardSelectClause<C, Q>>// SP
        implements StandardQuery._UnionOrderBySpec<C, Q>, StandardQuery {

    static <C, Q extends Query> _UnionOrderBySpec<C, Q> bracketQuery(final RowSet query) {
        if (!(query instanceof Query)) {
            throw _Exceptions.unknownRowSetType(query);
        }
        final _UnionOrderBySpec<C, ?> unionSpec;
        final CriteriaContext criteriaContext;
        criteriaContext = CriteriaContexts.bracketContext((Query) query);
        if (query instanceof Select) {
            unionSpec = new BracketSelect<>((Select) query, criteriaContext);
        } else if (query instanceof ScalarSubQuery) {
            unionSpec = new BracketScalarSubQuery<>((ScalarExpression) query, criteriaContext);
        } else if (query instanceof SubQuery) {
            unionSpec = new BracketSubQuery<>((SubQuery) query, criteriaContext);
        } else {
            throw _Exceptions.unknownRowSetType(query);
        }
        return (_UnionOrderBySpec<C, Q>) unionSpec;
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
        // never validate right,possibly union and select
        CriteriaUtils.assertSelectItemSizeMatch(left, right);
        final CriteriaContext criteriaContext;
        criteriaContext = CriteriaContexts.unionContext(left, right);
        final _UnionOrderBySpec<C, ?> unionSpec;
        if (left instanceof Select) {
            if (!(right instanceof Select && right instanceof StandardQuery)) {
                String m = String.format("standard query api support only standard %s.", Select.class.getName());
                throw new CriteriaException(m);
            }
            unionSpec = new UnionSelect<>((Select) left, unionType, (Select) right, criteriaContext);
        } else if (left instanceof ScalarSubQuery) {
            if (!(right instanceof ScalarExpression && right instanceof StandardQuery)) {
                String m;
                m = String.format("standard scalar sub query api support only standard %s.", ScalarExpression.class.getName());
                throw new CriteriaException(m);
            }
            unionSpec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType
                    , (ScalarExpression) right, criteriaContext);
        } else if (left instanceof SubQuery) {
            if (!(right instanceof SubQuery && right instanceof StandardQuery)) {
                String m = String.format("standard sub query api support only standard %s.", SubQuery.class.getName());
                throw new CriteriaException(m);
            }
            unionSpec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right, criteriaContext);
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (_UnionOrderBySpec<C, Q>) unionSpec;
    }


    static <C, Q extends Query> _UnionOrderBySpec<C, Q> noActionQuery(final RowSet rowSet) {

        final CriteriaContext criteriaContext;
        criteriaContext = CriteriaContexts.noActionContext(rowSet);

        final _UnionOrderBySpec<C, ?> spec;
        if (rowSet instanceof Select) {
            spec = new NoActionSelect<>((Select) rowSet, criteriaContext);
        } else if (rowSet instanceof ScalarSubQuery) {
            spec = new NoActionScalarSubQuery<>((ScalarExpression) rowSet, criteriaContext);
        } else if (rowSet instanceof SubQuery) {
            spec = new NoActionSubQuery<>((SubQuery) rowSet, criteriaContext);
        } else {
            throw _Exceptions.unknownRowSetType(rowSet);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }


    private StandardUnionQuery(Q left, CriteriaContext criteriaContext) {
        super(left, criteriaContext);

    }


    @Override
    public final String toString() {
        final String s;
        if (this instanceof Select && this.isPrepared()) {
            s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    final _UnionOrderBySpec<C, Q> createBracketQuery(RowSet rowSet) {
        return bracketQuery(rowSet);
    }

    @Override
    final _UnionOrderBySpec<C, Q> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return unionQuery((Q) left, unionType, right);
    }

    @Override
    final _UnionOrderBySpec<C, Q> getNoActionUnionRowSet(RowSet rowSet) {
        assert rowSet != this;
        return this;
    }

    @Override
    final _StandardSelectClause<C, Q> asUnionAndRowSet(UnionType unionType) {
        return StandardSimpleQuery.unionAndQuery(this.asQuery(), unionType);
    }



    private static final class BracketSelect<C> extends StandardUnionQuery<C, Select>
            implements Select, BracketRowSet {

        private BracketSelect(Select query, CriteriaContext criteriaContext) {
            super(query, criteriaContext);
        }

    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends StandardUnionQuery<C, Q>
            implements SubQuery, BracketRowSet {

        private BracketSubQuery(Q query, CriteriaContext criteriaContext) {
            super(query, criteriaContext);
        }

    }


    private static final class BracketScalarSubQuery<C> extends BracketSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private BracketScalarSubQuery(ScalarExpression query, CriteriaContext criteriaContext) {
            super(query, criteriaContext);
        }

    }//BracketScalarSubQuery

    private static final class NoActionSelect<C> extends StandardUnionQuery<C, Select>
            implements Select, NoActionRowSet {

        private NoActionSelect(Select left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//NoActionSelect

    private static class NoActionSubQuery<C, Q extends SubQuery> extends StandardUnionQuery<C, Q>
            implements SubQuery, NoActionRowSet {

        private NoActionSubQuery(Q left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//NoActionSubQuery


    private static class NoActionScalarSubQuery<C> extends NoActionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {
        private NoActionScalarSubQuery(ScalarExpression left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//NoActionScalarSubQuery

    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link UnionSelect}</li>
     *         <li>{@link UnionSubQuery}</li>
     *         <li>{@link UnionScalarSubQuery}</li>
     *     </ul>
     * </p>
     */
    private static abstract class UnionQuery<C, Q extends Query> extends StandardUnionQuery<C, Q>
            implements RowSetWithUnion {

        final UnionType unionType;

        final Q right;

        UnionQuery(Q left, UnionType unionType, Q right, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
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

        private UnionSelect(Select left, UnionType unionType, Select right, CriteriaContext criteriaContext) {
            super(left, unionType, right, criteriaContext);
        }


    }//UnionSelect


    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q> implements SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, Q right, CriteriaContext criteriaContext) {
            super(left, unionType, right, criteriaContext);
        }


    }// UnionSubQuery


    private static final class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType
                , ScalarExpression right, CriteriaContext criteriaContext) {
            super(left, unionType, right, criteriaContext);
        }


    }//UnionScalarSubQuery


}
