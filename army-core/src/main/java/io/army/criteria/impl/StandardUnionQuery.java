package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.impl.inner._UnionQuery;
import io.army.dialect.Constant;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.List;

/**
 * @see StandardSimpleQuery
 */
@SuppressWarnings("unchecked")
abstract class StandardUnionQuery<C, Q extends Query> extends PartQuery<
        C,
        Q,
        StandardQuery.StandardUnionSpec<C, Q>, //UR
        StandardQuery.StandardLimitClause<C, Q>,//OR
        StandardQuery.StandardUnionClause<C, Q>,//LR
        StandardQuery.StandardSelectSpec<C, Q>>// SP
        implements StandardQuery.StandardUnionSpec<C, Q>, _UnionQuery, StandardQuery {

    static <C, Q extends Query> StandardUnionSpec<C, Q> bracketQuery(final Q query) {
        query.prepared();
        final StandardUnionSpec<C, ?> unionSpec;
        if (query instanceof Select) {
            unionSpec = new BracketSelect<>((Select) query);
        } else if (query instanceof ScalarSubQuery) {
            unionSpec = new BracketScalarSubQuery<>((ScalarExpression) query);
        } else if (query instanceof SubQuery) {
            unionSpec = new BracketSubQuery<>((SubQuery) query);
        } else {
            throw _Exceptions.unknownQueryType(query);
        }
        return (StandardUnionSpec<C, Q>) unionSpec;
    }


    static <C, Q extends Query> StandardUnionSpec<C, Q> unionQuery(Q left, UnionType unionType, Q right) {
        left.prepared();
        // never validate right,possibly union and select
        CriteriaUtils.assertSelectItemSizeMatch(left, right);
        final StandardUnionSpec<C, ?> unionSpec;
        if (left instanceof Select) {
            unionSpec = new UnionSelect<>((Select) left, unionType, (Select) right);
        } else if (left instanceof ScalarSubQuery) {
            unionSpec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType, (ScalarExpression) right);
        } else if (left instanceof SubQuery) {
            unionSpec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right);
        } else {
            throw _Exceptions.unknownQueryType(left);
        }
        return (StandardUnionSpec<C, Q>) unionSpec;
    }


    final Q left;

    private StandardUnionQuery(Q left) {
        super(CriteriaContexts.unionContext(left));
        this.left = left;
        if (this instanceof Select) {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        } else {
            CriteriaContextStack.push(this.criteriaContext);
        }
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        return ((_PartQuery) this.left).selectItemList();
    }


    public final Selection selection() {
        if (!(this instanceof ScalarSubQuery)) {
            String m = String.format("this isn't %s instance.", ScalarSubQuery.class.getName());
            throw new IllegalStateException(m);
        }
        return (Selection) ((ScalarSubQuery) this.left).selectItemList().get(0);
    }

    @Nullable
    public final Selection selection(String derivedFieldName) {
        if (!(this instanceof SubQuery)) {
            String m = String.format("this isn't %s instance.", SubQuery.class.getName());
            throw new IllegalStateException(m);
        }
        return ((SubQuery) this.left).selection(derivedFieldName);
    }

    @Override
    public final StandardUnionSpec<C, Q> bracket() {
        return bracketQuery(this.asQuery());
    }

    @Override
    final StandardUnionSpec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return unionQuery(left, unionType, right);
    }

    @Override
    final StandardSelectSpec<C, Q> asQueryAndQuery(UnionType unionType) {
        return StandardSimpleQuery.asQueryAndQuery(this.asQuery(), unionType);
    }

    @SuppressWarnings("unchecked")
    @Override
    final Q internalAsQuery(final boolean justAsQuery) {
        if (this instanceof Select) {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        } else {
            CriteriaContextStack.pop(this.criteriaContext);
        }

        final Q query;
        if (this instanceof ScalarSubQuery) {
            query = (Q) ScalarSubQueryExpression.create((ScalarSubQuery) this);
        } else {
            query = (Q) this;
        }
        return query;
    }

    @Override
    final void internalClear() {
        //no-op
    }

    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    final void validateDialect(Dialect mode) {
        // no-op
    }


    private static final class BracketSelect<C> extends StandardUnionQuery<C, Select> implements Select {

        private BracketSelect(Select query) {
            super(query);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder();

            if (builder.length() == 0) {
                builder.append(Constant.LEFT_BRACKET);
            } else {
                builder.append(Constant.SPACE_LEFT_BRACKET);
            }
            context.dialect().select(this.left, context);

            builder.append(Constant.SPACE_RIGHT_BRACKET);

        }
    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends StandardUnionQuery<C, Q> implements SubQuery {


        private BracketSubQuery(Q query) {
            super(query);
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder();

            if (builder.length() == 0) {
                builder.append(Constant.LEFT_BRACKET);
            } else {
                builder.append(Constant.SPACE_LEFT_BRACKET);
            }

            context.dialect().subQuery(this.left, context);

            builder.append(Constant.SPACE_RIGHT_BRACKET);
        }


    }


    private static final class BracketScalarSubQuery<C> extends BracketSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private BracketScalarSubQuery(ScalarExpression query) {
            super(query);
        }


        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//BracketScalarSubQuery

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
    private static abstract class UnionQuery<C, Q extends Query> extends StandardUnionQuery<C, Q> {

        final UnionType unionType;

        final Q right;

        UnionQuery(Q left, UnionType unionType, Q right) {
            super(left);
            this.unionType = unionType;
            this.right = right;
        }


    }// UnionQuery


    private static final class UnionSelect<C> extends UnionQuery<C, Select> implements Select {

        private UnionSelect(Select left, UnionType unionType, Select right) {
            super(left, unionType, right);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final _Dialect dialect = context.dialect();
            dialect.select(this.left, context);

            context.sqlBuilder()
                    .append(this.unionType.keyWords);

            dialect.select(this.right, context);
        }

    }//UnionSelect


    private static class UnionSubQuery<C, Q extends SubQuery> extends UnionQuery<C, Q> implements SubQuery {

        private UnionSubQuery(Q left, UnionType unionType, Q right) {
            super(left, unionType, right);
        }


        @Override
        public final void appendSql(_SqlContext context) {
            final _Dialect dialect = context.dialect();
            dialect.subQuery(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.subQuery(this.right, context);
        }


    }// UnionSubQuery


    private static final class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType
                , ScalarExpression right) {
            super(left, unionType, right);
        }


        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }


    }//UnionScalarSubQuery


}
