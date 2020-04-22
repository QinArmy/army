package io.army.criteria.impl;

import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.InnerStandardSelect;
import io.army.meta.TableMeta;

class StandardContextualMultiSelect<C> extends AbstractStandardSelect<C> implements InnerStandardSelect {

    private final CriteriaContext criteriaContext;

    StandardContextualMultiSelect(C criteria) {
        super(criteria);

        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }


    /*################################## blow package method ##################################*/

    @Override
    final void doAsSelect() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();
    }


    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    /*################################## blow package template method ##################################*/


    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }

}
