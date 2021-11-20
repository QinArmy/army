package io.army.criteria.impl;

import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._StandardSelect;
import io.army.meta.TableMeta;

final class StandardContextualMultiSelect<C> extends AbstractStandardQuery<Select, C>
        implements _StandardSelect, Select {

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
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    final void onAddTable(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onAddTable(table, tableAlias);
    }

    @Override
    final void internalAsSelect() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
    }





}
