package io.army.criteria.impl;

import io.army.criteria.SubQuery;
import io.army.meta.TableMeta;

final class StandardContextualMultiSelect<C> extends AbstractMultiSelect<C> {

    private final CriteriaContext criteriaContext;

    StandardContextualMultiSelect(C criteria) {
        super(criteria);

        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }


    /*################################## blow package template method ##################################*/


    @Override
    void afterDoAsSelect() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();
    }

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    void doClear() {

    }

    /*################################## blow private method ##################################*/


}
