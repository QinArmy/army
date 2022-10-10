package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.mysql.MySQLDialect;
import io.army.util._Exceptions;

import java.util.function.Function;

/**
 * @see StandardQueries
 */
@Deprecated
@SuppressWarnings("unchecked")
abstract class StandardUnionQueries<C, Q extends Query> extends UnionRowSet<
        C,
        Q,
        StandardQuery._UnionOrderBySpec<C, Q>, //UR
        StandardQuery._UnionLimitSpec<C, Q>,//OR
        StandardQuery._UnionSpec<C, Q>,//LR
        StandardQuery._SelectSpec<C, Q>>// SP
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
        } else if (query instanceof StandardInsert._InsertQuery) {
            unionSpec = new InsertBracketSubQuery<>((StandardInsert._InsertQuery) query, newContext);
        } else if (query instanceof StandardInsert._ParentInsertQuery) {
            unionSpec = new ParentInsertBracketSubQuery<>((StandardInsert._ParentInsertQuery<?>) query, newContext);
        } else {
            unionSpec = new BracketSubQuery<>((SubQuery) query, newContext);
        }
        return (_UnionOrderBySpec<C, Q>) unionSpec;
    }


    static <Q extends Item> _UnionOrderBySpec<Q> unionSubQuery(final SubQuery left, final UnionType unionType, final SubQuery right, Function<SubQuery, Q> function) {
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
        } else if (left instanceof StandardInsert._InsertQuery) {
            unionSpec = new InsertUnionSubQuery<>((StandardInsert._InsertQuery) left, unionType, (SubQuery) right, newContext);
        } else if (left instanceof StandardInsert._ParentInsertQuery) {
            unionSpec = new ParentInsertUnionSubQuery<>((StandardInsert._ParentInsertQuery<?>) left, unionType, (SubQuery) right, newContext);
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
        } else if (query instanceof StandardInsert._InsertQuery) {
            spec = new InsertNoActionSubQuery<>((StandardInsert._InsertQuery) query, newContext);
        } else if (query instanceof StandardInsert._ParentInsertQuery) {
            spec = new ParentInsertNoActionSubQuery<>((StandardInsert._ParentInsertQuery<?>) query, newContext);
        } else {
            spec = new NoActionSubQuery<>((SubQuery) query, newContext);
        }
        return (_UnionOrderBySpec<C, Q>) spec;
    }


    private StandardUnionQueries(Q left, CriteriaContext context) {
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
    final _SelectSpec<C, Q> asUnionAndRowSet(UnionType unionType) {
        return StandardQueries.unionAndQuery(this.asQuery(), unionType);
    }


    private static final class BracketSelect<C> extends StandardUnionQueries<C, Select>
            implements Select, BracketRowSet {

        private BracketSelect(Select query, CriteriaContext criteriaContext) {
            super(query, criteriaContext);
        }

    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends StandardUnionQueries<C, Q>
            implements SubQuery, BracketRowSet {

        private BracketSubQuery(Q query, CriteriaContext criteriaContext) {
            super(query, criteriaContext);
        }

    }

    private static final class ParentInsertBracketSubQuery<C, CT>
            extends StandardUnionQueries<C, StandardInsert._ParentInsertQuery<CT>>
            implements SubQuery, BracketRowSet, StandardQueries.ParentInsertSubQuerySpec<CT>
            , StandardInsert._ParentInsertQuery<CT> {

        private ParentInsertBracketSubQuery(StandardInsert._ParentInsertQuery<CT> left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public CT child() {
            this.prepared();
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .child();
        }

        @Override
        public StandardInsert._ChildPartSpec<CT> fromLeft(final SubQuery query) {
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(query);
        }


    }//ParentInsertBracketSubQuery

    private static final class InsertBracketSubQuery<C> extends StandardUnionQueries<C, StandardInsert._InsertQuery>
            implements SubQuery, BracketRowSet
            , StandardInsert._InsertQuery
            , StandardQueries.InsertSubQuerySpec {

        private InsertBracketSubQuery(StandardInsert._InsertQuery left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardQueries.InsertSubQuerySpec) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public Insert._InsertSpec fromLeft(final SubQuery query) {
            return ((StandardQueries.InsertSubQuerySpec) this.left)
                    .fromLeft(query);
        }


    }//InsertBracketSubQuery


    private static final class BracketScalarSubQuery<C> extends BracketSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private BracketScalarSubQuery(ScalarExpression query, CriteriaContext criteriaContext) {
            super(query, criteriaContext);
        }

    }//BracketScalarSubQuery

    private static final class NoActionSelect<C> extends StandardUnionQueries<C, Select>
            implements Select, NoActionRowSet {

        private NoActionSelect(Select left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//NoActionSelect

    private static class NoActionSubQuery<C, Q extends SubQuery> extends StandardUnionQueries<C, Q>
            implements SubQuery, NoActionRowSet {

        private NoActionSubQuery(Q left, CriteriaContext criteriaContext) {
            super(left, criteriaContext);
        }

    }//NoActionSubQuery

    private static final class ParentInsertNoActionSubQuery<C, CT>
            extends StandardUnionQueries<C, StandardInsert._ParentInsertQuery<CT>>
            implements SubQuery, NoActionRowSet
            , StandardQueries.ParentInsertSubQuerySpec<CT>
            , StandardInsert._ParentInsertQuery<CT> {

        private ParentInsertNoActionSubQuery(StandardInsert._ParentInsertQuery<CT> left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public CT child() {
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .child();
        }

        @Override
        public StandardInsert._ChildPartSpec<CT> fromLeft(SubQuery query) {
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(query);
        }


    }//ParentInsertNoActionSubQuery

    private static final class InsertNoActionSubQuery<C> extends StandardUnionQueries<C, StandardInsert._InsertQuery>
            implements SubQuery, NoActionRowSet
            , StandardInsert._InsertQuery
            , StandardQueries.InsertSubQuerySpec {

        private InsertNoActionSubQuery(StandardInsert._InsertQuery left, CriteriaContext context) {
            super(left, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardQueries.InsertSubQuerySpec) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public Insert._InsertSpec fromLeft(final SubQuery query) {
            return ((StandardQueries.InsertSubQuerySpec) this.left)
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
    private static abstract class UnionQuery<C, Q extends Query> extends StandardUnionQueries<C, Q>
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

    private static final class ParentInsertUnionSubQuery<C, CT> extends UnionQuery<C, StandardInsert._ParentInsertQuery<CT>>
            implements SubQuery, StandardInsert._ParentInsertQuery<CT>
            , StandardQueries.ParentInsertSubQuerySpec<CT> {

        private ParentInsertUnionSubQuery(StandardInsert._ParentInsertQuery<CT> left, UnionType unionType, SubQuery right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .asInsert();
        }

        @Override
        public CT child() {
            this.prepared();
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(this)
                    .child();
        }

        @Override
        public StandardInsert._ChildPartSpec<CT> fromLeft(final SubQuery query) {
            return ((StandardQueries.ParentInsertSubQuerySpec<CT>) this.left)
                    .fromLeft(query);
        }

    }//ParentInsertUnionSubQuery


    private static final class InsertUnionSubQuery<C> extends UnionQuery<C, StandardInsert._InsertQuery>
            implements SubQuery, StandardInsert._InsertQuery, StandardQueries.InsertSubQuerySpec {

        private InsertUnionSubQuery(StandardInsert._InsertQuery left, UnionType unionType, SubQuery right, CriteriaContext context) {
            super(left, unionType, right, context);
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return ((StandardQueries.InsertSubQuerySpec) this.left).fromLeft(this)
                    .asInsert();
        }

        @Override
        public Insert._InsertSpec fromLeft(final SubQuery query) {
            return ((StandardQueries.InsertSubQuerySpec) this.left).fromLeft(query);
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
