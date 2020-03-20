package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerMySQLSelect;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class MySQLContextualSingleSelect<C> extends StandardContextualSingleSelect<C>
        implements InnerMySQLSelect, MySQLSelect.ModifierSelectionAble<C>
        , MySQLSelect.MySQLGroupByAble<C>, MySQLSelect.MySQLHavingAble<C> {

    private boolean withRollUp;

    MySQLContextualSingleSelect(C criteria) {
        super(criteria);
    }

    /*################################## blow ModifierSelectionAble method ##################################*/

    @Override
    public NoJoinFromAble<C> select(List<MySQLModifier> modifierList, String tableAlias, TableMeta<?> tableMeta) {
        doSelect(modifierList, SQLS.group(tableMeta, tableAlias));
        return this;
    }

    @Override
    public NoJoinFromAble<C> select(List<MySQLModifier> modifierList, String subQueryAlias) {
        doSelect(modifierList, SQLS.derivedGroup(subQueryAlias));
        return this;
    }

    @Override
    public NoJoinFromAble<C> select(List<MySQLModifier> modifierList, List<Selection> selectionList) {
        doSelect(modifierList, selectionList);
        return this;
    }

    @Override
    public NoJoinFromAble<C> select(List<MySQLModifier> modifierList, Selection selection) {
        doSelect(modifierList, Collections.singletonList(selection));
        return this;
    }


    /*################################## blow MySQLGroupByAble method ##################################*/

    @Override
    public MySQLHavingAble<C> groupBy(Expression<?> groupExp, boolean withRollUp) {
        super.groupBy(groupExp);
        this.withRollUp = withRollUp;
        return this;
    }

    @Override
    public MySQLHavingAble<C> groupBy(Function<C, List<Expression<?>>> function, boolean withRollUp) {
        super.groupBy(function);
        this.withRollUp = withRollUp;
        return this;
    }

    @Override
    public MySQLHavingAble<C> ifGroupByAndRollUp(Predicate<C> predicate, Function<C, MySQLGroup> expFunction) {
        if (predicate.test(this.criteria)) {
            MySQLGroup mySQLGroup = expFunction.apply(this.criteria);
            super.groupBy(mySQLGroup.groupExpList());
            this.withRollUp = mySQLGroup.withRollUp();
        }
        return this;
    }


    /*################################## blow InnerMySQLSelect method ##################################*/

    @Override
    public final boolean withRollUp() {
        Assert.state(prepared(), NOT_PREPARED_MSG);
        return this.withRollUp;
    }

    /*################################## blow package method ##################################*/


}
