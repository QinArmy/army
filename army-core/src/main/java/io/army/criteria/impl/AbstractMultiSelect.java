package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

abstract class AbstractMultiSelect<C> extends AbstractSelect<C> implements
        Select.SelectPartAble<C>, Select.FromAble<C>, Select.JoinAble<C>, Select.OnAble<C> {


    AbstractMultiSelect(C criteria) {
        super((criteria));
    }


    /*################################## blow SelectPartAble method ##################################*/

    @Override
    public <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelect(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public FromAble<C> select(Distinct distinct, SelectPart selectPart) {
        doSelect(distinct, selectPart);
        return this;
    }

    @Override
    public FromAble<C> select(SelectPart selectPart) {
        doSelect((Distinct) null, selectPart);
        return this;
    }

    @Override
    public <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList) {
        doSelect(distinct, selectPartList);
        return this;
    }

    @Override
    public <S extends SelectPart> FromAble<C> select(List<S> selectPartList) {
        doSelect((Distinct) null, selectPartList);
        return this;
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public JoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.NONE));
        return this;
    }

    @Override
    public JoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.NONE));
        return this;
    }

    /*################################## blow JoinAble method ##################################*/

    @Override
    public OnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.LEFT));
        return this;
    }

    @Override
    public OnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.LEFT));
        return this;
    }

    @Override
    public OnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.JOIN));
        return this;
    }

    @Override
    public OnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.JOIN));
        return this;
    }

    @Override
    public OnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.RIGHT));
        return this;
    }

    @Override
    public OnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT));
        return this;
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public final Select.JoinAble<C> on(List<IPredicate> predicateList) {
        doOn(predicateList);
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(IPredicate predicate) {
        doOn(Collections.singletonList(predicate));
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(Function<C, List<IPredicate>> function) {
        doOn(function.apply(this.criteria));
        return this;
    }

    /*################################## blow AbstractWhereAble method ##################################*/

    @Override
    int tableWrapperCount() {
        return 6;
    }

    @Override
    protected final void doAsSelect() {
        afterDoAsSelect();
    }

    /*################################## blow package method ##################################*/



    /*################################## blow package template method ##################################*/

    abstract void afterDoAsSelect();

    /*################################## blow private method ##################################*/


}
