package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

abstract class AbstractMultiSelect<C> extends AbstractSelect<C> implements
        Select.SelectPartAble<C>, Select.FromAble<C>, Select.JoinAble<C>, Select.OnAble<C> {


    AbstractMultiSelect(C criteria) {
        super((criteria));
    }


    /*################################## blow SelectPartAble method ##################################*/


    @Override
    public final Select.FromAble<C> select(Distinct distinct, List<SelectPart> selectPartList) {
        doSelect(distinct, selectPartList);
        return this;
    }

    @Override
    public final Select.FromAble<C> select(List<SelectPart> selectPartList) {
        doSelect((Distinct) null, selectPartList);
        return this;
    }

    @Override
    public final Select.FromAble<C> select(Distinct distinct, Function<C, List<SelectPart>> function) {
        doSelect(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.FromAble<C> select(Function<C, List<SelectPart>> function) {
        doSelect((Distinct) null, function.apply(this.criteria));
        return this;
    }

    /*################################## blow SelectionGroupAble method ##################################*/

    @Override
    public final FromAble<C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta) {
        doSelect(distinct, SQLS.group(tableMeta, tableAlias));
        return this;
    }

    @Override
    public final FromAble<C> select(String tableAlias, TableMeta<?> tableMeta) {
        doSelect((Distinct) null, SQLS.group(tableMeta, tableAlias));
        return this;
    }

    @Override
    public final FromAble<C> select(String subQueryAlias) {
        doSelect((Distinct) null, SQLS.derivedGroup(subQueryAlias));
        return this;
    }

    @Override
    public final FromAble<C> select(Distinct distinct, String subQueryAlias) {
        doSelect(distinct, SQLS.derivedGroup(subQueryAlias));
        return this;
    }


    /*################################## blow FromAble method ##################################*/

    @Override
    public final Select.JoinAble<C> from(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.NONE);
        return this;
    }

    /*################################## blow JoinAble method ##################################*/

    @Override
    public final Select.OnAble<C> leftJoin(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.LEFT);
        return this;
    }

    @Override
    public final Select.OnAble<C> join(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.JOIN);
        return this;
    }

    @Override
    public final Select.OnAble<C> rightJoin(TableAble tableAble, String tableAlias) {
        addTableAble(tableAble, tableAlias, JoinType.RIGHT);
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

    /**
     * invoke after {@link #asSelect()}
     *
     * @return a unmodifiable map
     */
    final Map<String, Selection> createSelectionMap() {
        Assert.state(prepared(), "select no prepared,state error.");

        Map<String, Selection> selectionMap = new HashMap<>();
        for (SelectPart selectPart : this.selectPartList()) {

            if (selectPart instanceof Selection) {
                Selection selection = (Selection) selectPart;
                if (selectionMap.putIfAbsent(selection.alias(), selection) != selection) {
                    throw new CriteriaException(ErrorCode.SELECTION_DUPLICATION, "selection[%s] duplication"
                            , selection);
                }
            } else if (selectPart instanceof SelectionGroup) {
                SelectionGroup group = (SelectionGroup) selectPart;
                String tableAlias = group.tableAlias();
                for (Selection selection : group.selectionList()) {
                    if (selectionMap.putIfAbsent(tableAlias, selection) != selection) {
                        throw new CriteriaException(ErrorCode.SELECTION_DUPLICATION, "selection[%s] duplication"
                                , selection);
                    }
                }
            }

        }
        return Collections.unmodifiableMap(selectionMap);
    }

    /*################################## blow package template method ##################################*/

    abstract void afterDoAsSelect();

    /*################################## blow private method ##################################*/


}
