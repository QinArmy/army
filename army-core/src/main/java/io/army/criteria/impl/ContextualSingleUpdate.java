package io.army.criteria.impl;

import io.army.criteria.JoinType;
import io.army.criteria.SubQuery;
import io.army.criteria.Update;
import io.army.meta.TableMeta;
import io.army.util.Assert;

final class ContextualSingleUpdate<C> extends AbstractUpdate<C> implements Update.NoJoinUpdateCommandAble<C> {

    private final CriteriaContext criteriaContext;


    ContextualSingleUpdate(C criteria) {
        super(criteria);
        this.criteriaContext = new AbstractSelect.CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow NoJoinUpdateCommandAble method ##################################*/

    @Override
    public final SetAble<C> update(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(tableMeta, tableAlias, JoinType.NONE);
        return this;
    }

    /*################################## blow package template method ##################################*/

    @Override
    final int tableWrapperCount() {
        return 1;
    }

    @Override
    final void doAsUpdate() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();

        Assert.state(tableWrapperListSize() == 1, "ContextualSingleUpdate update table count not equals 1 .");

    }

    @Override
    void doClear() {

    }

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        CriteriaContextHolder.getContext()
                .onAddSubQuery(subQuery, subQueryAlias);
    }
}
