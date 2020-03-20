package io.army.criteria.impl;

import io.army.criteria.MySQLModifier;
import io.army.criteria.MySQLUpdate;
import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.InnerMySQLUpdate;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.List;

final class MySQLContextualMultiUpdate<C> extends StandardContextualMultiUpdate<C>
        implements MySQLUpdate.MySQLMultiUpdateAble<C>, InnerMySQLUpdate {

    private List<SQLModifier> sqlModifierList = new ArrayList<>(2);

    MySQLContextualMultiUpdate(C criteria) {
        super(criteria);
    }

    /*################################## blow MySQLMultiUpdateAble method ##################################*/

    @Override
    public JoinAble<C> update(List<MySQLModifier> modifierList, TableMeta<?> tableMeta, String tableAlias) {
        this.sqlModifierList.addAll(modifierList);
        super.update(tableMeta, tableAlias);
        return this;
    }

    /*################################## blow InnerUpdate method ##################################*/

    @Override
    public final List<SQLModifier> modifierList() {
        Assert.state(prepared(), NOT_PREPARED_MSG);
        return this.sqlModifierList;
    }

}
