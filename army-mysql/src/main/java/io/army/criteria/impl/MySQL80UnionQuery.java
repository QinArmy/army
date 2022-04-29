package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._LateralSubQuery;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.criteria.mysql.MySQL80Query;
import io.army.dialect.Constant;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.session.Database;
import io.army.util._Exceptions;

import java.util.List;

/**
 * <p>
 * This class is base class all the implementation of MySQL 8.0 UNION clause syntax.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @param <C> java type of criteria object for dynamic statement
 * @param <Q> {@link Select} or {@link SubQuery} or {@link ScalarExpression}
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/union.html">UNION Clause</a>
 * @see MySQL80SimpleQuery
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQL80UnionQuery<C, Q extends Query> extends PartRowSet<
        C,
        Q,
        MySQL80Query.UnionOrderBy80Spec<C, Q>,
        MySQL80Query.UnionLimit80Spec<C, Q>,
        MySQL80Query.UnionSpec<C, Q>,
        MySQL80Query.With80Spec<C, Q>>
        implements MySQL80Query, MySQL80Query.UnionOrderBy80Spec<C, Q>, _UnionRowSet {

    static <C, Q extends Query> UnionOrderBy80Spec<C, Q> bracketQuery(final Q query) {
        query.prepared();
        final UnionOrderBy80Spec<C, ?> spec;
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
        return (UnionOrderBy80Spec<C, Q>) spec;
    }

    static <C, Q extends Query> UnionOrderBy80Spec<C, Q> unionQuery(Q left, UnionType unionType, Q right) {
        left.prepared();
        final UnionOrderBy80Spec<C, ?> spec;
        if (left instanceof Select) {
            spec = new UnionSelect<>((Select) left, unionType, (Select) right);
        } else if (left instanceof ScalarSubQuery) {
            if (left instanceof _LateralSubQuery) {
                spec = new LateralUnionScalarSubQuery<>((ScalarExpression) left, unionType, (ScalarExpression) right);
            } else {
                spec = new UnionScalarSubQuery<>((ScalarExpression) left, unionType, (ScalarExpression) right);
            }
        } else if (left instanceof SubQuery) {
            if (left instanceof _LateralSubQuery) {
                spec = new LateralUnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right);
            } else {
                spec = new UnionSubQuery<>((SubQuery) left, unionType, (SubQuery) right);
            }
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (UnionOrderBy80Spec<C, Q>) spec;
    }

    final Q left;

    private MySQL80UnionQuery(final Q left) {
        super(CriteriaContexts.unionContext(left));
        this.left = left;
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        return ((_PartRowSet) this.left).selectItemList();
    }

    @Override
    public final UnionOrderBy80Spec<C, Q> bracket() {
        return MySQL80UnionQuery.bracketQuery(this.left);
    }

    @Override
    final Q internalAsRowSet(final boolean fromAsQueryMethod) {
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
        return Dialect.MySQL80;
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
    final With80Spec<C, Q> asUnionAndRowSet(UnionType unionType) {
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

        @Override
        public final ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//BracketScalarSubQuery

    private static final class LateralBracketScalarSubQuery<C> extends BracketScalarSubQuery<C>
            implements _LateralSubQuery {

        private LateralBracketScalarSubQuery(ScalarExpression left) {
            super(left);
        }

    }//LateralBracketScalarSubQuery

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

    private static final class LateralUnionSubQuery<C> extends UnionSubQuery<C, SubQuery>
            implements _LateralSubQuery {

        private LateralUnionSubQuery(SubQuery left, UnionType unionType, SubQuery right) {
            super(left, unionType, right);
        }

    }//LateralUnionSubQuery


    private static class UnionScalarSubQuery<C> extends UnionSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionScalarSubQuery(ScalarExpression left, UnionType unionType
                , ScalarExpression right) {
            super(left, unionType, right);
        }

        @Override
        public final ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

    }//UnionScalarSubQuery

    private static final class LateralUnionScalarSubQuery<C> extends UnionScalarSubQuery<C>
            implements _LateralSubQuery {

        private LateralUnionScalarSubQuery(ScalarExpression left, UnionType unionType, ScalarExpression right) {
            super(left, unionType, right);
        }

    }//LateralUnionScalarSubQuery


}
