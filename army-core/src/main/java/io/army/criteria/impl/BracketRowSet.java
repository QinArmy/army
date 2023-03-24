package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.util._Assert;

import java.util.List;

@SuppressWarnings("unchecked")
abstract class BracketRowSet<I extends Item, RR, OR, LR, LO, LF, SP>
        extends LimitRowOrderByClause<OR, LR, LO, LF>
        implements _ParensRowSet,
        RowSet._StaticUnionClause<SP>,
        RowSet._StaticIntersectClause<SP>,
        RowSet._StaticExceptClause<SP>,
        RowSet._StaticMinusClause<SP>,
        _SelectionMap,
        Query._AsQueryClause<I>,
        Statement._RightParenClause<RR>,
        _RowSet._SelectItemListSpec,
        RowSet {


    private _RowSet innerRowSet;

    private Boolean prepared;

    BracketRowSet(ArmyStmtSpec spec) {
        super(CriteriaContexts.bracketContext(spec)); //must migrate WITH clause and context when create bracket.
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
    public final _RowSet innerRowSet() {
        final _RowSet rowSet = this.innerRowSet;
        if (rowSet == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return rowSet;
    }

    @Override
    public final List<? extends _SelectItem> selectItemList() {
        final _RowSet rowSet = this.innerRowSet;
        if (rowSet == null || !(this instanceof _PrimaryRowSet)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((_PrimaryRowSet) rowSet).selectItemList();
    }

    @Override
    public final int selectionSize() {
        final _RowSet rowSet = this.innerRowSet;
        if (rowSet == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return rowSet.selectionSize();
    }

    @Override
    public final Selection refSelection(final String derivedAlias) {
        final RowSet rowSet = this.innerRowSet;
        if (!(rowSet instanceof DerivedTable)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((_DerivedTable) rowSet).refSelection(derivedAlias);
    }

    @Override
    public final List<? extends Selection> refAllSelection() {
        final RowSet rowSet = this.innerRowSet;
        if (!(rowSet instanceof DerivedTable)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((_DerivedTable) rowSet).refAllSelection();
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.innerRowSet = null;
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

    final RR parensEnd(final RowSet parenRowSet) {
        if (this.innerRowSet != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.innerRowSet = (_RowSet) parenRowSet;
        this.context.onSetInnerContext(((CriteriaContextSpec) parenRowSet).getContext());
        return (RR) this;
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
        if (this.innerRowSet == null) {
            throw ContextStack.castCriteriaApi(context);
        }
        this.endOrderByClause();
        this.onEndQuery();

        context.endContext();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


}
