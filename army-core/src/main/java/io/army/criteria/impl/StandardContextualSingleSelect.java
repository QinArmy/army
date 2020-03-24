package io.army.criteria.impl;

import io.army.criteria.Distinct;
import io.army.criteria.Select;
import io.army.criteria.SelectPart;
import io.army.criteria.SubQuery;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.List;
import java.util.function.Function;

class StandardContextualSingleSelect<C> extends AbstractSelect<C> implements
        Select.NoJoinSelectPartAble<C>, Select.NoJoinFromAble<C> {

    private final CriteriaContext criteriaContext;


    StandardContextualSingleSelect(C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }


    /*################################## blow SelectionAble method ##################################*/

    @Override
    public <S extends SelectPart> NoJoinFromAble<C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelect(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public NoJoinFromAble<C> select(Distinct distinct, SelectPart selectPart) {
        doSelect(distinct, selectPart);
        return this;
    }

    @Override
    public NoJoinFromAble<C> select(SelectPart selectPart) {
        doSelect((Distinct) null, selectPart);
        return this;
    }

    @Override
    public <S extends SelectPart> NoJoinFromAble<C> select(Distinct distinct, List<S> selectPartList) {
        doSelect(distinct, selectPartList);
        return this;
    }

    @Override
    public <S extends SelectPart> NoJoinFromAble<C> select(List<S> selectPartList) {
        doSelect((Distinct) null, selectPartList);
        return this;
    }

    /*################################## blow NoJoinFromAble method ##################################*/

    @Override
    public WhereAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(tableMeta, tableAlias, JoinType.NONE);
        return this;
    }

    @Override
    public WhereAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(function.apply(this.criteria), subQueryAlia, JoinType.NONE);
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
