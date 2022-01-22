package io.army.criteria.impl;

import io.army.Database;
import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.impl.inner._UnionQuery;
import io.army.criteria.mysql.MySQL80Query;
import io.army.dialect.Constant;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.List;

@SuppressWarnings("unchecked")
abstract class MySQL80UnionQuery<C, Q extends Query> extends PartQuery<
        C,
        Q,
        MySQL80Query.UnionOrderBy80Spec<C, Q>,
        MySQL80Query.UnionLimit80Spec<C, Q>,
        MySQL80Query.Union80Spec<C, Q>,
        MySQL80Query.With80Spec<C, Q>>
        implements MySQL80Query, MySQL80Query.UnionOrderBy80Spec<C, Q>, _UnionQuery {

    static <C, Q extends Query> UnionOrderBy80Spec<C, Q> bracketQuery(Q query) {
        query.prepared();
        final UnionOrderBy80Spec<C, ?> spec;
        if (query instanceof Select) {
            spec = new BracketSelect<>((Select) query);
        } else if (query instanceof ScalarSubQuery) {
            spec = new BracketScalarSubQuery<>((ScalarQueryExpression<?>) query);
        } else if (query instanceof ColumnSubQuery) {
            spec = new BracketColumnSubQuery<>((ColumnSubQuery) query);
        } else if (query instanceof RowSubQuery) {
            spec = new BracketRowSubQuery<>((RowSubQuery) query);
        } else if (query instanceof SubQuery) {
            spec = new BracketSubQuery<>((SubQuery) query);
        } else {
            throw _Exceptions.unknownQueryType(query);
        }
        return (UnionOrderBy80Spec<C, Q>) spec;
    }

    static <C, Q extends Query> UnionOrderBy80Spec<C, Q> unionQuery(Q left, UnionType unionType, Q right) {
        left.prepared();
        final UnionOrderBy80Spec<C, ?> spec;
        if (left instanceof Select) {
            spec = new UnionSelect<>((Select) left, unionType, (Select) right);
        } else if (left instanceof ScalarSubQuery) {
            spec = new UnionScalarSubQuery<>((ScalarQueryExpression<?>) left, unionType, (ScalarQueryExpression<?>) right);
        } else if (left instanceof ColumnSubQuery) {
            spec = new UnionColumnSubQuery<>((ColumnSubQuery) left, unionType, (ColumnSubQuery) right);
        } else if (left instanceof RowSubQuery) {
            spec = new UnionRowSubQuery<>((RowSubQuery) left, unionType, (RowSubQuery) right);
        } else if (left instanceof SubQuery) {
            spec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right);
        } else {
            throw _Exceptions.unknownQueryType(left);
        }
        return (UnionOrderBy80Spec<C, Q>) spec;
    }

    final Q left;

    private MySQL80UnionQuery(final Q left) {
        super(CriteriaUtils.getUnionContext(left));
        this.left = left;
    }

    @Override
    public final List<SelectPart> selectPartList() {
        return ((_PartQuery) this.left).selectPartList();
    }

    @Override
    public final UnionOrderBy80Spec<C, Q> bracketsQuery() {
        return MySQL80UnionQuery.bracketQuery(this.left);
    }

    @Override
    final Q internalAsQuery(final boolean outer) {
        final Q query;
        if (this instanceof ScalarSubQuery) {
            query = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
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
        return Dialect.MYSQL80;
    }

    @Override
    final void validateDialect(Dialect mode) {
        if (mode.database() != Database.MySQL || mode.version() < 80) {
            throw _Exceptions.stmtDontSupportDialect(mode);
        }
    }

    @Override
    final UnionOrderBy80Spec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return MySQL80UnionQuery.unionQuery(left, unionType, right);
    }

    @Override
    final With80Spec<C, Q> asQueryAndQuery(UnionType unionType) {
        return MySQL80SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }


    private static final class BracketSelect<C> extends MySQL80UnionQuery<C, Select> implements Select {

        private BracketSelect(Select left) {
            super(left);
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

    }//BracketSelect

    private static class BracketSubQuery<C, Q extends SubQuery> extends MySQL80UnionQuery<C, Q>
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

    }//BracketSubQuery

    private static final class BracketRowSubQuery<C> extends BracketSubQuery<C, RowSubQuery>
            implements RowSubQuery {

        private BracketRowSubQuery(RowSubQuery left) {
            super(left);
        }

    }//BracketRowSubQuery


    private static final class BracketColumnSubQuery<C> extends BracketSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private BracketColumnSubQuery(ColumnSubQuery left) {
            super(left);
        }

    }//BracketColumnSubQuery

    private static final class BracketScalarSubQuery<C, E> extends BracketSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private BracketScalarSubQuery(ScalarQueryExpression<E> left) {
            super(left);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//BracketScalarSubQuery

    private static final class UnionSelect<C> extends MySQL80UnionQuery<C, Select> implements Select {

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

    private static class UnionSubQuery<C, Q extends SubQuery> extends MySQL80UnionQuery<C, Q> implements SubQuery {

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

    private static final class UnionRowSubQuery<C> extends UnionSubQuery<C, RowSubQuery>
            implements RowSubQuery {

        private UnionRowSubQuery(RowSubQuery left, UnionType unionType, RowSubQuery right) {
            super(left, unionType, right);
        }

    }//UnionRowSubQuery


    private static final class UnionColumnSubQuery<C> extends UnionSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private UnionColumnSubQuery(ColumnSubQuery left, UnionType unionType, ColumnSubQuery right) {
            super(left, unionType, right);
        }

    }//UnionColumnSubQuery


    private static final class UnionScalarSubQuery<C, E> extends UnionSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private UnionScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType
                , ScalarQueryExpression<?> right) {
            super(left, unionType, (ScalarQueryExpression<E>) right);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//UnionScalarSubQuery


}
