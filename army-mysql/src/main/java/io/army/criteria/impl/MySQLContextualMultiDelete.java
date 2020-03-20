package io.army.criteria.impl;

import io.army.criteria.MySQLDelete;
import io.army.criteria.MySQLModifier;
import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.InnerMySQLDelete;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class MySQLContextualMultiDelete<C> extends StandardContextualMultiDelete<C>
        implements InnerMySQLDelete, MySQLDelete.MySQLMultiDeleteAble<C> {

    private List<SQLModifier> sqlModifierList = new ArrayList<>(2);

    MySQLContextualMultiDelete(C criteria) {
        super(criteria);
    }


    /*################################## blow MySQLMultiDeleteAble method ##################################*/

    @Override
    public FromAble<C> delete(List<MySQLModifier> modifierList) {
        this.sqlModifierList.addAll(modifierList);
        return this;
    }

    /*################################## blow InnerMySQLDelete method ##################################*/

    @Override
    public final List<SQLModifier> modifierList() {
        Assert.state(prepared(), NOT_PREPARED_MSG);
        return this.sqlModifierList;
    }

    /*################################## blow package  method ##################################*/

    @Override
    final void doAsDelete() {
        super.doAsDelete();
        this.sqlModifierList = Collections.unmodifiableList(this.sqlModifierList);
    }

    @Override
    void doClear() {
        super.doClear();
        this.sqlModifierList = null;
    }

}
