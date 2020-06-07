package io.army.criteria.impl;

import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.InnerStandardSelect;
import io.army.meta.TableMeta;

final class StandardContextualMultiSelect<C> extends AbstractStandardSelect<C> implements InnerStandardSelect {

    static <C> StandardContextualMultiSelect<C> build(C criteria) {
        return new StandardContextualMultiSelect<>(criteria);
    }

    private final CriteriaContext criteriaContext;

    private StandardContextualMultiSelect(C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow package method ##################################*/

    @Override
    final void concreteAsSelect() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
    }

    @Override
    void concreteClear() {

    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    /*################################## blow package template method ##################################*/


    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onAddTable(table, tableAlias);
    }

}
