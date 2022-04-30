package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._LateralSubQuery;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;

/**
 * <p>
 * This class is base class of all union {@link io.army.criteria.RowSet}.
 * </p>
 *
 * @since 1.0
 */
abstract class UnionRowSet<C, Q extends RowSet, UR, OR, LR, SP>
        extends PartRowSet<C, Q, Void, Void, Void, Void, Void, Void, Void, UR, OR, LR, SP>
        implements _UnionRowSet {

    final Q left;

    UnionRowSet(Q left) {
        super(CriteriaContexts.unionContext(left));
        this.left = left;
        if (this instanceof NonPrimaryStatement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }


    @Override
    public final void appendSql(final _SqlContext context) {
        if (this instanceof BracketRowSet) {
            final StringBuilder builder = context.sqlBuilder();
            if (builder.length() == 0) {
                builder.append(Constant.LEFT_BRACKET);
            } else {
                builder.append(Constant.SPACE_LEFT_BRACKET);
            }
            context.dialect().rowSet(this.left, context);
            builder.append(Constant.SPACE_RIGHT_BRACKET);
        } else if (this instanceof RowSetWithUnion) {
            final _Dialect dialect = context.dialect();
            dialect.rowSet(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(((RowSetWithUnion) this).unionType().keyWords);

            dialect.rowSet(((RowSetWithUnion) this).rightRowSet(), context);
        } else {
            throw new IllegalStateException("error implementation");
        }
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        return ((_PartRowSet) this.left).selectItemList();
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

    public final ParamMeta paramMeta() {
        final Q left = this.left;
        if (!(left instanceof ScalarExpression)) {
            throw _Exceptions.castCriteriaApi();
        }
        return ((ScalarExpression) left).paramMeta();
    }


    @SuppressWarnings("unchecked")
    @Override
    final Q internalAsRowSet(final boolean fromAsQueryMethod) {
        if (this instanceof NonPrimaryStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        final Q query;
        if (this instanceof ScalarSubQuery) {
            query = (Q) ScalarSubQueryExpression.create((ScalarSubQuery) this);
        } else {
            query = (Q) this;
        }
        if (this.left instanceof _LateralSubQuery
                && !(this instanceof _LateralSubQuery)) {
            String m = String.format("%s don't implements %s."
                    , this.getClass().getName(), _LateralSubQuery.class.getName());
            throw new IllegalStateException(m);
        }
        return query;
    }

    @Override
    final void internalClear() {
        //no-op
    }

    /*################################## blow JoinableClause method ##################################*/

    @Override
    final _TableBlock createTableBlockWithoutOnClause(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createNextClauseWithoutOnClause(_JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }

    interface BracketRowSet {

    }

    interface RowSetWithUnion {

        UnionType unionType();

        RowSet rightRowSet();
    }


}
