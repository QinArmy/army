package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

 class StandardContextualSingleSelect<C> extends AbstractSelect<C> implements
         Select.SelectionAble<C>, Select.NoJoinFromAble<C> {

     private final CriteriaContext criteriaContext;


     StandardContextualSingleSelect(C criteria) {
         super(criteria);
         this.criteriaContext = new CriteriaContextImpl<>(criteria);
         CriteriaContextHolder.setContext(this.criteriaContext);
     }


    /*################################## blow SelectionAble method ##################################*/

     @Override
     public final NoJoinFromAble<C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta) {
         doSelect(distinct, SQLS.group(tableMeta, tableAlias));
         return this;
     }

     @Override
     public final NoJoinFromAble<C> select(String tableAlias, TableMeta<?> tableMeta) {
         doSelect((Distinct) null, SQLS.group(tableMeta, tableAlias));
         return this;
     }

     @Override
     public final NoJoinFromAble<C> select(Distinct distinct, String subQueryAlias) {
         doSelect(distinct, SQLS.derivedGroup(subQueryAlias));
         return this;
     }

     @Override
     public final NoJoinFromAble<C> select(String subQueryAlias) {
         doSelect((Distinct) null, SQLS.derivedGroup(subQueryAlias));
         return this;
     }

     @Override
     public final NoJoinFromAble<C> select(Distinct distinct, List<Selection> selectionList) {
         doSelect(distinct, new ArrayList<>(selectionList));
         return this;
     }

     @Override
     public final NoJoinFromAble<C> select(List<Selection> selectionList) {
         doSelect(Collections.emptyList(), selectionList);
         return this;
     }

     @Override
     public final NoJoinFromAble<C> select(Distinct distinct, Selection selection) {
         doSelect(Collections.singletonList(distinct), Collections.singletonList(selection));
         return this;
     }

     @Override
     public final NoJoinFromAble<C> select(Selection selection) {
         doSelect(Collections.emptyList(), Collections.singletonList(selection));
         return this;
     }

    /*################################## blow NoJoinFromAble method ##################################*/

     @Override
     public final WhereAble<C> from(TableAble tableAble, String tableAlias) {
         addTableAble(tableAble, tableAlias, JoinType.NONE);
         return this;
     }

     /*################################## blow  package method ##################################*/

     @Override
     final int tableWrapperCount() {
         return 1;
     }

     @Override
     final void doAsSelect() {
         CriteriaContextHolder.clearContext(this.criteriaContext);
         this.criteriaContext.clear();

         Assert.state(tableWrapperListSize() < 2
                 , "tableWrapperList.size() isn't 1,ContextualSingleSelect state error.");

     }

     /*################################## blow package template method ##################################*/

     @Override
     void onAddTable(TableMeta<?> table, String tableAlias) {

     }


     @Override
     final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
         this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
     }

    @Override
    void doClear() {

    }



}
