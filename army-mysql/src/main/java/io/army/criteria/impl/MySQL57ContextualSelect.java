package io.army.criteria.impl;

import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.mysql.InnerMySQL57Select;
import io.army.criteria.mysql.MySQL57Select;
import io.army.meta.TableMeta;

final class MySQL57ContextualSelect<C> extends AbstractMySQL57Query<MySQL57Select, C> implements MySQL57Select
        , InnerMySQL57Select {

    static <C> MySQL57ContextualSelect<C> build(C criteria) {
        return new MySQL57ContextualSelect<>(criteria);
    }

    private final CriteriaContext criteriaContext;

    private MySQL57ContextualSelect(C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
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
        CriteriaContextHolder.clearContext(this.criteriaContext);
    }


}
