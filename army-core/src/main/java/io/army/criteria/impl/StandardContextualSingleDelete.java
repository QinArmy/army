package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.meta.TableMeta;
import io.army.util.Assert;

class StandardContextualSingleDelete<C> extends AbstractContextualDelete<C>
        implements Delete.NoJoinFromAble<C>, Delete.SingleDeleteAble<C> {

    StandardContextualSingleDelete(C criteria) {
        super(criteria);
    }

    /*################################## blow SingleDeleteAble method ##################################*/

    @Override
    public NoJoinFromAble<C> delete() {
        return this;
    }

    /*################################## blow NoJoinFromAble method ##################################*/

    @Override
    public WhereAble<C> from(TableMeta<?> tableMeta) {
        addTableAble(new TableWrapperImpl(tableMeta, "", JoinType.NONE));
        return this;
    }

    /*################################## blow package  method ##################################*/

    @Override
    final void doAsDelete() {
        Assert.state(tableWrapperListSize() == 1, "single singleDelete table count not equals 1 .");
        afterDoAsDelete();
    }

    @Override
    final int tableWrapperCount() {
        return 1;
    }

    void afterDoAsDelete() {

    }
}
