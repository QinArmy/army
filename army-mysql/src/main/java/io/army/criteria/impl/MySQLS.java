package io.army.criteria.impl;

import io.army.criteria.EmptyObject;
import io.army.criteria.mysql.MySQL57Query;
import io.army.criteria.mysql.MySQL57Select;
import io.army.criteria.mysql.MySQL57SubQuery;

public abstract class MySQLS extends SQLS {

    /*################################## blow update  method ##################################*/
/*
    public static MySQLUpdate.MySQLSingleUpdateAble<EmptyObject> singleUpdate() {
        return null;
    }

    public static <C> MySQLUpdate.MySQLSingleUpdateAble<C> singleUpdate(C criteria) {
        return null;
    }

    public static MySQLUpdate.MySQLMultiUpdateAble<EmptyObject> multiUpdate() {
        return null;
    }

    public static <C> MySQLUpdate.MySQLMultiUpdateAble<C> multiUpdate(C criteria) {
        return null;
    }*/

    /*################################## blow delete method ##################################*/

  /*  public static MySQLDelete.MySQLSingleDeleteAble<EmptyObject> singleDelete() {
        return null;
    }

    public static <C> MySQLDelete.MySQLSingleDeleteAble<C> singleDelete(C criteria) {
        return null;
    }

    public static MySQLDelete.MySQLMultiDeleteAble<EmptyObject> multiDelete() {
        return null;
    }

    public static <C> MySQLDelete.MySQLMultiDeleteAble<C> multiDelete(C criteria) {
        return null;
    }*/

    /*################################## blow select method ##################################*/


    public static <C> MySQL57Query.MySQLSelectPartSpec<MySQL57Select, C> mySQL57Select(C criteria) {
        return MySQL57ContextualSelect.build(criteria);
    }

    public static MySQL57Query.MySQLSelectPartSpec<MySQL57Select, EmptyObject> mySQL57Select() {
        return MySQL57ContextualSelect.build(EmptyObject.getInstance());
    }

    public static <C> MySQL57SubQuery.MySQLSubQuerySelectPartAble<C> mySQL57SubQuery(C criteria) {
        return null;
    }

    public static MySQL57SubQuery.MySQLSubQuerySelectPartAble<EmptyObject> mySQL57SubQuery() {
        return null;
    }

}
