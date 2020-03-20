package io.army.criteria.impl;

import io.army.criteria.JoinType;
import io.army.criteria.Update;
import io.army.meta.TableMeta;
import io.army.util.Assert;

 class StandardContextualSingleUpdate<C> extends AbstractContextualUpdate<C> implements Update.SingleUpdateAble<C> {

     StandardContextualSingleUpdate(C criteria) {
         super(criteria);
     }

     /*################################## blow NoJoinUpdateCommandAble method ##################################*/

     @Override
     public SetAble<C> update(TableMeta<?> tableMeta, String tableAlias) {
         addTableAble(tableMeta, tableAlias, JoinType.NONE);
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

         afterDoAsUpdate();
     }

     /*################################## blow package template method ##################################*/

     void afterDoAsUpdate() {

     }

 }

