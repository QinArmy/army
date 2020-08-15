package io.army.criteria.impl;

import io.army.criteria.LockMode;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.InnerStandardSelect;
import io.army.meta.TableMeta;

import java.util.function.Function;

final class StandardContextualMultiSelect<C> extends AbstractStandardSelect<C> implements InnerStandardSelect {

    static <C> StandardContextualMultiSelect<C> build(C criteria) {
        return new StandardContextualMultiSelect<>(criteria);
    }

    private final CriteriaContext criteriaContext;

    private LockMode lockMode;

    private StandardContextualMultiSelect(C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }


    /*################################## blow LockAble method ##################################*/

    @Override
    public final UnionClause<C> lock(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final UnionClause<C> ifLock(Function<C, LockMode> function) {
        this.lockMode = function.apply(this.criteria);
        return this;
    }

    /*################################## blow UnionAble method ##################################*/

    @Override
    public final UnionAble<C> brackets() {
        return ComposeSelects.brackets(this.criteria, thisSelect());
    }

    @Override
    public final <S extends Select> UnionAble<C> union(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionAll(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_DISTINCT, function);
    }


    /*################################## blow package method ##################################*/


    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onAddTable(table, tableAlias);
    }

    @Override
    final void internalAsSelect() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
    }

    @Override
    final void internalClear() {
        this.lockMode = null;
    }

    @Override
    final boolean hasLockClause() {
        return this.lockMode != null;
    }

    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }


    /*################################## blow private method ##################################*/


}
