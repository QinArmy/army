package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.JoinType;
import io.army.criteria.TableAble;
import io.army.criteria.Update;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

final class StandardContextualMultiUpdate<C> extends AbstractContextualUpdate<C> implements Update.MultiUpdateAble<C>
        , Update.JoinAble<C>, Update.OnAble<C> {

    StandardContextualMultiUpdate(C criteria) {
        super(criteria);
    }

    /*################################## blow MultiUpdateAble method ##################################*/

    @Override
    public JoinAble<C> update(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(tableMeta, tableAlias, JoinType.NONE);
        return this;
    }

    /*################################## blow JoinAble method ##################################*/

    @Override
    public OnAble<C> leftJoin(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.LEFT);
        return this;
    }

    @Override
    public OnAble<C> join(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.JOIN);
        return this;
    }

    @Override
    public OnAble<C> rightJoin(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.RIGHT);
        return this;
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public JoinAble<C> on(List<IPredicate> predicateList) {
        doOn(predicateList);
        return this;
    }

    @Override
    public JoinAble<C> on(IPredicate predicate) {
        doOn(Collections.singletonList(predicate));
        return this;
    }

    @Override
    public JoinAble<C> on(Function<C, List<IPredicate>> function) {
        doOn(function.apply(this.criteria));
        return this;
    }

    /*################################## blow package template method ##################################*/

    @Override
    int tableWrapperCount() {
        return 6;
    }


    @Override
    void doAsUpdate() {

    }

}
