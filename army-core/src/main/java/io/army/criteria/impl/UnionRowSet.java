package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;

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
        implements _UnionRowSet, TypeInfer {

    final Q left;

    UnionRowSet(Q left, CriteriaContext criteriaContext) {
        super(criteriaContext, JoinableClause.voidClauseCreator());
        this.left = left;
        if (this instanceof SubStatement) {
            ContextStack.push(this.context);
        } else {
            ContextStack.setContextStack(this.context);
        }
    }


    @Override
    public final void appendSql(final _SqlContext context) {
        if (this instanceof BracketRowSet) {
            final StringBuilder builder = context.sqlBuilder();
            if (builder.length() == 0) {
                builder.append(_Constant.LEFT_PAREN);
            } else {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            context.parser().rowSet(this.left, context);
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        } else if (this instanceof RowSetWithUnion) {
            final DialectParser dialect = context.parser();
            dialect.rowSet(this.left, context);

            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(((RowSetWithUnion) this).unionType().keyWords);

            dialect.rowSet(((RowSetWithUnion) this).rightRowSet(), context);
        } else if (!(this instanceof NoActionRowSet)) {
            throw new IllegalStateException("error implementation");
        }
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        return ((_PartRowSet) this.left).selectItemList();
    }

    @Override
    public final int selectionSize() {
        return ((_PartRowSet) this.left).selectionSize();
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
        if (!(this instanceof DerivedTable)) {
            throw CriteriaUtils.unknownSelection(this.context, derivedFieldName);
        }
        return ((DerivedTable) this.left).selection(derivedFieldName);
    }

    @Override
    public final TypeMeta typeMeta() {
        final Q left = this.left;
        if (!(left instanceof ScalarExpression)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((ScalarExpression) left).typeMeta();
    }


    @SuppressWarnings("unchecked")
    @Override
    final Q internalAsRowSet(final boolean fromAsQueryMethod) {
        if (this instanceof SubStatement) {
            ContextStack.pop(this.context);
        } else {
            ContextStack.clearContextStack(this.context);
        }
        final Q query;
        if (this instanceof ScalarSubQuery) {
            query = (Q) ScalarQueryExpression.from((ScalarSubQuery) this);
        } else {
            query = (Q) this;
        }
        return query;
    }

    @Override
    final void internalClear() {
        //no-op
    }

    /*################################## blow JoinableClause method ##################################*/




    interface BracketRowSet {

    }

    interface NoActionRowSet {


    }

    interface RowSetWithUnion {

        UnionType unionType();

        RowSet rightRowSet();
    }


}
