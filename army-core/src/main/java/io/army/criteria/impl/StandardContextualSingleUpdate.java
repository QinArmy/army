package io.army.criteria.impl;

import io.army.criteria.Update;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;

class StandardContextualSingleUpdate<T extends IDomain, C> extends AbstractContextualUpdate<T, C>
        implements Update.SingleUpdateAble<T, C> {

    StandardContextualSingleUpdate(TableMeta<T> tableMeta, C criteria) {
        super(criteria);
    }

    @Override
    public final SetAble<T, C> update(TableMeta<T> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.NONE));
        return this;
    }

     /*################################## blow package  method ##################################*/

    @Override
    final int tableWrapperCount() {
        return 1;
    }

     @Override
     final void doAsUpdate() {
         Assert.state(tableWrapperListSize() == 1, "ContextualSingleUpdate update table count not equals 1 .");
     }



 }

