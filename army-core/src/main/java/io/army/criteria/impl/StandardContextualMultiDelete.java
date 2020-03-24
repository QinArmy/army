package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.TableAble;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

class StandardContextualMultiDelete<C> extends AbstractContextualDelete<C>
        implements Delete.FromAble<C>, Delete.JoinAble<C>, Delete.OnAble<C>
        , Delete.MultiDeleteAble<C> {

    StandardContextualMultiDelete(C criteria) {
        super(criteria);
    }

    /*################################## blow MultiDeleteAble method ##################################*/

    @Override
    public final FromAble<C> delete() {
        return this;
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public final JoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(tableMeta, tableAlias, JoinType.NONE);
        return this;
    }

    /*################################## blow JoinAble method ##################################*/

    @Override
    public final OnAble<C> leftJoin(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.LEFT);
        return this;
    }

    @Override
    public final OnAble<C> join(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.JOIN);
        return this;
    }

    @Override
    public final OnAble<C> rightJoin(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.RIGHT);
        return this;
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public final JoinAble<C> on(List<IPredicate> predicateList) {
        doOn(predicateList);
        return this;
    }

    @Override
    public final JoinAble<C> on(IPredicate predicate) {
        doOn(Collections.singletonList(predicate));
        return this;
    }

    @Override
    public final JoinAble<C> on(Function<C, List<IPredicate>> function) {
        doOn(function.apply(this.criteria));
        return this;
    }

    /*################################## blow package method ##################################*/

    @Override
    int tableWrapperCount() {
        return 6;
    }

}
