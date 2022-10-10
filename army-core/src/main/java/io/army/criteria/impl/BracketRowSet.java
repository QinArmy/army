package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class BracketRowSet<I extends Item, Q extends RowSet, RR, OR, LR, SP, S extends RowSet, UR>
        extends LimitRowOrderByClause<OR, LR> implements _PartRowSet
        , Query._QueryUnionClause<SP>
        , Query._QueryIntersectClause<SP>
        , Query._QueryExceptClause<SP>
        , Query._QueryMinusClause<SP>
        , Query._RowSetUnionClause<S, UR>
        , Query._RowSetIntersectClause<S, UR>
        , Query._RowSetExceptClause<S, UR>
        , Query._RowSetMinusClause<S, UR>
        , TabularItem.DerivedTableSpec
        , Query._QuerySpec<I>, Statement._RightParenClause<RR>
        , Statement, _SelfDescribed {


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
    public final UR union(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.UNION, supplier);
    }

    @Override
    public final UR unionAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.UNION_ALL, supplier);
    }

    @Override
    public final UR unionDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.UNION_DISTINCT, supplier);
    }

    @Override
    public final UR intersect(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.INTERSECT, supplier);
    }

    @Override
    public final UR intersectAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.INTERSECT_ALL, supplier);
    }

    @Override
    public final UR intersectDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.INTERSECT_DISTINCT, supplier);
    }

    @Override
    public final UR except(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.EXCEPT, supplier);
    }

    @Override
    public final UR exceptAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.EXCEPT_ALL, supplier);
    }

    @Override
    public final UR exceptDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.EXCEPT_DISTINCT, supplier);
    }

    @Override
    public final UR minus(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.MINUS, supplier);
    }

    @Override
    public final UR minusAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.MINUS_ALL, supplier);
    }

    @Override
    public final UR minusDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.MINUS_DISTINCT, supplier);
    }

    @Override
    public final I asQuery() {
        this.endQueryStatement();
        return this.onAsQuery();
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
    public final void appendSql(final _SqlContext context) {
        final RowSet rowSet = this.rowSet;
        if (rowSet == null) {
            throw _Exceptions.castCriteriaApi();
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_LEFT_PAREN);
        context.parser().rowSet(rowSet, context);
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
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


    final Statement._RightParenClause<RR> parenRowSetEnd(final Q parenRowSet) {
        if (this.rowSet != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.rowSet = parenRowSet;
        return this;
    }



    void onEndQuery() {
        //no-op
    }

    abstract I onAsQuery();


    abstract SP createQueryUnion(UnionType unionType);

    abstract UR createRowSetUnion(UnionType unionType, S right);


    private SP unionQuery(final UnionType unionType) {
        this.endQueryStatement();
        return this.createQueryUnion(unionType);
    }

    private UR unionRowSet(final UnionType unionType, Supplier<S> supplier) {
        this.endQueryStatement();
        final S rowSet;
        rowSet = supplier.get();
        if (rowSet == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.createRowSetUnion(unionType, rowSet);
    }


    private void endQueryStatement() {
        _Assert.nonPrepared(this.prepared);
        if (this.rowSet == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.endOrderByClause();
        this.onEndQuery();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


}
