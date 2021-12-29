package io.army.criteria.impl;

import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.mysql._MySQL57Select;
import io.army.criteria.mysql.MySQL57Select;
import io.army.meta.TableMeta;

final class MySQL57ContextualSelect<C> extends MySQL57PartQuery<MySQL57Select, C> implements MySQL57Select
        , _MySQL57Select {

    static <C> MySQL57ContextualSelect<C> build(C criteria) {
        return new MySQL57ContextualSelect<>(criteria);
    }

    private final CriteriaContext criteriaContext;

    private MySQL57ContextualSelect(C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    final void onMySQLAddTable(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onAddTable(table, tableAlias);
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    final void internalAsSelect() {
        CriteriaContextStack.clearContextStack(this.criteriaContext);
    }


}
