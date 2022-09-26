package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.mysql.MySQLDialect;
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
        final CriteriaContext newContext;
        newContext = CriteriaContexts.bracketContext(query);
        if (query instanceof Select) {
            unionSpec = new BracketSelect<>((Select) query, newContext);
        } else if (query instanceof ScalarSubQuery) {
            unionSpec = new BracketScalarSubQuery<>((ScalarExpression) query, newContext);
        } else if (!(query instanceof SubQuery)) {
            //no bug,never here
            throw CriteriaUtils.criteriaError(query, _Exceptions::unknownRowSetType, query);
        } else if (query instanceof Insert._StandardInsertQuery) {
            unionSpec = new InsertBracketSubQuery<>((Insert._StandardInsertQuery) query, newContext);
        } else if (query instanceof Insert._StandardParentInsertQuery) {
            unionSpec = new ParentInsertBracketSubQuery<>((Insert._StandardParentInsertQuery<?>) query, newContext);
        } else {
            unionSpec = new BracketSubQuery<>((SubQuery) query, newContext);
        }
        return (_UnionOrderBySpec<C, Q>) unionSpec;
    }


    static <C, Q extends Query> _UnionOrderBySpec<C, Q> unionQuery(final Q left, final UnionType unionType, final RowSet right) {
        switch (unionType) {
            case UNION:
            case UNION_ALL:
            case UNION_DISTINCT:
                break;
            default:
                throw _Exceptions.unexpectedEnum(unionType);
        }
        left.prepared();
        // never validate right,possibly union and select
        CriteriaUtils.assertSelectItemSizeMatch(left, right);

        final CriteriaContext newContext;
        newContext = CriteriaContexts.unionContext(left, right);
        final _UnionOrderBySpec<C, ?> unionSpec;
        if (left instanceof Select) {
            if (!(right instanceof Select && right instanceof StandardQuery)) {
                String m = String.format("standard query api support only standard %s.", Select.class.getName());
                throw CriteriaUtils.criteriaError(left, m);
            }
            unionSpec = new UnionSelect<>((Select) left, unionType, (Select) right, newContext);
        } else if (left instanceof ScalarSubQuery) {
            if (!(right instanceof ScalarExpression && right instanceof StandardQuery)) {
                String m;
                m = String.format("standard scalar sub query api support only standard %s."
                        , ScalarExpression.class.getName());
                throw CriteriaUtils.criteriaError(left, m);
            }
            unionSpec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType
                    , (ScalarExpression) right, newContext);
        } else if (!(left instanceof SubQuery)) {
            //no bug,never here
            throw CriteriaUtils.criteriaError(left, _Exceptions::unknownRowSetType, left);
        } else if (!(right instanceof SubQuery && right instanceof StandardQuery)) {
            String m = String.format("standard sub query api support only standard %s.", SubQuery.class.getName());
            throw CriteriaUtils.criteriaError(left, m);
        } else if (left instanceof Insert._StandardInsertQuery) {
            unionSpec = new InsertUnionSubQuery<>((Insert._StandardInsertQuery) left, unionType, (SubQuery) right, newContext);
        } else if (left instanceof Insert._StandardParentInsertQuery) {
            unionSpec = new ParentInsertUnionSubQuery<>((Insert._StandardParentInsertQuery<?>) left, unionType, (SubQuery) right, newContext);
        } else {
            unionSpec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right, newContext);
        }
        return (_UnionOrderBySpec<C, Q>) unionSpec;
    }


    static <C, Q extends Query> _UnionOrderBySpec<C, Q> noActionQuery(final RowSet query) {

        final CriteriaContext newContext;
        newContext = CriteriaContexts.noActionContext(query);

        final _UnionOrderBySpec<C, ?> spec;
        if (query instanceof Select) {
            spec = new NoActionSelect<>((Select) query, newContext);
        } else if (query instanceof ScalarSubQuery) {
            spec = new NoActionScalarSubQuery<>((ScalarExpression) query, newContext);
        } else if (!(query instanceof SubQuery)) {
            //no bug,never here
            throw CriteriaUtils.criteriaError(query, _Exceptions::unknownRowSetType, query);
        } else if (query instanceof Insert._StandardInsertQuery) {
            spec = new InsertNoActionSubQuery<>((Insert._StandardInsertQuery) query, newContext);
        } else if (query instanceof Insert._StandardParentInsertQuery) {
            spec = new ParentInsertNoActionSubQuery<>((Insert._StandardParentInsertQuery<?>) query, newContext);
        } else {
            spec = new NoActionSubQuery<>((SubQuery) query, newContext);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }


    private StandardUnionQuery(Q left, CriteriaContext context) {
        super(left, context);

    }


    @Override
    public final String toString() {
        final String s;
        if (this instanceof Select && this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
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

    private static final class ParentInsertBracketSubQuery<C, CT>
            extends StandardUnionQuery<C, Insert._StandardParentInsertQuery<CT>>
            implements SubQuery, BracketRowSet, StandardSimpleQuery.ParentInsertSubQuerySpec<CT>
            , Insert._StandardParentInsertQuery<CT> {

        private ParentInsertBracketSubQuery(Insert._StandardParentInsertQuery<CT> left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public CT child() {
            this.prepared();
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .child();
        }

        @Override
        public Insert._StandardChildSpec<CT> fromLeft(final SubQuery query) {
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(query);
        }


    }//ParentInsertBracketSubQuery

    private static final class InsertBracketSubQuery<C> extends StandardUnionQuery<C, Insert._StandardInsertQuery>
            implements SubQuery, BracketRowSet
            , Insert._StandardInsertQuery
            , StandardSimpleQuery.InsertSubQuerySpec {

        private InsertBracketSubQuery(Insert._StandardInsertQuery left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardSimpleQuery.InsertSubQuerySpec) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public Insert._InsertSpec fromLeft(final SubQuery query) {
            return ((StandardSimpleQuery.InsertSubQuerySpec) this.left)
                    .fromLeft(query);
        }


    }//InsertBracketSubQuery


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

    private static final class ParentInsertNoActionSubQuery<C, CT>
            extends StandardUnionQuery<C, Insert._StandardParentInsertQuery<CT>>
            implements SubQuery, NoActionRowSet
            , StandardSimpleQuery.ParentInsertSubQuerySpec<CT>
            , Insert._StandardParentInsertQuery<CT> {

        private ParentInsertNoActionSubQuery(Insert._StandardParentInsertQuery<CT> left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public CT child() {
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .child();
        }

        @Override
        public Insert._StandardChildSpec<CT> fromLeft(SubQuery query) {
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(query);
        }


    }//ParentInsertNoActionSubQuery

    private static final class InsertNoActionSubQuery<C> extends StandardUnionQuery<C, Insert._StandardInsertQuery>
            implements SubQuery, NoActionRowSet
            , Insert._StandardInsertQuery
            , StandardSimpleQuery.InsertSubQuerySpec {

        private InsertNoActionSubQuery(Insert._StandardInsertQuery left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardSimpleQuery.InsertSubQuerySpec) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public Insert._InsertSpec fromLeft(final SubQuery query) {
            return ((StandardSimpleQuery.InsertSubQuerySpec) this.left)
                    .fromLeft(query);
        }


    }//InsertNoActionSubQuery


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

        final Query right;

        UnionQuery(Q left, UnionType unionType, Query right, CriteriaContext context) {
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

        private UnionSelect(Select left, UnionType unionType, Select right, CriteriaContext context) {
            super(left, unionType, right, context);
        }


    }//UnionSelect


    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q> implements SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, Q right, CriteriaContext criteriaContext) {
            super(left, unionType, right, criteriaContext);
        }


    }// UnionSubQuery

    private static final class ParentInsertUnionSubQuery<C, CT> extends UnionQuery<C, Insert._StandardParentInsertQuery<CT>>
            implements SubQuery, Insert._StandardParentInsertQuery<CT>
            , StandardSimpleQuery.ParentInsertSubQuerySpec<CT> {

        private ParentInsertUnionSubQuery(Insert._StandardParentInsertQuery<CT> left, UnionType unionType, SubQuery right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public CT child() {
            this.prepared();
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .child();
        }

        @Override
        public Insert._StandardChildSpec<CT> fromLeft(final SubQuery query) {
            return ((StandardSimpleQuery.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(query);
        }

    }//ParentInsertUnionSubQuery


    private static final class InsertUnionSubQuery<C> extends UnionQuery<C, Insert._StandardInsertQuery>
            implements SubQuery, Insert._StandardInsertQuery, StandardSimpleQuery.InsertSubQuerySpec {

        private InsertUnionSubQuery(Insert._StandardInsertQuery left, UnionType unionType, SubQuery right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardSimpleQuery.InsertSubQuerySpec) this.left).fromLeft(this)
                    .asInsert();
        }

        @Override
        public Insert._InsertSpec fromLeft(final SubQuery query) {
            return ((StandardSimpleQuery.InsertSubQuerySpec) this.left).fromLeft(query);
        }


    }//InsertUnionSubQuery


    private static final class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType
                , ScalarExpression right, CriteriaContext criteriaContext) {
            super(left, unionType, right, criteriaContext);
        }


    }//UnionScalarSubQuery


}
