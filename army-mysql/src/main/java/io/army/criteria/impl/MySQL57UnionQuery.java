package io.army.criteria.impl;

import io.army.Database;
import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.impl.inner._UnionQuery;
import io.army.criteria.mysql.MySQL57Query;
import io.army.dialect.Constant;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.List;

@SuppressWarnings("unchecked")
abstract class MySQL57UnionQuery<C, Q extends Query> extends PartQuery<
        C,
        Q,
        MySQL57Query.UnionOrderBy57Spec<C, Q>,
        MySQL57Query.UnionLimit57Spec<C, Q>,
        MySQL57Query.Union57Spec<C, Q>,
        MySQL57Query.Select57Spec<C, Q>>
        implements MySQL57Query, MySQL57Query.UnionOrderBy57Spec<C, Q>, _UnionQuery {

    static <C, Q extends Query> UnionOrderBy57Spec<C, Q> bracketQuery(final Q query) {
        query.prepared();
        final UnionOrderBy57Spec<C, ?> spec;
        if (query instanceof Select) {
            spec = new BracketSelect<>((Select) query);
        } else if (query instanceof ScalarSubQuery) {
            spec = new BracketScalarSubQuery<>((ScalarExpression) query);
        } else if (query instanceof SubQuery) {
            spec = new BracketSubQuery<>((SubQuery) query);
        } else {
            throw _Exceptions.unknownQueryType(query);
        }
        return (UnionOrderBy57Spec<C, Q>) spec;
    }


    static <C, Q extends Query> UnionOrderBy57Spec<C, Q> unionQuery(Q left, UnionType unionType, Q right) {
        left.prepared();
        final UnionOrderBy57Spec<C, ?> spec;
        if (left instanceof Select) {
            spec = new UnionSelect<>((Select) left, unionType, (Select) right);
        } else if (left instanceof ScalarSubQuery) {
            spec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType, (ScalarExpression) right);
        } else if (left instanceof SubQuery) {
            spec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right);
        } else {
            throw _Exceptions.unknownQueryType(left);
        }
        return (UnionOrderBy57Spec<C, Q>) spec;
    }


    final Q left;

    MySQL57UnionQuery(Q left) {
        super(CriteriaContexts.unionContext(left));
        this.left = left;
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        return ((_PartQuery) this.left).selectItemList();
    }

    @Override
    public final MySQL57Query.UnionOrderBy57Spec<C, Q> bracket() {
        return bracketQuery(this.asQuery());
    }


    @Override
    final UnionOrderBy57Spec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return unionQuery(left, unionType, right);
    }

    @Override
    final Select57Spec<C, Q> asQueryAndQuery(UnionType unionType) {
        return MySQL57SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final Q internalAsQuery(boolean justAsQuery) {
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
        if (mode.database() != Database.MySQL) {
            throw new IllegalArgumentException(String.format("Don't support dialect[%s]", mode));
        }
    }


    private static final class BracketSelect<C> extends MySQL57UnionQuery<C, Select>
            implements Select {

        private BracketSelect(Select select) {
            super(select);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().select(this.left, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);

        }

    }//BracketQuery

    private static class BracketSubQuery<C, Q extends SubQuery> extends MySQL57UnionQuery<C, Q>
            implements SubQuery {


        private BracketSubQuery(Q left) {
            super(left);
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().subQuery(this.left, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

    }//


    private static final class BracketScalarSubQuery<C> extends BracketSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private BracketScalarSubQuery(ScalarExpression left) {
            super(left);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//BracketScalarSubQuery


    private static final class UnionSelect<C> extends MySQL57UnionQuery<C, Select>
            implements Select {

        private final UnionType unionType;

        private final Select right;

        private UnionSelect(Select left, UnionType unionType, Select right) {
            super(left);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public void appendSql(final _SqlContext context) {

            final _Dialect dialect = context.dialect();
            dialect.select(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.select(this.right, context);

        }

    }//UnionSelect

    private static class UnionSubQuery<C, Q extends SubQuery> extends MySQL57UnionQuery<C, Q>
            implements SubQuery {

        private final UnionType unionType;

        private final Q right;

        private UnionSubQuery(Q left, UnionType unionType, Q right) {
            super(left);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final _Dialect dialect = context.dialect();
            dialect.subQuery(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.subQuery(this.right, context);
        }

    }//UnionSubQuery


    private static final class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType, ScalarExpression right) {
            super(left, unionType, right);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//UnionScalarSubQuery


}
