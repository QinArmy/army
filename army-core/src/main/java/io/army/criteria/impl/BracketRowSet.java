package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._ParensRowSet;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.util._Assert;

import java.util.List;

@SuppressWarnings("unchecked")
abstract class BracketRowSet<I extends Item, RR, OR, LR, LO, LF, SP>
        extends LimitRowOrderByClause<OR, LR, LO, LF>
        implements _ParensRowSet,
        Query._QueryUnionClause<SP>,
        Query._QueryIntersectClause<SP>,
        Query._QueryExceptClause<SP>,
        Query._QueryMinusClause<SP>,
        TabularItem.DerivedTableSpec,
        Query._AsQueryClause<I>,
        Statement._RightParenClause<RR>,
        RowSet {


    private RowSet rowSet;

    private Boolean prepared;

    BracketRowSet(CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
    }


    @Override
    public final RR rightParen() {
        return (RR) this;
    }

    @Override
    public final SP union() {
        return this.unionQuery(UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.unionQuery(UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.unionQuery(UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.unionQuery(UnionType.INTERSECT);
    }

    @Override
    public final SP intersectAll() {
        return this.unionQuery(UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.unionQuery(UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.unionQuery(UnionType.EXCEPT);
    }

    @Override
    public final SP exceptAll() {
        return this.unionQuery(UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.unionQuery(UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.unionQuery(UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.unionQuery(UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.unionQuery(UnionType.MINUS_DISTINCT);
    }

    @Override
    public final I asQuery() {
        this.endQueryStatement();
        return this.onAsQuery();
    }


    @Override
    public final RowSet innerRowSet() {
        final RowSet rowSet = this.rowSet;
        if (rowSet == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return rowSet;
    }

    @Override
    public final int selectionSize() {
        final RowSet rowSet = this.rowSet;
        if (rowSet == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((_PartRowSet) rowSet).selectionSize();
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        final RowSet rowSet = this.rowSet;
        if (rowSet == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((_PartRowSet) rowSet).selectItemList();
    }

    @Override
    public final Selection selection(final String derivedAlias) {
        final RowSet rowSet = this.rowSet;
        if (!(rowSet instanceof DerivedTable)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((DerivedTable) rowSet).selection(derivedAlias);
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.rowSet = null;
        this.clearOrderByList();
        this.prepared = Boolean.FALSE;
    }


    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }


    final Statement._RightParenClause<RR> parenRowSetEnd(final RowSet parenRowSet) {
        if (this.rowSet != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.rowSet = parenRowSet;
        this.context.onSetInnerContext(((CriteriaContextSpec) parenRowSet).getContext());
        return this;
    }


    void onEndQuery() {
        //no-op
    }


    abstract I onAsQuery();


    abstract SP createUnionRowSet(UnionType unionType);


    private SP unionQuery(final UnionType unionType) {
        this.endQueryStatement();
        return this.createUnionRowSet(unionType);
    }

    private void endQueryStatement() {
        _Assert.nonPrepared(this.prepared);
        final CriteriaContext context = this.context;
        if (this.rowSet == null) {
            throw ContextStack.castCriteriaApi(context);
        }
        this.endOrderByClause();
        this.onEndQuery();

        context.endContext();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


}
