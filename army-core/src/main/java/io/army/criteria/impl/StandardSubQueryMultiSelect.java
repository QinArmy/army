package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

final class StandardSubQueryMultiSelect<C> extends AbstractStandardSelect<C> implements SubQuerySelect<C> {


    private Map<String, Selection> selectionMap;

    StandardSubQueryMultiSelect(C criteria) {
        super(criteria);
    }


    @Override
    public C criteria() {
        return this.criteria;
    }

    @Override
    public String toString() {
        return "#SubQuery@" + System.identityHashCode(this);
    }

    @Override
    public Selection selection(String derivedFieldName) {
        if (this.selectionMap == null) {
            this.selectionMap = CriteriaUtils.createSelectionMap(selectPartList());
        }
        Selection s = this.selectionMap.get(derivedFieldName);
        if (s == null) {
            throw new CriteriaException(ErrorCode.NO_SELECTION
                    , "not found Selection[%s] from SubQuery.", derivedFieldName);
        }
        return s;
    }


    @Override
    public void appendSQL(SQLContext context) {
        context.dql().subQuery(this, context);
    }

    @Override
    public final SelectAble lock(LockMode lockMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SelectAble ifLock(Function<C, LockMode> function) {
        throw new UnsupportedOperationException();
    }


    @Override
    final void onAddTable(TableMeta<?> table, String tableAlias) {
        CriteriaContextHolder.getContext()
                .onAddTable(table, tableAlias);
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        CriteriaContextHolder.getContext()
                .onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    public final UnionAble<C> brackets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final <S extends Select> UnionAble<C> union(Function<C, S> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final <S extends Select> UnionAble<C> unionAll(Function<C, S> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function) {
        throw new UnsupportedOperationException();
    }

    /*################################## blow package method ##################################*/

    @Override
    final void internalAsSelect() {
        if (this.selectionMap == null) {
            this.selectionMap = Collections.emptyMap();
        } else {
            this.selectionMap = Collections.unmodifiableMap(this.selectionMap);
        }
    }

    @Override
    final void internalClear() {
        this.selectionMap = null;
    }

    @Override
    final boolean hasLockClause() {
        return false;
    }
}
