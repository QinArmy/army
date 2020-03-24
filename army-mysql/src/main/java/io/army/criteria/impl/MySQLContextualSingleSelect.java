package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerMySQLSelect;
import io.army.util.Assert;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class MySQLContextualSingleSelect<C> extends StandardContextualSingleSelect<C>
        implements InnerMySQLSelect, MySQLSelect.MySQLNoJoinSelectAble<C>
        , MySQLSelect.MySQLGroupByAble<C>, MySQLSelect.MySQLHavingAble<C> {

    private boolean withRollUp;

    MySQLContextualSingleSelect(C criteria) {
        super(criteria);
    }

    /*################################## blow ModifierSelectionAble method ##################################*/

    @Override
    public <S extends SelectPart> NoJoinFromAble<C> select(Function<C, List<MySQLModifier>> modifierFunction
            , Function<C, List<S>> selectPartFunction) {
        this.doSelect(modifierFunction.apply(this.criteria), selectPartFunction.apply(this.criteria));
        return this;
    }

    @Override
    public NoJoinFromAble<C> select(List<MySQLModifier> modifierList, SelectPart selectPart) {
        doSelect(modifierList, selectPart);
        return this;
    }

    @Override
    public <S extends SelectPart> NoJoinFromAble<C> select(List<MySQLModifier> modifierList, List<S> selectPartList) {
        doSelect(modifierList, selectPartList);
        return this;
    }


    /*################################## blow MySQLGroupByAble method ##################################*/

    @Override
    public MySQLHavingAble<C> groupByAndRollUp(Expression<?> groupExp) {
        super.groupBy(groupExp);
        this.withRollUp = true;
        return this;
    }

    @Override
    public MySQLHavingAble<C> groupByAndRollUp(Function<C, List<Expression<?>>> function) {
        super.groupBy(function);
        this.withRollUp = true;
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
