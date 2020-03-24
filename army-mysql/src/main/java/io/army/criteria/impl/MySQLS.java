package io.army.criteria.impl;

import io.army.criteria.EmptyObject;
import io.army.criteria.MySQLDelete;
import io.army.criteria.MySQLSelect;
import io.army.criteria.MySQLUpdate;

public abstract class MySQLS extends SQLS {

    /*################################## blow update  method ##################################*/

    public static MySQLUpdate.MySQLSingleUpdateAble<EmptyObject> singleUpdate() {
        return new MySQLContextualSingleUpdate<>(EmptyObject.getInstance());
    }

    public static <C> MySQLUpdate.MySQLSingleUpdateAble<C> singleUpdate(C criteria) {
        return new MySQLContextualSingleUpdate<>(criteria);
    }

    public static MySQLUpdate.MySQLMultiUpdateAble<EmptyObject> multiUpdate() {
        return new MySQLContextualMultiUpdate<>(EmptyObject.getInstance());
    }

    public static <C> MySQLUpdate.MySQLMultiUpdateAble<C> multiUpdate(C criteria) {
        return new MySQLContextualMultiUpdate<>(criteria);
    }

    /*################################## blow delete method ##################################*/

    public static MySQLDelete.MySQLSingleDeleteAble<EmptyObject> singleDelete() {
        return new MySQLContextualSingleDelete<>(EmptyObject.getInstance());
    }

    public static <C> MySQLDelete.MySQLSingleDeleteAble<C> singleDelete(C criteria) {
        return new MySQLContextualSingleDelete<>(criteria);
    }

    public static MySQLDelete.MySQLMultiDeleteAble<EmptyObject> multiDelete() {
        return new MySQLContextualMultiDelete<>(EmptyObject.getInstance());
    }

    public static <C> MySQLDelete.MySQLMultiDeleteAble<C> multiDelete(C criteria) {
        return new MySQLContextualMultiDelete<>(criteria);
    }

    /*################################## blow select method ##################################*/


    public static MySQLSelect.MySQLNoJoinSelectAble<EmptyObject> singleSelect() {
        return new MySQLContextualSingleSelect<>(EmptyObject.getInstance());
    }

    public static <C> MySQLSelect.MySQLNoJoinSelectAble<C> singleSelect(C criteria) {
        return new MySQLContextualSingleSelect<>(criteria);
    }

    public static MySQLSelect.SelectPartAble<EmptyObject> multiSelect() {
        return new MySQLContextualMultiSelect<>(EmptyObject.getInstance());
    }

    public static <C> MySQLSelect.SelectPartAble<C> multiSelect(C criteria) {
        return new MySQLContextualMultiSelect<>(criteria);
    }


}
